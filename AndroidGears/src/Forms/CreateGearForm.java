package Forms;

import CustomPanel.ImagePanel;
import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecAuthor;
import Models.GearSpec.GearSpecSource;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
    private JTextField txtLibraryTag;
    private JTextField txtAuthorName;
    private JTextField txtAuthorEmail;
    private JButton btnAddAuthor;
    private JButton btnAddDependency;
    private JTextField txtProjectTags;
    private JPanel imgProjectIcon;
    private JTextArea txtProjectSummary;
    private JButton btnCreateAndroidGearSpec;
    private Gson gson;
    private GearSpec newSpec;


    public CreateGearForm() {
        this.gson = new Gson();
        initImageURLPanel();
        initButtons();
    }

    private void initButtons() {
        btnCreateAndroidGearSpec.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newSpec = CreateNewGearSpec(newSpec);
                System.out.print(gson.toJson(newSpec));
            }
        });
    }

    private GearSpec CreateNewGearSpec(GearSpec newSpec) {
        newSpec = new GearSpec();
        if(!txtProgectName.getText().isEmpty())
            newSpec.setName(txtProgectName.getText());

        if(!txtProjectMinSDK.getText().isEmpty())//TODO Also create a interger only check
            newSpec.setMinimum_api(Integer.parseInt(txtProjectMinSDK.getText()));

        if(!txtProjectVersion.getText().isEmpty())
            newSpec.setVersion(txtProjectVersion.getText());

        if(!txtProjectTags.getText().isEmpty())
            newSpec.setTags(ParseStringWithCommas(txtProjectTags.getText()));

        if(!txtImageURL.getText().isEmpty())// TODO check for valid url
            newSpec.setIcon(txtImageURL.getText());

        if(!txtLibraryTag.getText().isEmpty() && !txtSourceLibLocation.getText().isEmpty() && !txtSourceURL.getText().isEmpty())//TODO check for all 3 urls and file paths source must end with .git
            newSpec.setSource(new GearSpecSource(txtSourceURL.getText(),txtSourceLibLocation.getText(),txtLibraryTag.getText()));

        if(!txtAuthorName.getText().isEmpty()&&!txtAuthorEmail.getText().isEmpty())
            newSpec.setAuthors(CreateAuthorsArray());

        if(!txtProjectSummary.getText().isEmpty())
            newSpec.setSummary(txtProjectSummary.getText());

        newSpec.setRelease_notes("Nothing to see here.");
        newSpec.setType("jar");
        newSpec.setCopyright("That Other Guy 2014");
        newSpec.setHomepage("www.google.com");

        return newSpec;
    }



    private ArrayList<GearSpecAuthor> CreateAuthorsArray() {
        ArrayList<GearSpecAuthor> authors = new ArrayList<GearSpecAuthor>();
        authors.add(new GearSpecAuthor(txtAuthorName.getText(),txtAuthorEmail.getText()));
        return authors;
    }

    //TODO Create checks.
    private ArrayList<String> ParseStringWithCommas(String text) {
        String[] Strings = text.split(",");
        ArrayList<String> temp = new ArrayList<String>();
        for(int i = 0 ; i < Strings.length; i++){
            //Check for Spaces in front of the String.
            Strings[i] = RemovePrecedingSpaces(Strings[i]);
            //Check for Spaces at end of String.
            Strings[i] = RemoveSpacesAtEnd(Strings[i]);
            //Is empty check
            if(!Strings[i].isEmpty()){
                temp.add(Strings[i]);
            }
        }
        return temp;
    }
    //TODO Check if valid

    /**
     * Checks for spaces at the end of the string and recursively calls itself until the deed is finished
     * @param string String spaces are checked on
     * @return
     */
    private String RemovePrecedingSpaces(String string) {
        if(string.startsWith(" ")){
            string = string.substring(1);
            RemovePrecedingSpaces(string);
        }
        return string;
    }
    //TODO Check if valid
    /**
     * Checks for spaces in front of the string and recursively calls itself until the deed is finished
     * @param string String spaces are checked on
     * @return
     */
    private String RemoveSpacesAtEnd(String string) {
        if(string.endsWith(" ")){
            string = string.substring(0,string.length()-1);
            RemoveSpacesAtEnd(string);
        }
        return string;
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
