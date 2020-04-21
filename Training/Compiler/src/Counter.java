import org.omg.CORBA.ARG_IN;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Counter {

    public void run() throws IOException {
        //get amount of all landmarks

        HashMap<Integer, Integer> counter = new HashMap<>();
        List<Tuple<Integer, String>> lines = new ArrayList<>();

        File f = new File("E:\\Dissertation\\Landmarker\\Training\\ComponentData\\completeDataset.csv");    //creates a new file instance
        FileReader fr = new FileReader(f);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        String line = "";

        ArrayList<Thread> threadList = new ArrayList<>();

        int i = 0;

        while ((line = br.readLine()) != null) {
            if (line.contains("landmarkID")) {
                continue;
            } else if (i % 100 == 0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now) + "\t\tProgress: " + i);
            }
            i++;

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
        }

        List<Integer> landmarksOver100 = new ArrayList<>();

        for(Integer id : counter.keySet()){
            if(counter.get(id) >= 250){
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
    }
}
