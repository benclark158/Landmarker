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

        //This does the different components
        //DO NOT RUN LIKE THIS
        //Comment out which programs you do not want to run

        ProgramMain main = new ProgramMain();
        main.organiseFiles();

        ImageDownloader down = new ImageDownloader();
        down.collectImages();

        CustomDatasetCompiler comp = new CustomDatasetCompiler();
        comp.compileCustomDatasetFromFolders();

        Counter count = new Counter();
        count.outputPopularLandmarks();

        MapLandmarks map = new MapLandmarks();
        map.run();

        FormatAll resize = new FormatAll();
        resize.run();

        GatherData gather = new GatherData();
        gather.gatherDataFromIDs();

        LandmarkDataComplier data = new LandmarkDataComplier();
        data.run();
    }

    /**
     * Reads landmark list and gets list of all places within the UK
     * Uses scraper to get information
     */
    private void organiseFiles() {
        try {
            FileWriter errorWriter = new FileWriter("error.csv", true);

            errorWriter.write("landmarkID,url,latitude,longitude\r\n");

            List readLines = new ArrayList<>();

            readLines = FileIO.readFile("E:\\Training photos\\GoogleAPI Photos\\train_label_to_category.csv");

            int numThreads = 8;
            int size = Math.round(readLines.size() / Float.valueOf(numThreads));

            Collections.shuffle(readLines);

            //splits data into smaller lists
            List<List<String>> smallerLists = Lists.partition(readLines, size);

            Thread[] threads = new Thread[smallerLists.size()];
            Scraper[] runners = new Scraper[smallerLists.size()];

            int i = 0;

            //run multiple threads/scrapers
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


    /**
     * Scraper - scapes websites for information
     */
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
                //does work
                this.result = doScraping(this.threadLists, "thread-" + this.id);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        /**
         * Unused
         * @return
         * @throws Exception
         */
        public Tuple<HashMap, List<String>> getResult() throws Exception {
            if(this.result == null){
                throw new Exception("Result is null for thread: " + this.id);
            }
            return this.result;
        }

        /**
         * Scapes the wikipedia page for valid information
         * @param lines
         * @param thread
         * @return
         * @throws IOException
         */
        public Tuple<HashMap, List<String>> doScraping(List<String> lines, String thread) throws IOException {
            int errors = 0;
            float progress = 0.0f;
            float lineSize = lines.size();

            ArrayList<String> errorList = new ArrayList<>();
            HashMap<Integer, QuadTuple> hashMap = new HashMap<>();

            FileWriter writer = new FileWriter("thead" + thread + ".csv", true);
            writer.write("landmarkID,url,latitude,longitude,isUK\r\n");

            //iterate over lines
            for (int i = 0; i < lineSize; i++) {
                if (progress % 10 == 0) {
                    //System.out.println("thread: " + thread + "\t\tid: " + landmarkID + "/" + lineSize + "\t\terrors: " + errors);
                    System.out.println(thread + ":: " + progress / lineSize * 100.0f + "% :: " + i);
                }

                try {
                    //scape and output the data
                    String line = lines.get(i);
                    Tuple<Integer, QuadTuple> tuple = this.singleScrape(line);
                    hashMap.put(tuple.a, tuple.b);

                    QuadTuple<String, Float, Float, Boolean> quad = tuple.b;

                    progress++;

                    if(quad.d) {
                        writer.write(tuple.a + "," + quad.a + "," + quad.b + "," + quad.c + "," + true);
                    }

                } catch (Exception ex) {
                    //check errors and process accordingly
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

        /**
         * Scrapes a single website
         * @param line
         * @return
         * @throws IOException
         */
        private Tuple<Integer, QuadTuple> singleScrape(String line) throws IOException {
            String[] parts = line.split(",", 2);
            int landmarkID = Integer.parseInt(parts[0]);
            String url = parts[1];

            //open website
            Document doc = null;
            doc = Jsoup.connect(url.replace("\"", "").replace("http://", "https://")).get();

            //read html
            Element infoBox = doc.getElementById("wdinfobox");
            Elements externalText = infoBox.getElementsByClass("external text");

            //name of page
            String name = doc.getElementById("firstHeading").html();

            float latitude = 0, longitude = 0;
            boolean set = false;

            //check all elements for the gps coords
            for (Element el : externalText) {
                if (el.attr("href").contains("geohack")) {
                    String[] info = el.attr("href").split("&");

                    for (String str : info) {
                        if (str.contains("params=")) {
                            //format and read float gps values
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
            //output data as quad tuple
            QuadTuple<String, Float, Float, Boolean> quad = new QuadTuple<>(url, latitude, longitude, HelperFunctions.inUK(latitude, longitude));
            return new Tuple(landmarkID, quad);
        }
    }
}
