package Forms;

import CustomPanel.ImagePanel;
import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecAuthor;
import Models.GearSpec.GearSpecDependency;
import Models.GearSpec.GearSpecSource;
import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by AaronFleshner on 4/2/14.
 */
public class CreateGearForm {
    public JPanel MasterPanel;

    private JTextField txtProjectName;
    private JTextField txtHomePage;
    private JTextField txtLicense;
    private JTextField txtCopyRight;
    private JTextField txtProjectMinSDK;
    private JTextField txtProjectVersion;
    private JTextField txtSourceURL;
    private JTextField txtSourceLibLocation;
    private JTextField txtLibraryTag;
    private JTextField txtAuthorName;
    private JTextField txtAuthorEmail;
    private JTextField txtProjectTags;
    private JTextField txtDependencyName;
    private JTextField txtDependencyVersion;

    private JTable authorsTable;
    private DefaultTableModel AuthorModel;
    private JTable dependencyTable;
    private DefaultTableModel DependencyModel;

    private JButton btnAddAuthor;
    private JButton btnAddDependency;
    private JButton btnCreateAndroidGearSpec;
    private JButton btnRemoveAuthor;
    private JButton btnRemoveDependency;

    private JTextArea txtReleaseNotes;
    private JTextArea txtProjectSummary;
    private JComboBox cbLibraryType;
    private JComboBox cbMinSDK;

    private Gson gson;
    private ArrayList<GearSpecAuthor> authors = new ArrayList<GearSpecAuthor>();
    private ArrayList<GearSpecDependency> dependencies = new ArrayList<GearSpecDependency>();

    private GearSpec newSpec;



    public CreateGearForm() {
        this.gson = new Gson();
        initAuthorTable();
        initDependenciesTable();
        initButtons();
    }

    private void initAuthorTable() {
        ArrayList<GearSpecAuthor> authors = new ArrayList<GearSpecAuthor>();
        AuthorModel = (DefaultTableModel) authorsTable.getModel();
        AuthorModel.addColumn("Author's Name");
        AuthorModel.addColumn("Author's Email");
        AddAllNewAuthors(authors,AuthorModel);
    }
    private void initDependenciesTable() {//TODO add lint for the same library being added twice
        ArrayList<GearSpecDependency> dependencies = new ArrayList<GearSpecDependency>();
        DependencyModel = (DefaultTableModel) dependencyTable.getModel();
        DependencyModel.addColumn("Dependency's Name");
        DependencyModel.addColumn("Dependency's Version");
        AddAllNewDependencies(dependencies, DependencyModel);
    }

    private void AddAllNewAuthors(ArrayList<GearSpecAuthor> authors,DefaultTableModel model){
        for (GearSpecAuthor author : authors) {
            AddNewAuthor(author,model);
        }
    }

    private void AddNewAuthor(GearSpecAuthor gearSpecAuthor,DefaultTableModel model) {
        model.addRow(new Object[]{gearSpecAuthor.getName(),gearSpecAuthor.getEmail()});
    }


    private void AddAllNewDependencies(ArrayList<GearSpecDependency> dependencies,DefaultTableModel model){
        for (GearSpecDependency dependency : dependencies) {
            AddNewDependency(dependency, model);
        }
    }

    private void AddNewDependency(GearSpecDependency gearSpecDependency,DefaultTableModel model) {
        model.addRow(new Object[]{gearSpecDependency.getName(),gearSpecDependency.getVersion()});
    }



    private void initButtons() {
        btnCreateAndroidGearSpec.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newSpec = CreateNewGearSpec();
                System.out.print(gson.toJson(newSpec));
            }
        });

        btnAddAuthor.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!txtAuthorName.getText().isEmpty()&&!txtAuthorEmail.getText().isEmpty()){
                    AuthorModel.addRow(new Object[]{txtAuthorName.getText(),txtAuthorEmail.getText()});
                    authors.add(new GearSpecAuthor(txtAuthorName.getText(),txtAuthorEmail.getText()));
                    txtAuthorName.setText("");
                    txtAuthorEmail.setText("");

                }
            }
        });

        btnAddDependency.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!txtDependencyName.getText().isEmpty()&&!txtDependencyVersion.getText().isEmpty()){
                    DependencyModel.addRow(new Object[]{txtDependencyName.getText(),txtDependencyVersion.getText()});
                    dependencies.add(new GearSpecDependency(txtDependencyName.getText(),txtDependencyVersion.getText()));
                    txtDependencyName.setText("");
                    txtDependencyVersion.setText("");
                }
            }
        });
        btnRemoveAuthor.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AuthorModel.removeRow(authorsTable.getSelectedRow());
                authors.remove(authorsTable.getSelectedRow());
            }
        });

        btnRemoveDependency.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DependencyModel.removeRow(dependencyTable.getSelectedRow());
                dependencies.remove(dependencyTable.getSelectedRow());
            }
        });
    }

    /**
     * Creates the Android Gear Spec Object
      */
    private GearSpec CreateNewGearSpec() {
        GearSpec newSpec = new GearSpec();
        if(!txtProjectName.getText().isEmpty())
            newSpec.setName(txtProjectName.getText());



        if(!txtProjectVersion.getText().isEmpty())
            newSpec.setVersion(txtProjectVersion.getText());

        if(!txtProjectTags.getText().isEmpty())
            newSpec.setTags(ParseStringWithCommas(txtProjectTags.getText()));

        if(!txtLibraryTag.getText().isEmpty() && !txtSourceLibLocation.getText().isEmpty() && !txtSourceURL.getText().isEmpty())//TODO check for all 3 urls and file paths source must end with .git
            newSpec.setSource(new GearSpecSource(txtSourceURL.getText(),txtSourceLibLocation.getText(),txtLibraryTag.getText()));

        if(!txtProjectSummary.getText().isEmpty())
            newSpec.setSummary(txtProjectSummary.getText());

        if(!txtReleaseNotes.getText().isEmpty())
            newSpec.setRelease_notes(txtReleaseNotes.getText());

        if(!txtCopyRight.getText().isEmpty())
            newSpec.setCopyright(txtCopyRight.getText());

        if(!txtHomePage.getText().isEmpty())
            newSpec.setHomepage(txtHomePage.getText());

        if(!txtLicense.getText().isEmpty())
            newSpec.setLicense(txtLicense.getText());

        if(!authors.isEmpty())
            newSpec.setAuthors(authors);

        if(!dependencies.isEmpty())
            newSpec.setDependencies(dependencies);

        newSpec.setType(cbLibraryType.getSelectedItem().toString());
        newSpec.setMinimum_api(Integer.parseInt(cbMinSDK.getSelectedItem().toString()));

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

// TODO THIS DOESNT WORK DO NOT REUSE JUST YET
    private void checkImageURL(String urlString) {
//        JFrame imageFrame = new JFrame();
//        Image image = null;
//        JLabel label;

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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
