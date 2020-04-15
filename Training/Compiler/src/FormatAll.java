import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatAll {

    public void run() throws IOException {
        //resizeImg("\\TrainingData\\GoogleDataset\\photos\\1896\\036b12e3c331a6e2.jpg");

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

            String[] data = line.split(",");
            String url = data[1];

            resizeImg(url);

            i++;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now) + "\t\tProgress: " + i);
    }

    private void resizeImg(String location){
        String baseLoc = "E:\\Dissertation\\Landmarker\\Training";
        location = baseLoc + location;
        File ff = new File(location);

        try {
            //inputStream.reset();
            BufferedImage img = ImageIO.read(ff);
            int size = 224;

            if(img.getWidth() == img.getHeight() && img.getWidth() == size) {
                return;
            }

            BufferedImage newImg = resize(img, size, size);

            ImageIO.write(newImg, "jpg", new File(location).getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
            //ff.delete();
        }
    }

    private BufferedImage resize(BufferedImage img, int newW, int newH) throws Exception{
        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage dimg = new BufferedImage(newW, newH, img.getType() == 0 ? 2 : img.getType());//img.getType());
        Graphics2D g = dimg.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();

        return dimg;
    }
}
