package Forms;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecAuthor;
import Models.GearSpec.GearSpecDependency;
import Models.GearSpec.GearSpecSource;
import Models.GearSpecLinter.GearSpecLintResult;
import Services.AutoCompleteSerivce.AutoCompleteDocument;
import Services.AutoCompleteSerivce.GearsService;
import Utilities.Utils;
import Workers.Lint.LintGearSpecWorker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
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
    private JButton btnLoadGearSpec;

    private Gson gson;
    private ArrayList<GearSpecAuthor> authors = new ArrayList<GearSpecAuthor>();
    private ArrayList<GearSpecDependency> dependencies = new ArrayList<GearSpecDependency>();

    private GearSpec newSpec;


    public CreateGearForm() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        initAuthorTable();
        initDependenciesTable();
        initButtons();
        initAutoCompleteDependencies();
    }

    private void initAutoCompleteDependencies() {
        GearsService gearsService = new GearsService();


        // Create the auto completing document model with a reference to the
        // service and the input field.
        Document autoCompleteDocument = new AutoCompleteDocument(gearsService,txtDependencyName);
        txtDependencyName.setDocument(autoCompleteDocument);
    }

    //Initialize the Table so that you are able to add authors to the table.
    private void initAuthorTable() {//TODO add lint for the same author being added twice
        ArrayList<GearSpecAuthor> authors = new ArrayList<GearSpecAuthor>();
        //Create AuthorModel model for AddAllNewAuthors to use.. To add coloumns
        AuthorModel = (DefaultTableModel) authorsTable.getModel();
        AuthorModel.addColumn("Author's Name");
        AuthorModel.addColumn("Author's Email");
        //Really not used yet but if you wanted to add authors by default we could save a file where you could read authors from.
        AddAllNewAuthors(authors);
    }

    private void initDependenciesTable() {//TODO add lint for the same library being added twice
        ArrayList<GearSpecDependency> dependencies = new ArrayList<GearSpecDependency>();
        //Create dependency Model for AddAllNewDependencies to use. To add coloumns
        DependencyModel = (DefaultTableModel) dependencyTable.getModel();
        DependencyModel.addColumn("Dependency's Name");
        DependencyModel.addColumn("Dependency's Version");
        //Really not used yet but if you wanted to add dependencies by default we could save a file where you could read dependencies from.
        AddAllNewDependencies(dependencies);
    }

    //populate authors from dependency list if there ary any
    private void AddAllNewAuthors(ArrayList<GearSpecAuthor> authors) {
        for (GearSpecAuthor author : authors) {
            AddNewAuthor(author);
        }
    }
    //adds author row to AuthorModel
    private void AddNewAuthor(GearSpecAuthor gearSpecAuthor) {
        //add new author to arrayList
        authors.add(gearSpecAuthor);
        //Update table
        AuthorModel.addRow(new Object[]{gearSpecAuthor.getName(), gearSpecAuthor.getEmail()});
    }

    //populate dependencies from the authors list if there are any.
    private void AddAllNewDependencies(ArrayList<GearSpecDependency> dependencies) {
        for (GearSpecDependency dependency : dependencies) {
            AddNewDependency(dependency);
        }
    }
    //adds dependency to DependencyModel
    private void AddNewDependency(GearSpecDependency gearSpecDependency) {
        //add new dependency to arraylist
        dependencies.add(gearSpecDependency);
        DependencyModel.addRow(new Object[]{gearSpecDependency.getName(), gearSpecDependency.getVersion()});
    }


    private void initButtons() {
        btnCreateAndroidGearSpec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newSpec = CreateNewGearSpec();
                lintSpec(newSpec);
            }
        });

        btnAddAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtAuthorName.getText().isEmpty() && !txtAuthorEmail.getText().isEmpty()) {
                    //add new author to table
                    AddNewAuthor(new GearSpecAuthor(txtAuthorName.getText(), txtAuthorEmail.getText()));

                    //clear out fields
                    txtAuthorName.setText("");
                    txtAuthorEmail.setText("");
                }
            }
        });

        btnAddDependency.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtDependencyName.getText().isEmpty() && !txtDependencyVersion.getText().isEmpty()) {
                    //add new dependency to table.
                    AddNewDependency(new GearSpecDependency(txtDependencyName.getText(), txtDependencyVersion.getText()));

                    //clear out fields
                    txtDependencyName.setText("");
                    txtDependencyVersion.setText("");
                }
            }
        });
        btnRemoveAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //remove author from table
                AuthorModel.removeRow(authorsTable.getSelectedRow());
                //remove author from arrayList
                authors.remove(authorsTable.getSelectedRow());
            }
        });

        btnRemoveDependency.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //remove dependency from table
                DependencyModel.removeRow(dependencyTable.getSelectedRow());
                //remove dependency from arraylist
                dependencies.remove(dependencyTable.getSelectedRow());
            }
        });

        btnLoadGearSpec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GearSpec spec = loadGearSpec();
                if (spec != null) {
                    //load Spec from file chooser
                    loadSpecIntoForm(spec);
                }
                else {
                    showLoadErrorDialog();
                }
            }
        });
    }

    //loads the spec into the form.
    private void loadSpecIntoForm(GearSpec spec) {
        txtProjectName.setText(spec.getName());
        txtProjectVersion.setText(spec.getVersion());
        cbMinSDK.setSelectedIndex(spec.getMinimum_api());
        cbLibraryType.setSelectedItem(spec.getType());
        txtProjectTags.setText(GetTags(spec.getTags()));
        txtLibraryTag.setText(spec.getSource().getTag());
        txtSourceLibLocation.setText(spec.getSource().getSource_files());
        txtSourceURL.setText(spec.getSource().getUrl());
        txtProjectSummary.setText(spec.getSummary());
        txtReleaseNotes.setText(spec.getRelease_notes());
        txtCopyRight.setText(spec.getCopyright());
        txtHomePage.setText(spec.getHomepage());
        txtLicense.setText(spec.getLicense());
        if(spec.getAuthors()!=null)
            AddAllNewAuthors(spec.getAuthors());
        if(spec.getDependencies()!=null)
            AddAllNewDependencies(spec.getDependencies());
    }



    //loads the tags into a string to set the tags.
    private String GetTags(ArrayList<String> tags) {
        String temp="";

        //Iterate over all tags and concatenate them as comma separated strings
        if (tags != null){
            for(String tag:tags){
                temp = temp.concat(tag)+" , ";
            }
            return temp.substring(0,temp.length()-3);
        }

        return temp;
    }

    //Make sure that spec meets Android GearSpec Requirements.
    private void lintSpec(final GearSpec spec) {
        LintGearSpecWorker worker = new LintGearSpecWorker(spec) {
            @Override
            protected void done() {
                super.done();

                if (result.getPassed()) {
                    if (saveSpec(spec)) {
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(MasterPanel);
                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    }
                } else {
                    showLintErrorDialog(result);
                }
            }
        };
        worker.execute();
    }
    //Save Spec to file on computer.
    private Boolean saveSpec(GearSpec spec) {

        //Get top level frame
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(MasterPanel);

        //Create dialog for choosing gearspec file
        FileDialog fd = new FileDialog(topFrame, "Save .gearspec file", FileDialog.SAVE);
        fd.setDirectory(System.getProperty("user.home"));
        // Gets the name that is specified for the spec in the beginning
        fd.setFile(spec.getName()+".gearspec");
        fd.setVisible(true);
        //Get file
        String filename = fd.getFile();
        if (filename == null) {
            System.out.println("You cancelled the choice");
            return false;
        }
        else {
            System.out.println("You chose " + filename);

            //Get spec file
            File specFile = new File(fd.getDirectory() + Utils.pathSeparator() + filename);

            //Serialize spec to string
            String gearString = gson.toJson(spec);

            try {
                //If it exists, set it as the selected file path
                if (specFile.exists()) {
                    FileUtils.forceDelete(specFile);
                }

                //Write new spec
                FileUtils.write(specFile, gearString);
            } catch (IOException e) {
                e.printStackTrace();
                showSaveErrorDialog();
                return false;
            }

            return true;
        }
    }
    //load gearspec from file on computer
    private GearSpec loadGearSpec() {
        //Get top level frame
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(MasterPanel);

        //Create dialog for choosing gearspec file
        FileDialog fd = new FileDialog(topFrame, "Choose a .gearspec file", FileDialog.LOAD);
        fd.setDirectory(System.getProperty("user.home"));
        fd.setFile("*.gearspec");
        fd.setVisible(true);
        //Get file
        String filename = fd.getFile();
        if (filename == null)
            System.out.println("You cancelled the choice");
        else {
            System.out.println("You chose " + filename);

            //Get spec file
            File specFile = new File(fd.getDirectory() + Utils.pathSeparator() + filename);

            //If it exists, set it as the selected file path
            if (specFile.exists()) {
                //Generate spec
                return Utils.specForFile(specFile);
            }
        }

        return null;
    }

    /**
     * Creates the Android Gear Spec Object
     */
    private GearSpec CreateNewGearSpec() {
        GearSpec newSpec = new GearSpec();
        if (!txtProjectName.getText().isEmpty())
            newSpec.setName(txtProjectName.getText());

        if (!txtProjectVersion.getText().isEmpty())
            newSpec.setVersion(txtProjectVersion.getText());

        if (!txtProjectTags.getText().isEmpty())
            newSpec.setTags(ParseStringWithCommas(txtProjectTags.getText()));

        if (!txtSourceURL.getText().isEmpty()){
            String tagString = (txtLibraryTag.getText().equals("")) ? null : txtLibraryTag.getText();
            String sourceString = (txtSourceLibLocation.getText().equals("")) ? null : txtSourceLibLocation.getText();

            newSpec.setSource(new GearSpecSource(sourceString, txtSourceURL.getText(), tagString));
        }

        if (!txtProjectSummary.getText().isEmpty())
            newSpec.setSummary(txtProjectSummary.getText());

        if (!txtReleaseNotes.getText().isEmpty())
            newSpec.setRelease_notes(txtReleaseNotes.getText());

        if (!txtCopyRight.getText().isEmpty())
            newSpec.setCopyright(txtCopyRight.getText());

        if (!txtHomePage.getText().isEmpty())
            newSpec.setHomepage(txtHomePage.getText());

        if (!txtLicense.getText().isEmpty())
            newSpec.setLicense(txtLicense.getText());

        if (!authors.isEmpty())
            newSpec.setAuthors(authors);

        if (!dependencies.isEmpty())
            newSpec.setDependencies(dependencies);

        newSpec.setType(cbLibraryType.getSelectedItem().toString());
        newSpec.setMinimum_api(Integer.parseInt(cbMinSDK.getSelectedItem().toString()));

        return newSpec;
    }

    //TODO Create checks.
    private ArrayList<String> ParseStringWithCommas(String text) {
        String[] Strings = text.split(",");
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < Strings.length; i++) {
            //trims the whitespace from the beginning and the end of the string. leaving the whitespace in the middle alone
            Strings[i] = trimExtraWhiteSpace(Strings[i]);
            //Is empty check
            if (!Strings[i].isEmpty()) {
                temp.add(Strings[i]);
            }
        }
        return temp;
    }

    /**
     * Checks for spaces at the end of the string and at the beginning of the string and removes that whitespace.
     *
     * @param string String spaces are checked on
     * @return string with the whitespace trimmed off.
     */
    private String trimExtraWhiteSpace(String string) {
        //remove all whitespace after string and before string
        return string.replaceAll("\\s+$", "").replaceAll("^\\s+", "");
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    ///////////////////////
    // Dialogs
    ///////////////////////

    private void showLintErrorDialog(GearSpecLintResult result) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(MasterPanel),
                result.getResponseMessage(),
                "Lint Error",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showSaveErrorDialog() {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(MasterPanel),
                "There was a problem saving your Gear Spec. Please try again.",
                "Lint Error",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showLoadErrorDialog() {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(MasterPanel),
                "There was a problem loading your Gear Spec. Please try again.",
                "Lint Error",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }
}
