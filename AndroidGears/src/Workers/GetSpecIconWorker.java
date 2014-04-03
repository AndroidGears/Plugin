package Workers;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created by matthewyork on 4/3/14.
 */
public class GetSpecIconWorker extends SwingWorker<Void, Void> {
    public ImageIcon icon = null;
    private String UrlString;

    public GetSpecIconWorker(String urlString) {
        UrlString = urlString;
    }

    @Override
    protected Void doInBackground() throws Exception {
        URL  url;
        if (this.UrlString == null){
            url = new URL("https://raw.githubusercontent.com/AndroidGears/Resources/master/Logos/Logo80.jpg");

        }
        else {
            url = new URL(this.UrlString);
        }

        try {

            Image image = ImageIO.read(url);
            image = resizeImage((BufferedImage)image, 80,80, Image.SCALE_FAST);
            this.icon = new ImageIcon(image);
        } catch (IOException e) {
        }


        return null;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type) throws IOException {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
}
