import Helpers.*;
import sun.awt.SunHints;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageDownloader {

    private int failedImages, downloaded, skipped;

    /**
     * Collects all of the images
     * @throws Exception
     */
    public void collectImages() throws Exception {
        //reads list of uk places
        this.doPostRun(HelperFunctions.getUKPlaces(null));

    }

    /**
     * Downloads images from the internet
     * @param ukMap
     */
    private void doPostRun(HashMap<Integer, QuadTuple<String, Float, Float, Boolean>> ukMap) {
        System.out.println("Downloading Images...");

        this.failedImages = 0;
        this.downloaded = 0;
        this.skipped = 0;

        ArrayList<Thread> threadList = new ArrayList<Thread>();

        ICallback during = new ICallback() {
            @Override
            public void invoke(Object... args) throws Exception {
                String line = String.valueOf(args[0]);

                //reads line from file
                Pattern p = Pattern.compile("([0-9a-z]+),([\"])*([a-zA-Z0-9:/_,.()!%\\-']+)([\"])*,([0-9]+)(\\r\\n)*");
                Matcher m = p.matcher(line);

                //checks it matches the specified pattern
                if (m.matches()) {
                    String id = m.group(1);
                    String url = m.group(3);
                    int landmarkID = Integer.parseInt(m.group(5));

                    //start threaded download process
                    QuadTuple<String, Float, Float, Boolean> quad = ukMap.get(landmarkID);
                    if (quad != null && quad.d) {
                        Thread th = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downloadAndResize(url, id, landmarkID);
                            }
                        });
                        th.start();
                        threadList.add(th);

                        ArrayList<Thread> removeList = new ArrayList<>();

                        //keep list of alive threads only
                        for (Thread thread : threadList) {
                            if (!th.isAlive()) {
                                removeList.add(th);
                            }
                        }
                        for (Thread thread : removeList) {
                            if (!th.isAlive()) {
                                threadList.remove(th);
                            }
                        }

                        //if more than 50 threads running
                        // wait for entire list to finish
                        if (threadList.size() > 50) {
                            for (Thread thread : threadList) {
                                try {
                                    th.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        };

        // wait for all threads to finish
        ICallback post = new ICallback() {
            @Override
            public void invoke(Object... args) throws Exception {
                for(Thread th : threadList){
                    if(th.isAlive()){
                        try {
                            th.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("Completed: " + downloaded);
                System.out.println("Failed Image Resizes: " + failedImages);
            }
        };


        try {
            FileIO.readFileWithCallBack("E:\\Dissertation\\Landmarker\\Training\\train.csv", during, post);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Downloads an image and resizes it
     * @param strURL
     * @param id
     * @param landmarkID
     */
    public void downloadAndResize(String strURL, String id, int landmarkID) {
        File f = new File(Values.DOWNLOAD_LOCATION + "photos/" + landmarkID + "/");

        String name = landmarkID + "/" + id;
        String location = Values.DOWNLOAD_LOCATION + "photos/" + name + ".jpg";

        if(!f.exists()){
            f.mkdirs();
        }
        File fLoc = new File(location);
        if(fLoc.exists()){
            //this.downloadedList.add(fLoc);
            this.skipped++;
            this.downloaded++;
            return;
        }

        //image download
        HelperFunctions.downloadImage(strURL, location);

        //resizes image
        try {
            HelperFunctions.resizeImg(
                    location,
                    Values.IMAGE_WIDTH,
                    Values.IMAGE_HEIGHT,
                    args -> downloaded++,
                    args -> failedImages++);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
