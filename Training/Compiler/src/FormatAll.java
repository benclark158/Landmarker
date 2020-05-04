import Helpers.FileIO;
import Helpers.HelperFunctions;
import Helpers.ICallback;
import Helpers.Values;

public class FormatAll {

    /**
     * Formats the images by resizing them to a given size
     * @throws Exception
     */
    public void run() throws Exception {

        ICallback callback = new ICallback() {
            @Override
            public void invoke(Object... args) throws Exception {
                String line = String.valueOf(args[0]);
                String[] data = line.split(",");
                String url = data[1];

                //does resizing
                HelperFunctions.resizeImg(url, Values.IMAGE_WIDTH, Values.IMAGE_HEIGHT);
            }
        };

        FileIO.readFileWithCallBack("E:\\Dissertation\\Landmarker\\Training\\formattedData.csv", callback, null);
    }
}
