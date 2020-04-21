import com.sun.crypto.provider.HmacPKCS12PBESHA1;
import com.sun.xml.internal.ws.api.pipe.Tube;

import java.io.*;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapLandmarks {

    public void run() throws IOException {
        Tuple<List<String>, List<String>> t = this.getLines();
        this.outputLines(t.a, t.b);
    }

    private Tuple<List<String>, List<String>> getLines() throws IOException {
        List<String> newLines = new ArrayList<>();
        List<String> mapingLines = new ArrayList<>();
        HashMap<Integer, Integer> map = new HashMap<>();

        File f = new File("E:\\Dissertation\\Landmarker\\Training\\Compiler\\limitedData.csv");    //creates a new file instance
        FileReader fr = new FileReader(f);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        String line = "";

        int i = 0;

        while ((line = br.readLine()) != null) {
            if (line.contains("landmark")) {
                continue;
            } else if (i % 10000 == 0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now) + "\t\tProgress: " + i);
            }

            String[] array = line.split(",");
            int landmarkID = Integer.parseInt(array[0]);
            String rest = line.replace(String.valueOf(landmarkID) + ",", ",");

            int newID;

            if(map.containsKey(landmarkID)){
                //mapping exists
                newID = map.get(landmarkID);
            } else {
                //create mapping
                newID = map.values().size();
                map.put(landmarkID, newID);
            }

            String newLine = newID + rest;
            newLines.add(newLine);

            i++;
        }

        for (Integer key : map.keySet()){
            mapingLines.add(key + "," + map.get(key));
        }

        return new Tuple<>(newLines, mapingLines);
    }

    private void outputLines(List<String> lines, List<String> mappingLines) throws IOException {
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
