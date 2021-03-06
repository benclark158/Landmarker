package com.landmarker3;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.telecom.Call;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TensorflowImageModule extends ReactContextBaseJavaModule {

    Interpreter tflite;
    String currentModel;
    Context context;

    TensorBuffer outputProbabilityBuffer;
    TensorImage ti;
    TensorProcessor probabilityProcesser;
    List<String> labels;

    HashMap<String, LatLng> gps;

    public TensorflowImageModule(Context context){
        this.context = context;
        currentModel = "";

        this.labels = Values.makeLabels();
        this.gps = Values.getCoords();
    }

    @NonNull
    @Override
    public String getName() {
        return "TensorflowImage";
    }

    /**
     * Classifies an image and gps coords as a label. Images can also be unknown
     *
     * @param model model name to load
     * @param path android path to image
     * @param latitude latitude coord
     * @param longitude longitude coord
     * @param accuracy accuracy in meters, greater than 0
     * @param errorCallback callback called when the function errors 1 param
     * @param successCallback callback called when completed 4params
     */
    @ReactMethod
    public void classify(String model, String path, float latitude, float longitude, float accuracy, Callback errorCallback, Callback successCallback) {
        try {

            //load tflite model
            this.tflite = new Interpreter(loadModelFile(this.context, model));

            //Prep gps data
            float[][] gps = new float[1][2];
            gps[0][0] = latitude;
            gps[0][1] = longitude;

            //prep image
            Bitmap bitmap = this.getResizedBitmap(
                    this.loadImage(path), 224, 224);

            this.ti = new TensorImage(this.tflite.getInputTensor(1).dataType());

            //load inputs
            Object[] inputs = {gps, this.loadTensor(bitmap).getBuffer()};

            //Prepare output objects
            int[] probabilityShape = tflite.getOutputTensor(0).shape(); // {1, NUM_CLASSES}
            DataType probabilityDataType = tflite.getOutputTensor(0).dataType();
            this.probabilityProcesser = new TensorProcessor.Builder().add(this.getPostProcessNormaliseOp()).build();
            this.outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

            //add outputs to map
            Map<Integer, Object> outputs = new HashMap<>();
            outputs.put(0, this.outputProbabilityBuffer.getBuffer().rewind());

            //compute from model
            this.tflite.runForMultipleInputsOutputs(inputs, outputs);

            //get labeled outputs
            Map<String, Float> labeledProbability = new TensorLabel(labels, this.probabilityProcesser.process(outputProbabilityBuffer))
                            .getMapWithFloatValue();

            String str = "";

            //get closest landmarks
            Map<String, Float> distances = this.getDistances(new LatLng(latitude, longitude), accuracy);

            if(distances.size() == 0){
                //no close landmarks hence no landmark
                successCallback.invoke("Unknown Landmark", Values.UNKNOWN_INFORMATION, false, null);
                return;
            }

            // weight landmarks based on their place in the sorted distance map
            float maxDistance = Collections.max(distances.values()) * 2.0f;

            Map<String, Float> newProbs = new HashMap<>();

            for(String name : distances.keySet()){
                float multi = maxDistance - distances.get(name);
                float newProb = multi * labeledProbability.get(name);
                newProbs.put(name, newProb);
            }

            //sorted by weights
            Map<String, Float> sortedProbs = this.sortByComparator(newProbs, false);

            //debug code
            str = "\n\nResult: [" + sortedProbs.keySet().toArray()[0] + "] \n\n\nAltProbs: \n";

            for(String name : newProbs.keySet()){
                str += name + ": " + newProbs.get(name) + ", \n";
            }

            str += "] \n\n\nProbs: \n";

            for(String name : labeledProbability.keySet()){
                str += name + ": " + labeledProbability.get(name) + ", \n";
            }

            //end of debug code

            //get final outputs for callback
            String outputName = (String) sortedProbs.keySet().toArray()[0];
            boolean hasAddition = additional(outputName);
            String info = getInfo(outputName);
            String probs = labeledProbability.get(outputName) + ":" + newProbs.get(outputName);

            //is the probability is zero then unknown
            if(newProbs.get(outputName) > 0.0) {
                successCallback.invoke(outputName, info, hasAddition, probs + str);
            } else {
                successCallback.invoke("Unknown Landmark", Values.UNKNOWN_INFORMATION, false, outputName + "-" + probs + str);
            }
        } catch (Exception e) {
            //runs error callback if error in classifaction occured
            e.printStackTrace();
            errorCallback.invoke("Classify: "  + e.getMessage());
        }
    }

    /**
     * Get the information about a specific landmark
     * error callback invoked if the landmark is not a label
     * @param landmark landmark to get info of
     * @param success invoked with results 4 params
     * @param err called when errored 1param
     */
    @ReactMethod
    public void getInfo(String landmark, Callback success, Callback err){
        try{
            if(landmark.equals("")){
                throw new IllegalArgumentException("Landmark cannot be empty");
            } else if(!this.labels.contains(landmark)){
                throw new IllegalArgumentException("\'" + landmark + "\' is not a recognised landmark");
            }

            landmark = landmark.replace(" ", "_");

            boolean hasAdditional = additional(landmark);
            String info = getInfo(landmark);

            success.invoke(landmark, info, hasAdditional, "");
        } catch (Exception e){
            err.invoke("GetInfo error: " + e.getMessage());
        }
    }

    /**
     * get all gps positions
     * @param success 1param string json
     * @param err 1param error message
     */
    @ReactMethod
    public void getLandmarkGPS(Callback success, Callback err){
        try {
            Map<String, LatLng> points = Values.getCoords();

            JSONArray arr = new JSONArray();

            for (String name : points.keySet()) {
                JSONObject obj = new JSONObject();
                obj.put("name", name);
                obj.put("latitude", points.get(name).latitude);
                obj.put("longitude", points.get(name).longitude);
                arr.put(obj);
            }
            success.invoke(arr.toString());
        } catch (Exception e){
            err.invoke("GetLandmarkGPS error: " + e.getMessage());
        }
    }

    /**
     * Gets the string info about a location, usually description
     * @param outputName name of location
     * @return string info
     */
    public String getInfo(String outputName) {
        String str = Values.getInfo().get(outputName);

        if(str == null){
            return "";
        }
        return str;
    }

    /**
     * Checks if landmark has additional data from website
     * @param outputName landmark to check
     * @return true if it has additional data, false if not
     */
    public boolean additional(String outputName) {
        Object val = Values.getUnavailableWebsites().get(outputName);
        if(val == null){
            return true;
        }
        if((boolean) val){
            return false;
        }

        return false;
    }

    /**
     * Gets distances between current place and all other landmarks in the dataset
     * Only returns places that are within (15000 + accuracy) meters
     * @param currentPos latlng object for current position
     * @param accuracy accuracy of current position in meters
     * @return map of places and their distances
     */
    public Map<String, Float> getDistances(LatLng currentPos, float accuracy){
        if(accuracy < 0){
            throw new IllegalArgumentException("Accuracy must be greater than 0");
        }

        HashMap<String, Float> distanceMap = new HashMap<>();

        //filter distances tht are too far away
        for(String name : this.gps.keySet()){
            //Distance in meters
            float distance = (float) SphericalUtil.computeDistanceBetween(this.gps.get(name), currentPos);
            if(distance < 15000 +  accuracy) {
                distanceMap.put(name, distance);
            }
        }
        return distanceMap; //this.sortByComparator(distanceMap, true);
    }

    /**
     * Sorts string float hashmap
     * @param unsortMap map to sort
     * @param order order to sort in, assending or descending
     * @return sorted map
     */
    public Map<String, Float> sortByComparator(Map<String, Float> unsortMap, final boolean order) {
        List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * Loads the tflite model
     * Memory-map the model file in Assets.
     * @param cxt context (android)
     * @param model model name to load
     * @return loaded file
     * @throws IOException if file is not found
     */
    public MappedByteBuffer loadModelFile(Context cxt, String model) throws IOException {
        AssetFileDescriptor fileDescriptor = cxt.getAssets().openFd(model);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /**
     * Converts image to tensorimage with normalisation
     * @param bitmap image to convert
     * @return tensorimage
     */
    public TensorImage loadTensor(Bitmap bitmap){
        this.ti.load(bitmap);

        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(getPreProcessNormalisationOp())
                .build();
        return imageProcessor.process(this.ti);
    }

    /**
     * Getnormalisation process for image
     * @return normalisation opperator
     */
    public TensorOperator getPreProcessNormalisationOp() {
        return new NormalizeOp(127.5f, 127.5f);
    }

    /**
     * Get post operator for classification out
     * @return operator for output from CNN
     */
    public TensorOperator getPostProcessNormaliseOp() {
        return new NormalizeOp(0.0f, 1.0f);
    }

    /**
     * Loads image from path
     * @param path path of image
     * @return bitmap image
     * @throws IOException if image cannot be found
     */
    public Bitmap loadImage(String path) throws IOException {

        URLConnection conn = new URL(path).openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is, 8192);
        Bitmap bm = BitmapFactory.decodeStream(bis);

        return bm;
    }

    /**
     * Resizes a bitmap image to the size defined
     * @param bm image to resize
     * @param newWidth width of new image
     * @param newHeight height of new image
     * @return bitmap image that has been resized
     */
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        if(newWidth <= 0 || newHeight <= 0){
            throw new IllegalArgumentException("newWidth and newHeight must be greater than 0");
        }

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
