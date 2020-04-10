import com.google.common.collect.Lists;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        System.setProperty("org.jline.terminal.dumb", "true");
/*
        for(int i = 0; i < 500; i++){
            String str = String.valueOf(i);
            if(str.length() == 2){
                str = "0" + str;
            } else if(str.length() == 1){
                str = "00" + str;
            }
            System.out.println("https://s3.amazonaws.com/google-landmark/train/images_" + str + ".tar");
        }*/

        //return;

        Main main = new Main();

        //main.organiseFiles();

        ImageDownloader down = new ImageDownloader();
        down.collectImages();
    }

    private void organiseFiles() {
        try {
            FileWriter errorWriter = new FileWriter("error.csv", true);

            errorWriter.write("landmarkID,url,latitude,longitude\r\n");

            List readLines = new ArrayList<>();

            File file = new File("E:\\Training photos\\GoogleAPI Photos\\train_label_to_category.csv");    //creates a new file instance
            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
            String line = "";

            while ((line = br.readLine()) != null) {
                if (line.contains("landmark_id")) {
                    continue;
                }
                readLines.add(line);
            }

            int numThreads = 8;
            int size = Math.round(readLines.size() / Float.valueOf(numThreads));

            Collections.shuffle(readLines);

            //new Scraper(0, null).doScraping(readLines, "blank");

            List<List<String>> smallerLists = Lists.partition(readLines, size);

            fr.close();

            Thread[] threads = new Thread[smallerLists.size()];
            Scraper[] runners = new Scraper[smallerLists.size()];

            int i = 0;

            for (List<String> threadLists : smallerLists) {
                threads[i] = new Thread(runners[i] = new Scraper(i, threadLists));

                threads[i].start();
                i++;
            }

            for (Thread thread : threads) {
                thread.join();
            }
/*
            List<Tuple<HashMap, List<String>>> results = new ArrayList<>(numThreads);

            HashMap<Integer, QuadTuple<String, Float, Float, Boolean>> map = new HashMap<>();

            for (int x = 0; x < runners.length; x++) {
                results.set(x, runners[x].getResult());

                for(String str : results.get(x).b) {
                    errorWriter.write(str);
                }

                HashMap partMap = results.get(x).a;

                for(Object landmarkID : partMap.keySet()){
                    int val = Integer.parseInt(String.valueOf(landmarkID));
                    map.put(val, (QuadTuple<String, Float, Float, Boolean>) partMap.get(val));
                }
            }*/



            errorWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static class Scraper implements Runnable {

        Tuple<HashMap, List<String>> result;
        List<String> threadLists;
        int id;

        public Scraper(int ix, List<String> lists){
            this.threadLists = lists;
            this.id = ix;
        }

        @Override
        public void run() {
            try {
                this.result = doScraping(this.threadLists, "thread-" + this.id);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        public Tuple<HashMap, List<String>> getResult() throws Exception {
            if(this.result == null){
                throw new Exception("Result is null for thread: " + this.id);
            }
            return this.result;
        }

        public  Tuple<HashMap, List<String>> doScraping(List<String> lines, String thread) throws IOException {
            int errors = 0;
            float progress = 0.0f;
            float lineSize = lines.size();

            ArrayList<String> errorList = new ArrayList<>();
            HashMap<Integer, QuadTuple> hashMap = new HashMap<>();

            FileWriter writer = new FileWriter("thead" + thread + ".csv", true);
            writer.write("landmarkID,url,latitude,longitude,isUK\r\n");

            //for (String line : ProgressBar.wrap(lines, "TaskName")) {
            for (int i = 0; i < lineSize; i++) {
                if (progress % 10 == 0) {
                    //System.out.println("thread: " + thread + "\t\tid: " + landmarkID + "/" + lineSize + "\t\terrors: " + errors);
                    System.out.println(thread + ":: " + progress / lineSize * 100.0f + "% :: " + i);
                }

                try {
                    String line = lines.get(i);
                    Tuple<Integer, QuadTuple> tuple = this.singleScrape(line);
                    hashMap.put(tuple.a, tuple.b);

                    QuadTuple<String, Float, Float, Boolean> quad = tuple.b;

                    progress++;

                    if(quad.d) {
                        writer.write(tuple.a + "," + quad.a + "," + quad.b + "," + quad.c + "," + true);
                    }

                } catch (Exception ex) {
                    if (!(ex instanceof NullPointerException)) {
                        if (ex instanceof HttpStatusException) {
                            if (((HttpStatusException) ex).getStatusCode() == 429) {
                                //System.out.println("429 error");
                                i--;
                            }
                        }
                    }
                }
            }
            writer.close();
            return new Tuple(hashMap, errorList);
        }

        private Tuple<Integer, QuadTuple> singleScrape(String line) throws IOException {
            String[] parts = line.split(",", 2);
            int landmarkID = Integer.parseInt(parts[0]);
            String url = parts[1];

            Document doc = null;
            doc = Jsoup.connect(url.replace("\"", "").replace("http://", "https://")).get();

            Element infoBox = doc.getElementById("wdinfobox");
            Elements externalText = infoBox.getElementsByClass("external text");

            String name = doc.getElementById("firstHeading").html();

            float latitude = 0, longitude = 0;
            boolean set = false;

            for (Element el : externalText) {
                if (el.attr("href").contains("geohack")) {
                    String[] info = el.attr("href").split("&");

                    for (String str : info) {
                        if (str.contains("params=")) {
                            String longLat = str.replace("params=", "").replace("_E_globe:Earth_", "");
                            String[] breakup = longLat.split("_N_");
                            latitude = Float.parseFloat(breakup[0]);
                            longitude = Float.parseFloat(breakup[1]);

                            set = true;
                            break;
                        }
                    }
                    if (set) {
                        break;
                    }
                }
            }
            //String csvStr = landmarkID + "," + url + "," + latitude + "," + longitude + "\r\n";
            QuadTuple<String, Float, Float, Boolean> quad = new QuadTuple<>(url, latitude, longitude, this.inUK(latitude, longitude));
            return new Tuple(landmarkID, quad);
        }

        public boolean inUK(float longitude, float latitude){
            float lowerLong = 49.82380908513249f;
            float upperLong = 59.478568831926395f;

            float lowerLat = -10.8544921875f;
            float upperLat = 2.021484375f;

            if(latitude <= upperLat && latitude >= lowerLat){
                return longitude <= upperLong && longitude >= lowerLong;
            }

            return false;
        }
    }
}