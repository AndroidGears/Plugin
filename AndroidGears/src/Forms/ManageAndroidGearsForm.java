package Forms;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import Models.GearSpec.GearSpecDependency;
import Panels.SpecDetailsPanel;
import Renderers.GearSpecCellRenderer;
import Renderers.ProjectCellRenderer;
import Utilities.OSValidator;
import Utilities.Utils;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

import Models.GearSpec.GearSpec;
import Workers.*;
import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * Created by matthewyork on 4/1/14.
 */
public class ManageAndroidGearsForm{
    public static final int DETAILS_INNER_WIDTH = 250;
    private static final int AGREE_TO_UNINSTALL_GEAR = 1;
    private static final int AGREE_TO_UNINSTALL_GEAR_AND_DEPENDENTS = 2;

    File androidGearsDirectory;
    private GearSpec selectedSpec;
    private ArrayList<GearSpec> searchProjects;
    private ArrayList<GearSpec> installedProjects;
    private ArrayList<String> projectVersions;
    Project[] targetProjects;

    private JTextField SearchTextField;
    private JTabbedPane tabbedPane1;
    private JButton doneButton;
    public JPanel MasterPanel;
    private JPanel SearchPanel;
    private JPanel DetailsPanel;
    private JList SearchList;
    private JList InstalledList;
    private JScrollPane DetailsScrollPane;
    private JButton SyncButton;
    private JLabel StatusLabel;
    private JList VersionsList;
    private JLabel ChangeVersionsLabel;
    private JButton InstallUninstallButton;
    private JButton OpenInBrowserButton;
    private JLabel LoadingSpinnerLabel;
    private JComboBox TargetProjectComboBox;

    private void createUIComponents() {

    }

    public ManageAndroidGearsForm() {
        setupSearchTable();
        setupSearchTextField();
        setupButtons();
        setupMiscUI();
    }

