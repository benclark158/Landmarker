import com.google.common.collect.Lists;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BreakApart {
    public void run() throws IOException {
        List<String> lineList = new ArrayList<>();

        File f = new File("E:\\Dissertation\\Landmarker\\Training\\formattedData.csv");    //creates a new file instance
        FileReader fr = new FileReader(f);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        String line = "";

        int i = 0;

        while ((line = br.readLine()) != null) {
            if (line.contains("landmark")) {
                continue;
            } else if (i % 1000 == 0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now) + "\t\tProgress: " + i);
            }
            lineList.add(line);
            i++;
        }

        List<List<String>> files = Lists.partition(lineList, lineList.size() / 10);

        File targetFile = new File("partitionedDataset/");
        if(!targetFile.exists()){
            targetFile.mkdir();
        }

        i = 0;
        for(List<String> file : files){

            FileWriter writer = new FileWriter("partitionedDataset/partition_" + i + ".csv", true);
            writer.write("landmarkID,url,actual_latitude,actual_longitude,noise_lat,noise_long\r\n");

            for(String lines : file){
                writer.write(lines + "\r\n");
            }
            writer.close();
            i++;
        }
    }
}
