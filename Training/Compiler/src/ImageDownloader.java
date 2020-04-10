import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageDownloader {

    private int failedImages, downloaded, skipped;

    public void collectImages() throws IOException {
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

        System.out.println("Downloading Images...");

        this.failedImages = 0;
        this.downloaded = 0;
        this.skipped = 0;

        File f = new File("E:\\Dissertation\\Landmarker\\Training\\train.csv");    //creates a new file instance
        FileReader fr = new FileReader(f);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        line = "";

        ArrayList<Thread> threadList = new ArrayList<>();

        int i = 0;

        while ((line = br.readLine()) != null) {
            if (line.contains("landmark_id")) {
                continue;
            } else if (i % 100 == 0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now) + "\t\tProgress: " + i + "\t\tDownloads: " + this.downloaded + "\t\tFailures: " + this.failedImages + "\t\tSkipped: " + this.skipped);
            }
            i++;

            Pattern p = Pattern.compile("([0-9a-z]+),([\"])*([a-zA-Z0-9:/_,.()!%\\-']+)([\"])*,([0-9]+)(\\r\\n)*");
            Matcher m = p.matcher(line);

            if (m.matches()) {
                String id = m.group(1);
                String url = m.group(3);
                int landmarkID = Integer.parseInt(m.group(5));

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

                    for(Thread thread : threadList){
                        if(!th.isAlive()){
                            removeList.add(th);
                        }
                    }
                    for(Thread thread : removeList){
                        if(!th.isAlive()){
                            threadList.remove(th);
                        }
                    }

                    if(threadList.size() > 50){
                        for(Thread thread : threadList){
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

        for(Thread th : threadList){
            if(th.isAlive()){
                try {
                    th.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Completed: " + this.downloaded);
        System.out.println("Failed Image Resizes: " + this.failedImages);
    }

    public void downloadAndResize(String strURL, String id, int landmarkID) {
        String fileLoc = "E:\\Dissertation\\Landmarker\\Training\\TrainingData\\GoogleDataset\\";
        File f = new File(fileLoc + "photos/" + landmarkID + "/");

        String name = landmarkID + "/" + id;
        String location = fileLoc + "photos/" + name + ".jpg";

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

        }


        //BufferedImage image = resize(img, size, size);

        File ff = new File(location);

        try {
            //inputStream.reset();
            BufferedImage img = ImageIO.read(ff);
            int size = 244;

            BufferedImage newImg = resize(img, size, size);

            ImageIO.write(newImg, "jpg", new File(location).getAbsoluteFile());
            this.downloaded++;
        } catch (Exception e) {
            //e.printStackTrace();
            ff.delete();
            this.failedImages++;
        }
    }


    public BufferedImage resize(BufferedImage img, int newW, int newH) throws Exception{
        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,

                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();

        return dimg;
    }
}
