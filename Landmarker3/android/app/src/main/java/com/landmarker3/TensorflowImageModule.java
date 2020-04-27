package com.landmarker3;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

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

    @ReactMethod
    public void classify(String model, String path, float latitude, float longitude, float accuracy, Callback errorCallback, Callback successCallback) {
        try {

            this.tflite = new Interpreter(loadModelFile(this.context, model));

            //this.tflite.;

            float[][] gps = new float[1][2];
            gps[0][0] = latitude;
            gps[0][1] = longitude;

            //path = "https://i2-prod.nottinghampost.com/incoming/article3128479.ece/ALTERNATES/s615/0_JAJ_TEM_090519UoN_001JPG.jpg";
            //path = "https://upload.wikimedia.org/wikipedia/commons/8/8e/Bolingbroke_Castle%2C_Old_Bolingbroke_-_geograph.org.uk_-_1455687.jpg";

            Bitmap bitmap = this.getResizedBitmap(
                    this.loadImage(path), 224, 224);

            this.ti = new TensorImage(this.tflite.getInputTensor(1).dataType());

            int[] probabilityShape = tflite.getOutputTensor(0).shape(); // {1, NUM_CLASSES}
            DataType probabilityDataType = tflite.getOutputTensor(0).dataType();
            this.probabilityProcesser = new TensorProcessor.Builder().add(this.getPostProcessNormaliseOp()).build();

            Object[] inputs = {gps, this.loadTensor(bitmap).getBuffer()};

            this.outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

            Map<Integer, Object> outputs = new HashMap<>();
            outputs.put(0, this.outputProbabilityBuffer.getBuffer().rewind());

            this.tflite.runForMultipleInputsOutputs(inputs, outputs);

            //Finally some outputs!
            Map<String, Float> labeledProbability = new TensorLabel(labels, this.probabilityProcesser.process(outputProbabilityBuffer))
                            .getMapWithFloatValue();

            String str = "";

            Map<String, Float> distances = this.getDistances(new LatLng(latitude, longitude), accuracy);

            if(distances.size() == 0){
                successCallback.invoke("Unknown Landmark", Values.UNKNOWN_INFORMATION, false, null);
                return;
            }

            float maxDistance = Collections.max(distances.values());

            Map<String, Float> newProbs = new HashMap<>();

            for(String name : distances.keySet()){
                float multi = maxDistance - distances.get(name);
                float newProb = multi * labeledProbability.get(name);
                newProbs.put(name, newProb);
            }

            Map<String, Float> sortedProbs = this.sortByComparator(newProbs, false);

            //Map<String, Float> sortedProbs = this.sortByComparator(closestProbs, false);

            str = "\n\nResult: [" + sortedProbs.keySet().toArray()[0] + "] \n\n\nAltProbs: \n";

            for(String name : newProbs.keySet()){
                str += name + ": " + newProbs.get(name) + ", \n";
            }

            str += "] \n\n\nProbs: \n";

            for(String name : labeledProbability.keySet()){
                str += name + ": " + labeledProbability.get(name) + ", \n";
            }

            String outputName = (String) sortedProbs.keySet().toArray()[0];
            boolean hasAddition = additional(outputName);
            String info = getInfo(outputName);
            String probs = labeledProbability.get(outputName) + ":" + newProbs.get(outputName);

            if(newProbs.get(outputName) > 0.0) {
                successCallback.invoke(outputName, info, hasAddition, probs + str);
            } else {
                successCallback.invoke("Unknown Landmark", Values.UNKNOWN_INFORMATION, false, outputName + "-" + probs + str);
            }


            //successCallback.invoke("Correct", str, true);

        } catch (Exception e) {
            e.printStackTrace();
            errorCallback.invoke("error: "  + e.getMessage());
        }
    }

    private String getInfo(String outputName) {
        String str = Values.getInfo().get(outputName);

        if(str == null){
            return "";
        }
        return str;
    }

    private boolean additional(String outputName) {
        Object val = Values.getUnavailableWebsites().get(outputName);
        if(val == null){
            return true;
        }
        if(val instanceof Boolean && (boolean) val){
            return (boolean) val;
        }

        return false;
    }

    private Map<String, Float> getDistances(LatLng currentPos, float accuracy){
        HashMap<String, Float> distanceMap = new HashMap<>();

        for(String name : this.gps.keySet()){
            //Distance in meters
            float distance = (float) SphericalUtil.computeDistanceBetween(this.gps.get(name), currentPos);
            if(distance < 15000 +  accuracy) {
                distanceMap.put(name, distance);
            }
        }
        return distanceMap; //this.sortByComparator(distanceMap, true);
    }

    private List<String> pickClosest(LatLng currentPos, float accuracy, int amount) {
        Map<String, Float> sorted = this.getDistances(currentPos, accuracy);

        List<String> topX = new LinkedList<>();

        for(int i = 0; i < Math.min(sorted.keySet().size(), amount); i++){
            String name = (String) sorted.keySet().toArray()[i];
            topX.add(name);
        }
        return topX;
    }

    private Map<String, Float> sortByComparator(Map<String, Float> unsortMap, final boolean order) {
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

    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile(Context cxt, String model) throws IOException {
        AssetFileDescriptor fileDescriptor = cxt.getAssets().openFd(model);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private TensorImage loadTensor(Bitmap bitmap){
        this.ti.load(bitmap);

        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(getPreProcessNormalisationOp())
                .build();
        return imageProcessor.process(this.ti);
    }

    private TensorOperator getPreProcessNormalisationOp() {
        return new NormalizeOp(127.5f, 127.5f);
    }

    private TensorOperator getPostProcessNormaliseOp() {
        return new NormalizeOp(0.0f, 1.0f);
    }

    private Bitmap loadImage(String path) throws IOException {

        URLConnection conn = new URL(path).openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is, 8192);
        Bitmap bm = BitmapFactory.decodeStream(bis);

        return bm;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
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
