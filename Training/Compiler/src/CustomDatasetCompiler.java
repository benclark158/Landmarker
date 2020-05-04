import Helpers.HelperFunctions;
import Helpers.Tuple;

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

    public static final String OUTPUT_FILE_NAME = "uonTrainingTest.csv";
    public static final String DATASET_LOCATION = "..\\TrainingData\\UoNDataset\\";

    /**
     * Compiles images from custom dataset
     * @throws IOException
     */
    public void compileCustomDatasetFromFolders() throws IOException {
        this.compileCustomDatasetFromFolders(OUTPUT_FILE_NAME, DATASET_LOCATION);
    }

    /**
     * Compiles images from custom dataset
     * @param outputFilename
     * @param datasetLocation
     * @throws IOException
     */
    public void compileCustomDatasetFromFolders(String outputFilename, String datasetLocation) throws IOException {
        Random random = new Random();

        //gets list of all landmarks in given dir
        List<String> landmarks = new ArrayList<>();
        HashMap<String, Integer> landmarkNumbers = new HashMap<>();
        AtomicInteger i = new AtomicInteger();

        //loads files and their paths
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

        //iterates over all files found
        for (String landmark : landmarks) {
            String landmarkImgLoc = datasetLocation + landmark;
            File imgFile = new File(landmarkImgLoc);
            Files.list(imgFile.toPath()).forEach(path -> {
                //System.out.println(path.getFileName().toString());
                String filename = path.getFileName().toString();
                //if text file, parse as gps data
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

        //output to file in specified format
        FileWriter writer = new FileWriter(outputFilename, true);
        writer.write("landmarkID,url,actual_latitude,actual_longitude,noise_lat,noise_long\r\n");
        for(Tuple<String, String> t : landmarkImgPair){
            String landmark = t.a;
            Integer landmarkNumber = landmarkNumbers.get(landmark);
            String imgURL = datasetLocation + landmark + "\\" + t.b;

            imgURL = imgURL.replace("..\\", "/").replace("\\", "/");

            Tuple<Float, Float> originalLatLong = landmarkLocation.get(landmark);
            float latitude = originalLatLong.a;
            float longitude = originalLatLong.b;

            //add noise
            Tuple<Float, Float> latlong = HelperFunctions.addNoiseToLatLong(latitude, longitude, random);
            float nLat = latlong.a;
            float nLong = latlong.b;

            //write to file
            String line = landmarkNumber + "," + imgURL + "," + latitude + "," + longitude + "," + nLat + "," + nLong + "\r\n";
            writer.write(line);
        }
        writer.close();
    }
}
