package com.landmarker3;

import android.content.Context;
import android.graphics.Bitmap;
import android.telecom.Call;

import androidx.test.platform.app.InstrumentationRegistry;

import com.facebook.react.bridge.Callback;
import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.TensorImage;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TensorflowImageModuleTest {

    TensorflowImageModule tf;
    Callback fail, doNothing;

    @Before
    public void setUp() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        this.tf = new TensorflowImageModule(ctx);

        this.fail = args -> {
            System.out.println(args);
            fail();
        };

        this.doNothing = args -> {};
    }

    @After
    public void tearDown() throws Exception {
        this.tf = null;
    }

    @Test
    public void getName() {
        assertNotNull(this.tf.getName());
    }

    @Test
    public void classify() {
        //Testing not possible for this method as files cannot be loaded in test mode
        /*


        String path = "https://upload.wikimedia.org/wikipedia/commons/8/8e/Bolingbroke_Castle%2C_Old_Bolingbroke_-_geograph.org.uk_-_1455687.jpg";

        this.tf.classify("final_model_v1.tflite", path, 50, 50, 10, fail, doNothing);
        this.tf.classify("", path, 50, 50, 10, doNothing, fail);
        this.tf.classify(null, path, 50, 50, 10, doNothing, fail);

        this.tf.classify("final_model_v1.tflite", path, 10000, 10000, 10, doNothing, fail);
        this.tf.classify("final_model_v1.tflite", path, -10000, 10000, 10, doNothing, fail);
        this.tf.classify("final_model_v1.tflite", path, 10000, -10000, 10, doNothing, fail);
        this.tf.classify("final_model_v1.tflite", path, -10000, -10000, 10, doNothing, fail);

        this.tf.classify("final_model_v1.tflite", path, -79, -79, 10, fail, doNothing);
        this.tf.classify("final_model_v1.tflite", path, 79, 79, 10, fail, doNothing);

        this.tf.classify("final_model_v1.tflite", path, -79, -79, -10, doNothing, fail);
        this.tf.classify("final_model_v1.tflite", path, 79, 79, 100000, fail, doNothing);

        try{
            this.tf.classify("final_model_v1.tflite", path, 79, 79, 100, doNothing, null);
            this.tf.classify("final_model_v1.tflite", path, 79, 79, 100, null, null);
            fail();
        } catch(Exception e){}*/
    }

    @Test
    public void getInfoReact() {
        this.tf.getInfo("Trent_Building", this.doNothing, this.fail);
        this.tf.getInfo("Trent Building", this.doNothing, this.doNothing);

        try {
            this.tf.getInfo("", this.fail, this.doNothing);
            this.tf.getInfo("hfogkjfdjgh", this.fail, this.doNothing);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void getInfo(){
        assertNotNull(this.tf.getInfo("test"));
        assertNotNull(this.tf.getInfo("ghfoghfghdfjghf"));
        assertNotNull(this.tf.getInfo(null));

        assertNotEquals(this.tf.getInfo("Trent_Building"), "");
        assertEquals(this.tf.getInfo("Trent_building"), "");

        assertTrue(this.tf.getInfo("Trent_Building").length() > 0);
    }

    @Test
    public void getLandmarkGPS() {
        //react
    }

    @Test
    public void additional() {
        assertTrue(this.tf.additional("gfhg"));
        assertTrue(this.tf.additional("Trent_Building"));
        assertTrue(this.tf.additional("ghdsighdghdfkghfk545345lg"));
        assertFalse(this.tf.additional("Portland_Building_(Nottingham)"));


    }

    @Test
    public void getDistances() {
        assertNotNull(this.tf.getDistances(new LatLng(0, 0), 1000000000));
        assertTrue(this.tf.getDistances(new LatLng(0, 0), 1000000000).size() > 0);
        assertTrue(this.tf.getDistances(new LatLng(0, 0), 0).size() >= 0);

        try {
            this.tf.getDistances(new LatLng(0, 0), -100);
            fail();
        } catch (Exception e) {
        }

        try {
            this.tf.getDistances(new LatLng(0, 90), 100);
        } catch (Exception e) {
            fail();
        }

        try {
            this.tf.getDistances(null, 100);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void sortByComparator() {
        Map<String, Float> map = new HashMap<>();
        map.put("biggest", 100.0f);
        map.put("smallest", 1.0f);
        map.put("mid", 6.0f);

        assertNotNull(this.tf.sortByComparator(map, true));
        assertNotNull(this.tf.sortByComparator(map, false));

        assertEquals(this.tf.sortByComparator(map, true).size(), map.size());
        assertEquals(this.tf.sortByComparator(map, false).size(), map.size());

        try{
            this.tf.sortByComparator(null, true);
            fail();
        }catch (Exception e){}

        Map<String, Float> sorted = this.tf.sortByComparator(map, true);
        assertEquals(sorted.keySet().toArray()[2], "biggest");
        assertEquals(sorted.keySet().toArray()[0], "smallest");

        sorted = this.tf.sortByComparator(map, false);
        assertEquals(sorted.keySet().toArray()[0], "biggest");
        assertEquals(sorted.keySet().toArray()[2], "smallest");
    }

    @Test
    public void loadModelFile() {
        //Cannot be tested due to android file IO limitations
    }

    @Test
    public void loadTensor() {
        this.tf.ti = new TensorImage(DataType.FLOAT32);
        Bitmap bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);

        assertNotNull(this.tf.loadTensor(bm));
        assertNotNull(this.tf.ti.getBuffer());
    }

    @Test
    public void getPreProcessNormalisationOp() {
        assertNotNull(this.tf.getPreProcessNormalisationOp());
        assertTrue(this.tf.getPreProcessNormalisationOp() instanceof NormalizeOp);
    }

    @Test
    public void getPostProcessNormaliseOp() {
        assertNotNull(this.tf.getPostProcessNormaliseOp());
        assertTrue(this.tf.getPostProcessNormaliseOp() instanceof NormalizeOp);
    }

    @Test
    public void loadImage() {
        //cannot be tested due to file io limitations
    }

    @Test
    public void getResizedBitmap() {
        Bitmap bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);

        assertNotNull(this.tf.getResizedBitmap(bm, 100, 100));

        bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);

        Bitmap newBm = this.tf.getResizedBitmap(bm, 100, 100);
        assertEquals(newBm.getWidth(), 100);
        assertEquals(newBm.getHeight(), 100);

        bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);

        newBm = this.tf.getResizedBitmap(bm, 500, 100);
        assertEquals(newBm.getWidth(), 500);
        assertEquals(newBm.getHeight(), 100);

        bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);

        newBm = this.tf.getResizedBitmap(bm, 500, 1000);
        assertEquals(newBm.getWidth(), 500);
        assertEquals(newBm.getHeight(), 1000);

        bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);

        newBm = this.tf.getResizedBitmap(bm, 1200, 1000);
        assertEquals(newBm.getWidth(), 1200);
        assertEquals(newBm.getHeight(), 1000);

        bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);

        try {
            assertNotNull(this.tf.getResizedBitmap(null, 100, 100));
            fail();
        } catch (Exception e) {}

        bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);

        try {
            assertNotNull(this.tf.getResizedBitmap(bm, -1, 100));
            fail();
        } catch (Exception e) {}

        bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);

        try {
            assertNotNull(this.tf.getResizedBitmap(bm, 100, -1));
            fail();
        } catch (Exception e) {}
    }
}