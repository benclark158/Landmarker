package Helpers.Test;

import Helpers.HelperFunctions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelperFunctionsTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addNoiseToLatLong() {
    }

    @Test
    void resizeImg() {
    }

    @Test
    void testResizeImg() {
    }

    @Test
    void downloadImage() {
    }

    @Test
    void getUKPlaces() {
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
        HelperFunctions.downloadImage("https://images-eu.ssl-images-amazon.com/images/G/02/gno/sprites/nav-sprite-global_bluebeacon-1x_optimized_layout1._CB468502434_.png",
                "C:/imgTest.png");

        try {
            HelperFunctions.resizeImg(null, 0, 0);
            fail();
        } catch (Exception e) {}
    }
}