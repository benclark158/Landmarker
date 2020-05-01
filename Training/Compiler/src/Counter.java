import Helpers.FileIO;
import Helpers.ICallback;
import Helpers.Tuple;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Counter {

    public void outputTop100Landmarks() throws Exception {
        //get amount of all landmarks

        HashMap<Integer, Integer> counter = new HashMap<>();
        List<Tuple<Integer, String>> lines = new ArrayList<>();

        ICallback callback = args -> {
            String line = String.valueOf(args[0]);

            String[] split = line.split(",");
            int landmarkID = Integer.parseInt(split[0]);

            lines.add(new Tuple<>(landmarkID, line));

            int currentCount = 0;

            if(counter.containsKey(landmarkID)){
                currentCount = counter.get(landmarkID);
                counter.remove(landmarkID);
            }

            currentCount++;

            counter.put(landmarkID, currentCount);
        };

        ICallback post = args -> {
            List<Integer> landmarksOver100 = new ArrayList<>();

            for(Integer id : counter.keySet()){
                if(counter.get(id) >= 500){
                    landmarksOver100.add(id);
                }
            }

            FileWriter formatted = new FileWriter("limitedData.csv", true);
            formatted.write("landmarkID,url,actual_latitude,actual_longitude,noise_lat,noise_long\r\n");

            for(Tuple<Integer, String> t : lines){
                if(landmarksOver100.contains(t.a)) {
                    formatted.write(t.b + "\r\n");
                }
            }
            formatted.close();
        };

        FileIO.readFileWithCallBack("E:\\Dissertation\\Landmarker\\Training\\ComponentData\\completeDataset.csv", callback, post);
    }
}
