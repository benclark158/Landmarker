import Helpers.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LandmarkDataComplier {

    /**
     * Easy use function for below code
     * @throws Exception
     */
    public void run() throws Exception {
        //gets landmarks in uk
        HashMap<Integer, QuadTuple<String, Float, Float, Boolean>> ukMap = HelperFunctions.getUKPlaces(null);

        //gets images in uk
        //formatted with noise ready for training
        this.outputImageData(ukMap);
    }

    /**
     * Gets images that are in the uk from the complete 4million image dataset
     * @param ukMap
     * @throws Exception
     */
    public void outputImageData(HashMap<Integer, QuadTuple<String, Float, Float, Boolean>> ukMap) throws Exception {
        String strDir = Values.DOWNLOAD_LOCATION + "photos\\";

        FileWriter writer = new FileWriter("googleTrainingTest.csv", true);
        writer.write("landmarkID,url,actual_latitude,actual_longitude,noise_lat,noise_long\r\n");

        Random random = new Random();

        ICallback during = new ICallback() {
            @Override
            public void invoke(Object... args) throws Exception {
                String line = String.valueOf(args[0]);

                //Check format of each line
                Pattern p = Pattern.compile("([0-9a-z]+),([\"])*([a-zA-Z0-9:/_,.()!%\\-']+)([\"])*,([0-9]+)(\\r\\n)*");
                Matcher m = p.matcher(line);

                if (m.matches()) {
                    String id = m.group(1); //id of image?
                    int landmarkID = Integer.parseInt(m.group(5));

                    QuadTuple<String, Float, Float, Boolean> quad = ukMap.get(landmarkID);
                    if (quad != null && quad.d) {
                        //check if is in uk ^^

                        String loc = strDir + landmarkID + "\\" + id + ".jpg";
                        File locFile = new File(loc);

                        //check specific image exists
                        if (locFile.exists()) {
                            //THIS FILE EXISTS SO WRITE TO FILE!

                            //creates noisy lat and long
                            Tuple<Float, Float> tuple = HelperFunctions.addNoiseToLatLong(quad.b, quad.c, random);
                            float nLat = tuple.a;
                            float nLong = tuple.b;

                            //creates formatted line
                            String format = landmarkID + "," + loc.replace("..\\", "/").replace("\\", "/") +
                                    "," + quad.b + "," + quad.c + "," + nLat + "," + nLong + "\r\n";

                            //writes line to file
                            writer.write(format);
                        }
                    }
                }
            }
        };

        FileIO.readFileWithCallBack("E:\\Dissertation\\Landmarker\\Training\\train.csv", during);

        writer.close();
    }


    /**
     * Unused function! DO NOT USE - INCOMPLETE
     * @param url
     * @return
     * @throws IOException
     */
    @Deprecated
    public TriTuple<String, String, String> getInfo(String url) throws IOException {

        if(url != null){
            return null;
        }

        Document doc = null;
        doc = Jsoup.connect(url.replace("\"", "").replace("http://", "https://")).get();

        Element infoBox = doc.getElementById("wdinfobox");
        Elements externalText = infoBox.getElementsByClass("extiw");

        //todo

        float latitude = 0, longitude = 0;
        String wikipedia = null;
        for (Element el : externalText) {
            if (el.attr("href").contains("wikipedia")) {
                wikipedia = el.attr("href");
                break;
            }
        }

        String name = null, info = null, infoHTML = null;

        if(wikipedia != null){
            doc = Jsoup.connect(wikipedia).get();
            name = doc.getElementById("firstHeading").html();
            Element content = doc.getElementById("mw-content-text");
            Elements tagP = content.getElementsByTag("p");

             for(Element e : tagP){
                 if(!e.text().equals("")){
                    info = e.text();
                    infoHTML = e.html();
                     break;
                 }
             }
        }

        //return new TriTuple<>(name, info, infoHTML);
        return null;
    }
}
