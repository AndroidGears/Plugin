package CustomPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created by aaronfleshner on 4/3/14.
 */
public class ImagePanel extends JPanel {

    private BufferedImage image;

    public ImagePanel() {

    }

    public void setImage(String url) {
        try {
            image = ImageIO.read(new URL(url));
        } catch (IOException ex) {
            // handle exception...
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters
    }

}
