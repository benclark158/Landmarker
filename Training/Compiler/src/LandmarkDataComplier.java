import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LandmarkDataComplier {

    public void generateInformation(){
        List<Tuple<Integer, String>> listNames = new ArrayList<>();
    }

    public String formatInputs(float latitude, float longitude, String imageURL){
        return latitude + "," + longitude + "," + imageURL;
    }

    public HashMap<Integer, QuadTuple> getLandmarkData() throws IOException {
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
