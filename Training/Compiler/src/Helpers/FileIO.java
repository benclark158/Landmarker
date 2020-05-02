package Helpers;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileIO {

    public static void readFileWithCallBack(String filePath, ICallback during) throws Exception {
        readFileWithCallBack(filePath, during, null);
    }

    public static void readFileWithCallBack(String filePath, ICallback during, ICallback post) throws Exception {
        List<String> lines = readFile(filePath);

        for (String line : lines) {
            during.invoke(line);
        }

        if(post != null) {
            post.invoke();
        }
    }

    public static List<String> readFile(String filePath) throws IOException {
        File f = new File(filePath);    //creates a new file instance
        FileReader fr = new FileReader(f);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        String line = "";

        List<String> list = new ArrayList<>();

        int i = 0;

        while ((line = br.readLine()) != null) {
            if (line.contains("landmarkID") || line.contains("landmark_id")) {
                continue;
            } else if (i % 100 == 0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now) + "\t\tProgress: " + i);
            }
            i++;
            list.add(line);
        }

        return list;
    }
}
