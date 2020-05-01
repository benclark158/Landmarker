import Helpers.FileIO;
import Helpers.HelperFunctions;
import Helpers.QuadTuple;
import Helpers.Tuple;
import com.google.common.collect.Lists;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProgramMain {

    public static void main(String[] args) throws Exception {
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
        ProgramMain main = new ProgramMain();
        main.organiseFiles();

        ImageDownloader down = new ImageDownloader();
        down.collectImages();

        CustomDatasetCompiler comp = new CustomDatasetCompiler();
        comp.compileCustomDatasetFromFolders();

        Counter count = new Counter();
        count.outputTop100Landmarks();

        MapLandmarks map = new MapLandmarks();
        map.run();

        FormatAll resize = new FormatAll();
        resize.run();

        GatherData gather = new GatherData();
        gather.gatherDataFromIDs();

        LandmarkDataComplier data = new LandmarkDataComplier();
        data.run();
    }

    private void organiseFiles() {
        try {
            FileWriter errorWriter = new FileWriter("error.csv", true);

            errorWriter.write("landmarkID,url,latitude,longitude\r\n");

            List readLines = new ArrayList<>();

            readLines = FileIO.readFile("E:\\Training photos\\GoogleAPI Photos\\train_label_to_category.csv");

            int numThreads = 8;
            int size = Math.round(readLines.size() / Float.valueOf(numThreads));

            Collections.shuffle(readLines);

            //new Scraper(0, null).doScraping(readLines, "blank");

            List<List<String>> smallerLists = Lists.partition(readLines, size);

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

        public Tuple<HashMap, List<String>> doScraping(List<String> lines, String thread) throws IOException {
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
            QuadTuple<String, Float, Float, Boolean> quad = new QuadTuple<>(url, latitude, longitude, HelperFunctions.inUK(latitude, longitude));
            return new Tuple(landmarkID, quad);
        }
    }
}
