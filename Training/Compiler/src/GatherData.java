import Helpers.Tuple;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class GatherData {

    /**
     * This is mainly used to generate a complete csv containing the data needed for the hard coded
     * data values within the app. This may not be required for future versions of the app.
     * @throws IOException
     */
    public void gatherDataFromIDs() throws IOException {

        HashMap<Integer, Integer> mapping = new HashMap<>();

        File f = new File("E:\\Dissertation\\Landmarker\\Training\\Compiler\\idMapping.csv");    //creates a new file instance
        FileReader fr = new FileReader(f);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        String line = "";

        int i = 0;

        while ((line = br.readLine()) != null) {
            if (line.contains("oldID")) {
                continue;
            } else if (i % 10000 == 0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now) + "\t\tProgress: " + i);
            }
            i++;
            String[] parts = line.split(",");

            mapping.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }

        HashMap<Integer, Tuple<String, String>> names = this.getNames(mapping);
        HashMap<Integer, Tuple<Float, Float>> gps = this.getGPS(mapping);

        //trent building and portland building do not exist in wikipedia database
        names.put(999056, new Tuple<>("", "Trent Building"));
        names.put(999057, new Tuple<>("", "Portland Building"));

        FileWriter formatted = new FileWriter("labelData.csv", true);
        formatted.write("oldLID,newLID,url,name,latitude,longitude\r\n");

        for(int id : mapping.keySet()){
            String str = id + "," + mapping.get(id) + "," + names.get(id).a + "," + names.get(id).b + "," +
                    gps.get(id).a  + "," + gps.get(id).b + "\r\n";
            formatted.write(str);
        }

        formatted.close();
    }

    private HashMap<Integer, Tuple<Float, Float>> getGPS(HashMap<Integer, Integer> mapping) throws IOException {
        HashMap<Integer, Tuple<Float, Float>> gps = new HashMap<>();

        File f = new File("E:\\Dissertation\\Landmarker\\Training\\ComponentData\\completeDataset.csv");    //creates a new file instance
        FileReader fr = new FileReader(f);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        String line = "";

        int i = 0;

        while ((line = br.readLine()) != null) {
            if (line.contains("landmarkID")) {
                continue;
            } else if (i % 10000 == 0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now) + "\t\tProgress: " + i);
            }
            i++;
            String[] parts = line.split(",");

            if(parts.length > 6){
                throw new IndexOutOfBoundsException("Something bad?");
            }

            int id = Integer.parseInt(parts[0]);
            float latitude = Float.parseFloat(parts[2]);
            float longitude = Float.parseFloat(parts[3]);

            if(gps.get(id) == null && mapping.get(id) != null && mapping.get(id) >= 0){

                Tuple<Float, Float> t = new Tuple<>(latitude, longitude);
                gps.put(id, t);
            }
        }
        return gps;
    }

    private HashMap<Integer, Tuple<String, String>> getNames(HashMap<Integer, Integer> mapping) throws IOException {
        HashMap<Integer, Tuple<String, String>> names = new HashMap<>();

        File f = new File("E:\\Dissertation\\Landmarker\\Training\\ComponentData\\train_label_to_category.csv");    //creates a new file instance
        FileReader fr = new FileReader(f);   //reads the file
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
        String line = "";

        int i = 0;

        while ((line = br.readLine()) != null) {
            if (line.contains("landmark_id")) {
                continue;
            } else if (i % 10000 == 0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now) + "\t\tProgress: " + i);
            }
            i++;

            String[] parts = line.split(",", 2);

            if(parts.length > 2){
                System.out.println("OVER 2");
                throw new IndexOutOfBoundsException("Something bad?");
            }

            int id = Integer.parseInt(parts[0]);
            String url = parts[1];

            if(names.get(id) == null && mapping.get(id) != null && mapping.get(id) >= 0){
                String name = url.split("Category:")[1];

                Tuple<String, String> t = new Tuple<>(url, name);
                names.put(id, t);
            }
        }
        return names;
    }
}
