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
        List<String> lines = new ArrayList<>();

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

            lines.add(line);

            String[] split = line.split(",");
            int landmarkID = Integer.parseInt(split[0]);

            int currentCount = 0;

            if(counter.containsKey(counter)){
                currentCount = counter.get(landmarkID);
                counter.remove(landmarkID);
            }

            currentCount++;

            counter.put(landmarkID, currentCount);
        }

        List<Integer> landmarksOver100 = new ArrayList<>();

        for(Integer id : counter.keySet()){
            if(counter.get(id) >= 100){
                landmarksOver100.add(id);
            }
        }


    }
}
