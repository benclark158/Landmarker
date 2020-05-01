import Helpers.FileIO;
import Helpers.ICallback;
import Helpers.Tuple;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapLandmarks {

    public void run() throws Exception {
        Tuple<List<String>, List<String>> t = this.getLines();
        this.outputLines(t.a, t.b);
    }

    protected Tuple<List<String>, List<String>> getLines() throws Exception {
        List<String> newLines = new ArrayList<>();
        List<String> mapingLines = new ArrayList<>();
        HashMap<Integer, Integer> map = new HashMap<>();

        ICallback callback = args -> {
            String line = String.valueOf(args[0]);

            String[] array = line.split(",");
            int landmarkID = Integer.parseInt(array[0]);
            String rest = line.replace(String.valueOf(landmarkID) + ",", ",");

            int newID;

            if (map.containsKey(landmarkID)) {
                //mapping exists
                newID = map.get(landmarkID);
            } else {
                //create mapping
                newID = map.values().size();
                map.put(landmarkID, newID);
            }

            String newLine = newID + rest;
            newLines.add(newLine);

        };

        FileIO.readFileWithCallBack("E:\\Dissertation\\Landmarker\\Training\\Compiler\\limitedData.csv", callback);

        for (Integer key : map.keySet()){
            mapingLines.add(key + "," + map.get(key));
        }

        return new Tuple<>(newLines, mapingLines);
    }

    protected void outputLines(List<String> lines, List<String> mappingLines) throws IOException {
        FileWriter formatted = new FileWriter("lim-formattedData.csv", true);
        formatted.write("landmarkID,url,actual_latitude,actual_longitude,noise_lat,noise_long\r\n");

        for(String line : lines){
            formatted.write(line.replace(",/TrainingData/", ",C:/Users/Ben Clark/Desktop/TrainingData/") + "\r\n");
        }
        formatted.close();

        FileWriter mapping = new FileWriter("idMapping.csv", true);
        mapping.write("oldID,newID\r\n");

        for(String line : mappingLines){
            mapping.write(line + "\r\n");
        }
        mapping.close();
    }
}
