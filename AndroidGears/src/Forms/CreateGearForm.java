package Forms;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecAuthor;
import Models.GearSpec.GearSpecDependency;
import Models.GearSpec.GearSpecSource;
import Models.GearSpecLinter.GearSpecLintResult;
import Utilities.Utils;
import Workers.Lint.LintGearSpecWorker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    }

    private void initAuthorTable() {
        ArrayList<GearSpecAuthor> authors = new ArrayList<GearSpecAuthor>();
        AuthorModel = (DefaultTableModel) authorsTable.getModel();
        AuthorModel.addColumn("Author's Name");
        AuthorModel.addColumn("Author's Email");
        AddAllNewAuthors(authors, AuthorModel);
    }

    private void initDependenciesTable() {//TODO add lint for the same library being added twice
        ArrayList<GearSpecDependency> dependencies = new ArrayList<GearSpecDependency>();
        DependencyModel = (DefaultTableModel) dependencyTable.getModel();
        DependencyModel.addColumn("Dependency's Name");
        DependencyModel.addColumn("Dependency's Version");
        AddAllNewDependencies(dependencies, DependencyModel);
    }

    private void AddAllNewAuthors(ArrayList<GearSpecAuthor> authors, DefaultTableModel model) {
        for (GearSpecAuthor author : authors) {
            AddNewAuthor(author, model);
        }
    }

    private void AddNewAuthor(GearSpecAuthor gearSpecAuthor, DefaultTableModel model) {
        model.addRow(new Object[]{gearSpecAuthor.getName(), gearSpecAuthor.getEmail()});
    }


    private void AddAllNewDependencies(ArrayList<GearSpecDependency> dependencies, DefaultTableModel model) {
        for (GearSpecDependency dependency : dependencies) {
            AddNewDependency(dependency, model);
        }
    }

    private void AddNewDependency(GearSpecDependency gearSpecDependency, DefaultTableModel model) {
        model.addRow(new Object[]{gearSpecDependency.getName(), gearSpecDependency.getVersion()});
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
                    AuthorModel.addRow(new Object[]{txtAuthorName.getText(), txtAuthorEmail.getText()});
                    authors.add(new GearSpecAuthor(txtAuthorName.getText(), txtAuthorEmail.getText()));
                    txtAuthorName.setText("");
                    txtAuthorEmail.setText("");

                }
            }
        });

        btnAddDependency.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtDependencyName.getText().isEmpty() && !txtDependencyVersion.getText().isEmpty()) {
                    DependencyModel.addRow(new Object[]{txtDependencyName.getText(), txtDependencyVersion.getText()});
                    dependencies.add(new GearSpecDependency(txtDependencyName.getText(), txtDependencyVersion.getText()));
                    txtDependencyName.setText("");
                    txtDependencyVersion.setText("");
                }
            }
        });
        btnRemoveAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AuthorModel.removeRow(authorsTable.getSelectedRow());
                authors.remove(authorsTable.getSelectedRow());
            }
        });

        btnRemoveDependency.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DependencyModel.removeRow(dependencyTable.getSelectedRow());
                dependencies.remove(dependencyTable.getSelectedRow());
            }
        });

        btnLoadGearSpec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GearSpec spec = loadGearSpec();
            }
        });
    }

    private void lintSpec(final GearSpec spec) {
        LintGearSpecWorker worker = new LintGearSpecWorker(spec){
            @Override
            protected void done() {
                super.done();

                if (result.getPassed()){
                    if (saveSpec(spec)){
                        JFrame frame  = (JFrame)SwingUtilities.getWindowAncestor(MasterPanel);
                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    }
                    else {
                        showSaveErrorDialog();
                    }
                }
                else {
                    showLintErrorDialog(result);
                }
            }
        };
        worker.execute();
    }

    private Boolean saveSpec(GearSpec spec){

        //Get top level frame
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(MasterPanel);

        //Create dialog for choosing gearspec file
        FileDialog fd = new FileDialog(topFrame, "Save .gearspec file", FileDialog.SAVE);
        fd.setDirectory(System.getProperty("user.home"));
        fd.setFile(".gearspec");
        fd.setVisible(true);
        //Get file
        String filename = fd.getFile();
        if (filename == null)
            System.out.println("You cancelled the choice");
        else {
            System.out.println("You chose " + filename);

            //Get spec file
            File specFile = new File(fd.getDirectory()+ Utils.pathSeparator()+filename);

            //Serialize spec to string
            String gearString = gson.toJson(spec);

            try {
                //If it exists, set it as the selected file path
                if (specFile.exists()){
                    FileUtils.forceDelete(specFile);
                }

                //Write new spec
                FileUtils.write(specFile, gearString);
            } catch (IOException e) {
                e.printStackTrace();

            }

            return true;
        }

        return false;
    }

    private GearSpec loadGearSpec(){
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
            File specFile = new File(fd.getDirectory()+Utils.pathSeparator()+filename);

            //If it exists, set it as the selected file path
            if (specFile.exists()){
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

        if (!txtLibraryTag.getText().isEmpty() && !txtSourceLibLocation.getText().isEmpty() && !txtSourceURL.getText().isEmpty())//TODO check for all 3 urls and file paths source must end with .git
            newSpec.setSource(new GearSpecSource(txtSourceLibLocation.getText(), txtSourceURL.getText(), txtLibraryTag.getText()));

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


    private ArrayList<GearSpecAuthor> CreateAuthorsArray() {
        ArrayList<GearSpecAuthor> authors = new ArrayList<GearSpecAuthor>();
        authors.add(new GearSpecAuthor(txtAuthorName.getText(), txtAuthorEmail.getText()));
        return authors;
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
}
