package Renderers;

import Models.GearSpec.GearSpec;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static javax.swing.text.StyleConstants.setIcon;

/**
 * Created by matthewyork on 4/1/14.
 */

public class GearSpecCellRenderer extends JPanel implements ListCellRenderer {
    private static final Color HIGHLIGHT_COLOR = Color.decode("0x2B2B2B");

    JLabel nameLabel;
    JLabel imageLabel;

    public GearSpecCellRenderer() {
        setOpaque(true);

    }

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
    {
        GearSpec spec = (GearSpec)value;

        //Check for first runthrough
        if(nameLabel == null){
            //Set layout
            this.setLayout(new FlowLayout(FlowLayout.LEFT));

            //Set name label
            nameLabel = new JLabel(spec.getName(), JLabel.LEFT);
            nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.PLAIN, 18));

            //Set image
            BufferedImage image = null;
            try {
                URL url = null;

                if (spec.getIcon() != null){
                    url = new URL(spec.getIcon());
                }
                else {
                    url = new URL("http://www.mkyong.com/image/mypic.jpg");
                }

                image = ImageIO.read(url);
                image = resizeImage(image, 35,35, Image.SCALE_DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }


            imageLabel = new JLabel(new ImageIcon(image));

            //Add components
            this.add(imageLabel);
            this.add(nameLabel);
        }
        else {
            nameLabel.setText(spec.getName());

            //Set image
            BufferedImage image = null;
            try {
                URL url = null;

                if (spec.getIcon() != null){
                    url = new URL(spec.getIcon());
                }
                else {
                    url = new URL("http://www.mkyong.com/image/mypic.jpg");
                }

                image = ImageIO.read(url);
                image = resizeImage(image, 35,35, Image.SCALE_FAST);
            } catch (IOException e) {
                e.printStackTrace();
            }


            imageLabel.setIcon(new ImageIcon(image));
        }

        if(isSelected) {
            setBackground(HIGHLIGHT_COLOR);
            setOpaque(true);
            //setForeground(Color.white);
        } else {
            setOpaque(false);
            //setForeground(Color.black);
        }
        return this;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type) throws IOException {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
}