    private void setupSearchTable() {

        //Add directories mode
        SearchProjectListWorker worker = new SearchProjectListWorker("", Utils.androidGearsDirectory()){
            @Override
            protected void done() {
                super.done();
                searchProjects = this.specs;
            }
        };
        worker.execute();


        //Setup click listener
        SearchList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                didSelectSearchSpecAtIndex(SearchList.getSelectedIndex());
            }
        });

        InstalledList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                didSelectInstalledSpecAtIndex(InstalledList.getSelectedIndex());
            }
        });

        VersionsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                didSelectSpecVersion(VersionsList.getSelectedIndex());
            }
        });
    }

    private void setupSearchTextField() {
        SearchTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                //Get pressed character
                char c = keyEvent.getKeyChar();

                //Build searchString
                String searchString = SearchTextField.getText();
                if(c == 8 && searchString.length() > 0){
                    searchString = SearchTextField.getText().substring(0, searchString.length()-1);
                }
                else if(isValidCharacter(c)){
                    searchString = SearchTextField.getText()+keyEvent.getKeyChar();
                }

                //Get searchProjects and reload
                SearchProjectListWorker worker = new SearchProjectListWorker(searchString, Utils.androidGearsDirectory()){
                    @Override
                    protected void done() {
                        super.done();
                        searchProjects = this.specs;
                        reloadList();
                    }
                };
                worker.execute();

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
    }

    private void setupButtons(){
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame frame  = (JFrame)SwingUtilities.getWindowAncestor(MasterPanel);
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        SyncButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Set synchronizing
                StatusLabel.setText("Synchronizing available gears with server...");
                LoadingSpinnerLabel.setVisible(true);

                //Synchronize Specs
                GitWorker worker = new GitWorker(){
                    @Override
                    protected void done() {
                        super.done();
                        StatusLabel.setText("Gears successfully synced with server");
                        LoadingSpinnerLabel.setVisible(false);
                    }
                };
                worker.execute();
            }
        });

        //Install/Uninstall button
        InstallUninstallButton.setVisible(false);
        InstallUninstallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                toggleDependency();
            }
        });

        //Show homepage button
        OpenInBrowserButton.setVisible(false);

        //Show in browser
        OpenInBrowserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                openSpecHomePageInBrowser();
            }
        });
    }

    private void setupMiscUI() {
        ChangeVersionsLabel.setFont(new Font(ChangeVersionsLabel.getFont().getName(), Font.PLAIN, 12));
        StatusLabel.setText("");
        LoadingSpinnerLabel.setVisible(false);

        //Setup project list
        ProjectManager pm = ProjectManager.getInstance();
        targetProjects = pm.getOpenProjects();

        TargetProjectComboBox.setModel(new ListComboBoxModel<Project>(Arrays.asList(targetProjects)));
        TargetProjectComboBox.setSelectedIndex(0);
        TargetProjectComboBox.setRenderer(new ProjectCellRenderer());
        TargetProjectComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        TargetProjectComboBox.setFocusable(false);

        //Focus search bar
        SearchTextField.setVisible(true);
        SearchTextField.requestFocusInWindow();
    }

    private void reloadList(){
        SearchList.setListData(searchProjects.toArray());
        SearchList.setCellRenderer(new GearSpecCellRenderer());
        SearchList.setVisibleRowCount(searchProjects.size());

    }

    private Boolean isValidCharacter(char c){
        //Number
        if(c >= 32 && c <= 126){
            return true;
        }

        return false;
    }


    ///////////////////////
    // JList Selection
    ////////////////////////

    private void didSelectSearchSpecAtIndex(int index){
        if (index >= 0 && index < searchProjects.size()){
            selectedSpec = searchProjects.get(index);
            setDetailsForSpec(selectedSpec, searchProjects.get(index).getVersion());
            getVersionDetailsForSepc();
        }
    }

    private void didSelectInstalledSpecAtIndex(int index){
        if (index >= 0 && index < installedProjects.size()){
            selectedSpec = installedProjects.get(index);
            setDetailsForSpec(selectedSpec, installedProjects.get(index).getVersion()); //MAY NEED TO CHANGE
            getVersionDetailsForSepc();
        }

    }

    private void didSelectSpecVersion(int index) {
        if (index >= 0 && index < projectVersions.size()){
            setDetailsForSpec(selectedSpec, projectVersions.get(index));
        }
    }


    ////////////////////////
    // Details Management
    ///////////////////////

    private void setDetailsForSpec(GearSpec spec, String version){
        //If it is the same as you have selected, don't do anything, else, get the specified version
        if (!(spec.getName().equals(selectedSpec.getName()) && spec.getVersion().equals(version))){
            selectedSpec = specForVersion(spec.getName(), version);
        }

        SpecDetailsPanel specDetailsPanel = new SpecDetailsPanel(selectedSpec);

        //Set panel in scrollpane
        DetailsScrollPane.setViewportView(specDetailsPanel);
        DetailsScrollPane.revalidate();
        DetailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        DetailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //DetailsScrollPane.setPreferredSize(new Dimension(panel.getSize().width, panel.getSize().height));


        //Set install/uninstall button
        //CHECK HERE FOR INSTALLATION STATUS
        String buttonText = (selectedSpec.isInstalled(targetProjects[TargetProjectComboBox.getSelectedIndex()])) ? "Uninstall Gear" : "Install Gear";
        InstallUninstallButton.setText(buttonText);
        InstallUninstallButton.setVisible(true);

        //Enable show homepage button again
        OpenInBrowserButton.setVisible(true);

    }

    private void getVersionDetailsForSepc(){
        //Set versions
        GetProjectVersionsWorker worker = new GetProjectVersionsWorker(selectedSpec){
            @Override
            protected void done() {
                super.done();

                projectVersions = this.versions;

                VersionsList.setListData(projectVersions.toArray());
                VersionsList.setCellRenderer(new DefaultListCellRenderer());
                VersionsList.setVisibleRowCount(projectVersions.size());
            }
        };
        worker.execute();
    }

    private GearSpec specForVersion(String specName, String version){
        //Get path separator
        String pathSeparator = (OSValidator.isWindows()) ? "\\":"/";

        File specFile = new File(Utils.androidGearsDirectory().getAbsolutePath()+pathSeparator+specName+pathSeparator+version+pathSeparator+specName+".gearspec");

        if(specFile.exists()){
            String specString = null;
            try {
                specString = FileUtils.readFileToString(specFile);
            } catch (IOException e) {
                e.printStackTrace();
                return new GearSpec();
            }

            //Get spec
            GearSpec spec = new Gson().fromJson(specString, GearSpec.class);

            return spec;
        }

        return new GearSpec();
    }


    public static boolean ping(String url, int timeout) {
        //url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }

    ///////////////////////
    // Website loading
    ///////////////////////
    private void openSpecHomePageInBrowser(){
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                if (selectedSpec.getHomepage().contains("github.com")){
                    desktop.browse(URI.create(selectedSpec.getHomepage()+"/tree/"+selectedSpec.getSource().getTag()));
                }
                else {
                    desktop.browse(URI.create(selectedSpec.getHomepage()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    ///////////////////////
    // Install / Uninstall
    ///////////////////////

    private void toggleDependency(){
        Project targetProject = targetProjects[TargetProjectComboBox.getSelectedIndex()];

        if (this.selectedSpec.isInstalled(targetProject)){
            ArrayList<GearSpec> gearsToUninstall = new ArrayList<GearSpec>();
            gearsToUninstall.add(this.selectedSpec);

            //Prompt to add dependents
            ArrayList<GearSpec> dependents = this.selectedSpec.dependentGears(targetProject);
            if (dependents.size() > 0){
                gearsToUninstall.addAll(warnOfDependents(dependents));
            }

            //Prompt to add dependencies
            if (this.selectedSpec.getDependencies() != null){
                if (this.selectedSpec.getDependencies().size() > 0){
                    gearsToUninstall.addAll(warnOfDependencies(this.selectedSpec));
                }
            }

            uninstallDependencies(gearsToUninstall, targetProjects[targetProjects.length-1]);
        }
        else {
            //Set UI in download state
            StatusLabel.setText("Installing Gear and its dependencies: "+this.selectedSpec.getName());
            LoadingSpinnerLabel.setVisible(true);
            InstallUninstallButton.setEnabled(false);
            SyncButton.setEnabled(false);


            InstallDependencyForSpecWorker worker = new InstallDependencyForSpecWorker(this.selectedSpec, targetProjects[TargetProjectComboBox.getSelectedIndex()]){

                @Override
                protected void done() {
                    super.done();

                    //Hide loading spinner and renable buttons
                    LoadingSpinnerLabel.setVisible(false);
                    InstallUninstallButton.setEnabled(true);
                    SyncButton.setEnabled(true);

                    //Flip button text
                    if (this.successful){
                        InstallUninstallButton.setText("Uninstall Gear");
                        StatusLabel.setText("Successfully installed: "+ManageAndroidGearsForm.this.selectedSpec.getName());
                    }
                    else {
                        StatusLabel.setText("Installation failed for: "+ManageAndroidGearsForm.this.selectedSpec.getName());
                    }
                }
            };
            worker.execute();
        }
    }

    private ArrayList<GearSpec> warnOfDependents(ArrayList<GearSpec> dependents){
        String dependentString = "";
        for(GearSpec dependentGear : dependents){
            dependentString= dependentString+dependentGear.getName()+ " - "+dependentGear.getVersion()+"\n";
        }

        String dependencyMessageString = "The gear you wish to uninstall has other gears depending on it:\n"+dependentString
                +"\nContinuing could cause unexpected behavior in these gears.";

        Object[] options = {"Cancel",
                "Continue", "Continue and Uninstall Dependents"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(MasterPanel),
                dependencyMessageString,
                "Dependency check",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        //Process answer
        if (answer == AGREE_TO_UNINSTALL_GEAR){
            return new ArrayList<GearSpec>();
        }
        else if (answer == AGREE_TO_UNINSTALL_GEAR_AND_DEPENDENTS){
            return dependents;
        }
        else {
            return new ArrayList<GearSpec>();
        }
    }

    private ArrayList<GearSpec> warnOfDependencies(GearSpec spec){
        if (spec.getDependencies() != null){
            String dependentString = "";
            ArrayList<GearSpec> dependencies = new ArrayList<GearSpec>();
            for(GearSpecDependency dependency : spec.getDependencies()){
                dependentString= dependentString+dependency.getName()+ " - "+dependency.getVersion()+"\n";
                dependencies.add(Utils.specForInfo(dependency.getName(), dependency.getVersion()));
            }

            String dependencyMessageString = "The gear you wish to uninstall depends on other gears:\n"+dependentString
                    +"\nWould you also like to uninstall these gears?.";

            Object[] options = {"No",
                    "Yes"};
            int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(MasterPanel),
                    dependencyMessageString,
                    "Dependency check",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            //Process answer
            if (answer == AGREE_TO_UNINSTALL_GEAR){
                return dependencies;
            }
        }

        return new ArrayList<GearSpec>();
    }

    private void uninstallDependencies(ArrayList<GearSpec> specs, final Project project){
        //Set UI in uninstall state
        StatusLabel.setText("Uninstalling gears");
        LoadingSpinnerLabel.setVisible(true);
        InstallUninstallButton.setEnabled(false);
        SyncButton.setEnabled(false);

        UninstallDependencyForSpecWorker worker = new UninstallDependencyForSpecWorker(specs, project){

            @Override
            protected void done() {
                super.done();

                //Hide loading spinner and renable buttons
                LoadingSpinnerLabel.setVisible(false);
                InstallUninstallButton.setEnabled(true);
                SyncButton.setEnabled(true);

                //Flip button text
                if (this.successful){
                    InstallUninstallButton.setText("Install Gear");
                    StatusLabel.setText("Successfully uninstalled gear.");
                }
                else {
                    StatusLabel.setText("There was a problem uninstalling the gear. Please try again.");
                }
            }
        };
        worker.execute();
    }
}


