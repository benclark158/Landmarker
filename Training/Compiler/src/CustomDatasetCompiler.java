import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomDatasetCompiler {

    public CustomDatasetCompiler(){

    }

    public void start() throws IOException {
        Random random = new Random();
        String datasetLocation = "..\\TrainingData\\UoNDataset\\";

        //gets list of all landmarks in given dir
        List<String> landmarks = new ArrayList<>();
        HashMap<String, Integer> landmarkNumbers = new HashMap<>();
        AtomicInteger i = new AtomicInteger();

        File datasetFile = new File(datasetLocation);
        Files.list(datasetFile.toPath()).forEach(path -> {
                    //System.out.println(path.getFileName().toString());
                    String strPath = path.getFileName().toString();
                    if (strPath.toLowerCase().contains("trent") || strPath.toLowerCase().contains("portland") || false) {
                        i.getAndIncrement();
                        landmarkNumbers.put(strPath, 999000 + i.get());
                        landmarks.add(path.getFileName().toString());
                    }
                });

        List<Tuple<String, String>> landmarkImgPair = new ArrayList<>();
        HashMap<String, Tuple<Float, Float>> landmarkLocation = new HashMap<>();

        for (String landmark : landmarks) {
            String landmarkImgLoc = datasetLocation + landmark;
            File imgFile = new File(landmarkImgLoc);
            Files.list(imgFile.toPath()).forEach(path -> {
                //System.out.println(path.getFileName().toString());
                String filename = path.getFileName().toString();
                if(filename.endsWith(".txt")){
                    String gps = filename.replace(".txt", "");
                    String[] coords = gps.split("#");
                    Tuple<Float, Float> latLong = new Tuple<>(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]));
                    landmarkLocation.put(landmark, latLong);
                } else {
                    Tuple<String, String> pair = new Tuple<>(landmark, filename);
                    landmarkImgPair.add(pair);
                }
            });
        }

        FileWriter writer = new FileWriter("uonTrainingTest.csv", true);
        writer.write("landmarkID,url,actual_latitude,actual_longitude,noise_lat,noise_long\r\n");
        for(Tuple<String, String> t : landmarkImgPair){
            String landmark = t.a;
            Integer landmarkNumber = landmarkNumbers.get(landmark);
            String imgURL = datasetLocation + landmark + "\\" + t.b;

            imgURL = imgURL.replace("..\\", "/").replace("\\", "/");

            Tuple<Float, Float> originalLatLong = landmarkLocation.get(landmark);
            float latitude = originalLatLong.a;
            float longitude = originalLatLong.b;

            Tuple<Float, Float> latlong = this.addNoiceToLatLong(latitude, longitude, random);
            float nLat = latlong.a;
            float nLong = latlong.b;

            String line = landmarkNumber + "," + imgURL + "," + latitude + "," + longitude + "," + nLat + "," + nLong + "\r\n";
            writer.write(line);
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
}
