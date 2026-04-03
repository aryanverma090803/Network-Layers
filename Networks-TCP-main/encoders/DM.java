package encoders;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DM {

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
            int prevGray = 128; // start mid-level

            for (int x = 0; x < width; x += 200) {
                int y = height / 2;
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int gray = (r + g + b) / 3;

                if (gray > prevGray)
                    bits.append("1");
                else
                    bits.append("0");

                prevGray = gray;
            }

            return bits.toString();

        } catch (IOException e) {
            System.out.println("Error reading image: " + e.getMessage());
            return "";
        }
    }
}
