import Helpers.FileIO;
import Helpers.ICallback;
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
    public void gatherDataFromIDs() throws Exception {

        HashMap<Integer, Integer> mapping = new HashMap<>();

        ICallback callback = new ICallback() {
            @Override
            public void invoke(Object... args) throws Exception {
                String line = String.valueOf(args[0]);
                String[] parts = line.split(",");
                mapping.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
        };

        FileIO.readFileWithCallBack("E:\\Dissertation\\Landmarker\\Training\\Compiler\\idMapping.csv", callback);


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

    /**
     * gets gps data for each landmark id
     * @param mapping
     * @return
     * @throws IOException
     */
    private HashMap<Integer, Tuple<Float, Float>> getGPS(HashMap<Integer, Integer> mapping) throws Exception {
        HashMap<Integer, Tuple<Float, Float>> gps = new HashMap<>();

        ICallback callback = new ICallback() {
            @Override
            public void invoke(Object... args) throws Exception {
                String line = String.valueOf(args[0]);
                String[] parts = line.split(",");

                //bad parse? Shouldnt happen!
                if(parts.length > 6){
                    throw new IndexOutOfBoundsException("Something bad?");
                }

                int id = Integer.parseInt(parts[0]);
                float latitude = Float.parseFloat(parts[2]);
                float longitude = Float.parseFloat(parts[3]);

                //check valid names
                if(gps.get(id) == null && mapping.get(id) != null && mapping.get(id) >= 0){

                    //adds data to list
                    Tuple<Float, Float> t = new Tuple<>(latitude, longitude);
                    gps.put(id, t);
                }
            }
        };

        FileIO.readFileWithCallBack("E:\\Dissertation\\Landmarker\\Training\\ComponentData\\completeDataset.csv", callback);

        return gps;
    }

    /**
     * Gets name and url for each place
     * @param mapping
     * @return
     * @throws IOException
     */
    private HashMap<Integer, Tuple<String, String>> getNames(HashMap<Integer, Integer> mapping) throws Exception {
        HashMap<Integer, Tuple<String, String>> names = new HashMap<>();

        ICallback callback = new ICallback() {
            @Override
            public void invoke(Object... args) throws Exception {
                String line = String.valueOf(args[0]);
                String[] parts = line.split(",", 2);

                //checks 2 parts per line
                if(parts.length > 2){
                    System.out.println("OVER 2");
                    throw new IndexOutOfBoundsException("Something bad?");
                }

                //parse ints
                int id = Integer.parseInt(parts[0]);
                String url = parts[1];

                //check values are valid for this entry
                if(names.get(id) == null && mapping.get(id) != null && mapping.get(id) >= 0){
                    String name = url.split("Category:")[1];

                    //adds name
                    Tuple<String, String> t = new Tuple<>(url, name);
                    names.put(id, t);
                }
            }
        };

        FileIO.readFileWithCallBack("E:\\Dissertation\\Landmarker\\Training\\ComponentData\\train_label_to_category.csv", callback);

        return names;
    }
}
