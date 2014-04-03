package Forms;

import CustomPanel.ImagePanel;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by matthewyork on 4/2/14.
 */
public class CreateGearForm {
    public JPanel MasterPanel;
    private JTextField txtImageURL;
    private JTextField txtProgectName;
    private JTextField txtProjectMinSDK;
    private JTextField txtProjectVersion;
    private JTextField txtSourceURL;
    private JTextField txtSourceLibLocation;
    private JTextField textField1;
    private JTextField txtAuthorName;
    private JTextField txtAuthorEmail;
    private JButton btnAddAuthor;
    private JButton btnAddDependency;
    private JTextField txtProjectTags;
    private JPanel imgProjectIcon;
    private JTextArea txtProjectSummary;
    private JButton btnCreateAndroidGearSpec;
    private Gson gson;


    public CreateGearForm() {
        this.gson = new Gson();
        initImageURLPanel();
    }

    private void initImageURLPanel() {
        txtImageURL.setText("http://www.mkyong.com/image/mypic.jpg");
//        txtImageURL.setText("resources/icons/gears@2x.png");
//        checkImageURL(txtImageURL.getText());
//        txtImageURL.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//               checkImageURL(txtImageURL.getText());
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                checkImageURL(txtImageURL.getText());
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                checkImageURL(txtImageURL.getText());
//            }
//        });

    }
// TODO THIS DOESNT WORK DO NOT REUSE JUST YET
    private void checkImageURL(String urlString) {
//        JFrame imageFrame = new JFrame();
//        Image image = null;
//        JLabel label;
        ImagePanel imagePanel = new ImagePanel();
        imagePanel.setSize(70,70);
        imagePanel.setImage("http://www.mkyong.com/image/mypic.jpg");
        imgProjectIcon.add(imagePanel);

//        if(urlString.contains("www.")){
//            imgProjectIcon.setImage("http://www.mkyong.com/image/mypic.jpg");
//            try {
//                URL url = new URL(urlString);
//                image = ImageIO.read(url);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            label = new JLabel(new ImageIcon(image));
//            imageFrame.add(label);
//            imageFrame.setVisible(true);
//        }else{
//            try {
//                File file = new File("/resources/icons/gears@2x.png");
//                image = ImageIO.read(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            label = new JLabel(new ImageIcon(image));
//            imageFrame.add(label);
//            imageFrame.setVisible(true);
//        }
    }
}
