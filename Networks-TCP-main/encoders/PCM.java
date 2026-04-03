package encoders;

import java.awt.image.BufferedImage; //image import into memory
import java.io.File; // file 
import java.io.IOException;
import javax.imageio.ImageIO; //file se image ko read ko 

public class PCM {

    public String encode(String imagePath) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePath));
            if (img == null) {
                System.out.println("Error: Unable to process image.");
                return "";
            }

            int width = img.getWidth();
            int height = img.getHeight();

            StringBuilder bits = new StringBuilder();

            for (int x = 0; x < width; x += 200) {
                int y = height / 2; 
                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int gray = (r + g + b) / 3;

                int level = (gray * 8) / 256;
                String bin = String.format("%3s", Integer.toBinaryString(level))
                                  .replace(' ', '0');
                bits.append(bin);
            }

            return bits.toString();

        } catch (IOException e) {
            System.out.println("Error reading image: " + e.getMessage());
            return "";
        }
    }
}




// In line 24 , we can increase or decrease the sampple gap , i.e , x += sampleGap 
// in our code , we sample every 5 pixels across the width of the image which means more samples and more output bits.