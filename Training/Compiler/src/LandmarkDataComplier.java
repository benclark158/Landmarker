import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LandmarkDataComplier {

    public void generateInformation() throws IOException {
        //List<Tuple<Integer, String>> listNames = new ArrayList<>();
        HashMap<Integer, QuadTuple> ukMap = this.getUKLandmarks();
        this.outputImageData(ukMap);
    }

    public void outputImageData(HashMap<Integer, QuadTuple> ukMap) throws IOException {
        String strDir = "..\\TrainingData\\GoogleDataset\\";

        File f = new File("E:\\Dissertation\\Landmarker\\Training\\train.csv");    //creates a new file instance
        FileReader fr = new FileReader(f);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        String line = "";

        ArrayList<Thread> threadList = new ArrayList<>();

        int i = 0;

        FileWriter writer = new FileWriter("uonTrainingTest.csv", true);
        writer.write("landmarkID,url,actual_latitude,actual_longitude,noise_lat,noise_long\r\n");

        Random random = new Random();

        while ((line = br.readLine()) != null) {
            if (line.contains("landmark_id")) {
                continue;
            } else if (i % 100 == 0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now) + "\t\tProgress: " + i);
            }
            i++;

            Pattern p = Pattern.compile("([0-9a-z]+),([\"])*([a-zA-Z0-9:/_,.()!%\\-']+)([\"])*,([0-9]+)(\\r\\n)*");
            Matcher m = p.matcher(line);

            if (m.matches()) {
                String id = m.group(1); //id of image?
                String url = m.group(3); //<< for download - not needed!
                int landmarkID = Integer.parseInt(m.group(5));

                QuadTuple<String, Float, Float, Boolean> quad = ukMap.get(landmarkID);
                if (quad != null && quad.d) {
                    String loc = strDir + landmarkID + "\\" + id + ".jpg";
                    File locFile = new File(loc);
                    if(locFile.exists()){
                        //THIS FILE EXISTS SO WRITE TO FILE!

                        Tuple<Float, Float> tuple = this.addNoiceToLatLong(quad.b, quad.c, random);
                        float nLat = tuple.a;
                        float nLong = tuple.b;

                        String format = landmarkID + "," + loc.replace("..\\", "").replace("\\", "") +
                                "," + quad.b + "," + quad.c + "," + nLat + "," + nLong + "\r\n";
                        writer.write(format);
                    }
                }
            }
        }

        writer.close();
    }

    public Tuple<Float, Float> addNoiceToLatLong(float latitude, float longitude, Random random){

        float latm = (random.nextFloat() * 200.0f) - 100.0f;
        float longm = (random.nextFloat() * 200.0f) - 100.0f;

        float earth = 6378.137f;  //radius of the earth in kilometer
        double pi = Math.PI;
        double m = (1.0f / ((2.0f * pi / 360.0f) * earth)) / 1000.0f;  //1 meter in degree

        float new_latitude = (float) (latitude + (latm * m));

        float new_longitude = (float) (longitude + (longm * m) / Math.cos(latitude * (pi / 180)));

        return new Tuple<>(new_latitude, new_longitude);
    }

    public HashMap<Integer, QuadTuple> getUKLandmarks() throws IOException {
        File geo = new File("output.csv");
        FileReader frGeo = new FileReader(geo);
        BufferedReader brGeo = new BufferedReader(frGeo);
        String line = "";

        HashMap<Integer, QuadTuple> ukMap = new HashMap<>();

        while ((line = brGeo.readLine()) != null) {
            if (line.contains("landmark_id")) {
                continue;
            }
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
        brGeo.close();
        frGeo.close();
        return ukMap;
    }

    public TriTuple<String, String, String> getInfo(String url) throws IOException {

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

        return new TriTuple<>(name, info, infoHTML);
    }
}
