package com.landmarker3;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.google.android.gms.maps.model.LatLng;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageOperator;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
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
    public void classify(String model, String path, float longitude, float latitude, Callback errorCallback, Callback successCallback) {
        try {

            this.tflite = new Interpreter(loadModelFile(this.context, model));

            //this.tflite.;

            float[][] gps = new float[1][2];
            gps[0][0] = longitude;
            gps[0][1] = latitude;

            path = "https://i2-prod.nottinghampost.com/incoming/article3128479.ece/ALTERNATES/s615/0_JAJ_TEM_090519UoN_001JPG.jpg";

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

            List<String> closest = this.pickClosest(new LatLng(latitude, longitude), 10);

            successCallback.invoke("success: " + str);

        } catch (Exception e) {
            e.printStackTrace();
            errorCallback.invoke("error: "  + e.getMessage());
        }
    }

    private List<String> pickClosest(LatLng currentPos, int amount) {
        HashMap
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
