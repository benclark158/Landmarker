package Helpers.Test;

import Helpers.HelperFunctions;
import Helpers.Tuple;
import Helpers.Values;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class HelperFunctionsTest {

    String location = "/imgTest.JPG";
    Random random;
    @BeforeEach
    void setUp() {
        this.random = new Random();

        HelperFunctions.downloadImage("https://upload.wikimedia.org/wikipedia/commons/4/41/Sunflower_from_Silesia2.jpg",
                location);

        Values.BASE_LOCATION = "";
    }

    @AfterEach
    void tearDown() {
        this.deleteFile(this.location);
    }

    @Test
    void addNoiseToLatLong() {
        Tuple t = HelperFunctions.addNoiseToLatLong(0, 0, this.random);
        assertNotNull(t);
        assertNotNull(t.a);
        assertNotNull(t.b);

        assertTrue(t.a instanceof Float);
        assertTrue(t.b instanceof Float);

        for(int i = 0; i < 1000; i++) {
            Tuple<Float, Float> vals = HelperFunctions.addNoiseToLatLong(90.0f, 180f, this.random);
            System.out.println(Math.abs(vals.a) + " : " + Math.abs(vals.b));
            assertTrue(Math.abs(vals.a) <= 90.0f);
            assertTrue(Math.abs(vals.b) <= 180.0f);
        }
    }

    @Test
    void downloadImage() {
        HelperFunctions.downloadImage("https://upload.wikimedia.org/wikipedia/commons/4/41/Sunflower_from_Silesia2.jpg", this.location);
        assertTrue(new File(this.location).exists());

        this.deleteFile(this.location);

        try {
            HelperFunctions.downloadImage(null, this.location);
            HelperFunctions.downloadImage("", this.location);
            HelperFunctions.downloadImage("https://upload.wikimedia.org/wikipedia/commons/4/41/Sunflower_from_Silesia2.jpg", "");
            HelperFunctions.downloadImage("https://upload.wikimedia.org/wikipedia/commons/4/41/Sunflower_from_Silesia2.jpg", null);


        } catch (Exception e){
            e.printStackTrace();
            fail();
        }

        assertFalse(new File(this.location).exists());

        HelperFunctions.downloadImage("https://google.com/", this.location);

        assertTrue(new File(this.location).exists());
    }

    @Test
    void getUKPlaces() throws Exception {
        //Cannot be tested if file does not exist!

        try{
            HelperFunctions.getUKPlaces(null);
        } catch (Exception e){
            if(!(e instanceof FileNotFoundException)) {
                fail();
            }
        }

        try {
            assertNotNull(HelperFunctions.getUKPlaces(null));
        } catch(FileNotFoundException e){}
    }

    @Test
    void inUK() {
        assertFalse(HelperFunctions.inUK(100000, 100000));
        assertFalse(HelperFunctions.inUK(0, 0));
        assertFalse(HelperFunctions.inUK(-100000, -100000));
        assertFalse(HelperFunctions.inUK(-0.00001f, -0.00001f));
        assertFalse(HelperFunctions.inUK(1.2568f, 51.2568f));
        assertTrue(HelperFunctions.inUK(51.5585f, 1.2568f));
    }

    @Test
    void resize() {
        try {
            HelperFunctions.resizeImg(this.location, 224, 224);
            assertEquals(this.getImgSize(this.location).width, 224);
            assertEquals(this.getImgSize(this.location).height, 224);

            HelperFunctions.resizeImg(this.location, 224, 1);
            assertEquals(this.getImgSize(this.location).width, 224);
            assertEquals(this.getImgSize(this.location).height, 1);

            HelperFunctions.resizeImg(this.location, 1, 1);
            assertEquals(this.getImgSize(this.location).height, 1);
            assertEquals(this.getImgSize(this.location).width, 1);

            HelperFunctions.resizeImg(this.location, 10000, 10000);
            assertEquals(this.getImgSize(this.location).height, 10000);
            assertEquals(this.getImgSize(this.location).width, 10000);

            HelperFunctions.resizeImg(this.location, 100, 100);
            assertEquals(this.getImgSize(this.location).height, 100);
            assertEquals(this.getImgSize(this.location).width, 100);

            HelperFunctions.resizeImg(null, 0, 0);
            HelperFunctions.resizeImg(null, 244, 258);
            HelperFunctions.resizeImg(this.location, 0, 0);
            HelperFunctions.resizeImg(this.location, -100, -200);

            assertEquals(this.getImgSize(this.location).height, 100);
            assertEquals(this.getImgSize(this.location).width, 100);

        } catch (Exception e) {
            fail();
        }
    }

    private void deleteFile(String loc){
        File f = new File(loc);
        if(f.exists()){
            f.delete();
        }
    }

    private Dimension getImgSize(String location) throws IOException {
        BufferedImage img = ImageIO.read(new File(location));
        return new Dimension(img.getWidth(), img.getHeight());
    }
}