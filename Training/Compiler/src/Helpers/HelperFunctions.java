package Helpers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperFunctions {

    public static Tuple<Float, Float> addNoiseToLatLong(float latitude, float longitude, Random random){

        float latm = (random.nextFloat() * 200.0f) - 100.0f;
        float longm = (random.nextFloat() * 200.0f) - 100.0f;

        float earth = 6378.137f;  //radius of the earth in kilometer
        double pi = Math.PI;
        double m = (1.0f / ((2.0f * pi / 360.0f) * earth)) / 1000.0f;  //1 meter in degree

        float new_latitude = (float) (latitude + (latm * m));

        float new_longitude = (float) (longitude + (longm * m) / Math.cos(latitude * (pi / 180)));

        if(latitude == 90 || latitude == -90){
            new_longitude = 2.0f * latitude;
        }

        if(new_longitude > 180.0f){
            new_longitude -= 360.0f;
        } else if(new_longitude < -180.0){
            new_longitude += 360;
        }

        if(new_latitude > 90.0){
            new_latitude = 90.0f - (new_latitude - 90.0f);
            new_longitude = new_longitude * -1;
        } else if(new_latitude < -90.0){
            new_latitude = 90 + (new_latitude + 90.0f);
            new_longitude = new_longitude * -1;
        }

        new_longitude = Math.max(new_longitude, 0.0001f / 4.0f);

        return new Tuple<>(new_latitude, new_longitude);
    }

    public static void resizeImg(String location, int width, int height) throws Exception {
        resizeImg(location, width, height, null, null);
    }

    public static void resizeImg(String location, int width, int height, ICallback success, ICallback error) throws Exception {
        location = Values.BASE_LOCATION + location;
        File ff = new File(location);

        try {
            //inputStream.reset();
            BufferedImage img = ImageIO.read(ff);

            if (img.getWidth() == width && img.getHeight() == height) {
                return;
            }

            BufferedImage newImg = resize(img, width, height);

            ImageIO.write(newImg, "jpg", new File(location).getAbsoluteFile());

            if(success != null) {
                success.invoke();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(error != null){
                error.invoke(e.getMessage());
            }
        }
    }

    public static void downloadImage(String strURL, String location){
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {

            URL url = new URL(strURL);
            inputStream = url.openStream();
            outputStream = new FileOutputStream(location);

            byte[] buffer = new byte[2048];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException :- " + e.getMessage());

        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException :- " + e.getMessage());

        } catch (IOException e) {
            System.out.println("IOException :- " + e.getMessage());
        } catch (NullPointerException e){
            System.out.println("NullPointerException :- " + e.getMessage());
        }
    }

    public static HashMap<Integer, QuadTuple<String, Float, Float, Boolean>> getUKPlaces(ICallback post) throws Exception {
        HashMap<Integer, QuadTuple<String, Float, Float, Boolean>> ukMap = new HashMap<>();

        ICallback callback = new ICallback() {
            @Override
            public void invoke(Object... args) throws Exception {
                String line = String.valueOf(args[0]);
                Pattern p = Pattern.compile("([0-9]+),([\"])*([a-zA-Z0-9:/_,.()!%\\-']+)([\"])*,([0-9.-]+),([0-9.\\-E]+),(true)(\\r\\n)*");
                Matcher m = p.matcher(line);

                if (m.matches()) {
                    int landmarkID = Integer.parseInt(m.group(1));
                    String url = m.group(3);
                    float latitude = Float.parseFloat(m.group(5));
                    float longitude = Float.parseFloat(m.group(6));
                    boolean isUK = Boolean.parseBoolean(m.group(7));

                    QuadTuple<String, Float, Float, Boolean> quad = new QuadTuple<>(url, latitude, longitude, isUK);
                    ukMap.put(landmarkID, quad);
                }
            }
        };

        FileIO.readFileWithCallBack("ukPlaces.csv", callback, post);

        return ukMap;
    }

    public static boolean inUK(float longitude, float latitude){
        float lowerLong = 49.82380908513249f;
        float upperLong = 59.478568831926395f;

        float lowerLat = -10.8544921875f;
        float upperLat = 2.021484375f;

        if(latitude <= upperLat && latitude >= lowerLat){
            return longitude <= upperLong && longitude >= lowerLong;
        }

        return false;
    }

    protected static BufferedImage resize(BufferedImage img, int newW, int newH) throws Exception{
        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage dimg = new BufferedImage(newW, newH, img.getType() == 0 ? 2 : img.getType());//img.getType());
        Graphics2D g = dimg.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();

        return dimg;
    }
}
