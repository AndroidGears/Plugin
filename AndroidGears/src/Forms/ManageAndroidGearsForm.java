package Forms;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import Models.GearSpec.GearSpecDependency;
import Panels.SpecDetailsPanel;
import Renderers.GearSpecCellRenderer;
import Renderers.ModuleCellRenderer;
import Renderers.ProjectCellRenderer;
import Singletons.SettingsManager;
import Utilities.OSValidator;
import Utilities.Utils;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

import Models.GearSpec.GearSpec;
import Workers.*;
import com.google.gson.Gson;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * Created by matthewyork on 4/1/14.
 */
public class ManageAndroidGearsForm{
    public static final int DETAILS_INNER_WIDTH = 230;
    private static final int AGREE_TO_UNINSTALL_GEAR = 1;
    private static final int AGREE_TO_UNINSTALL_GEAR_AND_DEPENDENTS = 2;

    File androidGearsDirectory;
    private GearSpec selectedSpec;
    private ArrayList<GearSpec> availableGears;
    private ArrayList<GearSpec> declaredProjects;
    private ArrayList<GearSpec> installedProjects;
    private ArrayList<String> projectVersions;
    Project[] targetProjects;
    Module[] targetModules;

    private JTextField SearchTextField;
    private JTabbedPane SearchTabbedPane;
    private JButton doneButton;
    public JPanel MasterPanel;
    private JPanel SearchPanel;
    private JPanel DetailsPanel;
    private JList AllGearsList;
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
    private JLabel HeaderLogo;
    private JList DeclaredList;
    private JButton DeclareUndeclareGearButton;
    private JComboBox TargetModuleComboBox;

    private void createUIComponents() {

    }

    public ManageAndroidGearsForm() {
        setupComboBoxes();
        setupMiscUI();
        setupTables();
        setupSearchTextField();
        setupButtons();
    }

    private void setupTables() {

        //Add directories mode
        SearchProjectListWorker worker = new SearchProjectListWorker("", targetProjects[TargetProjectComboBox.getSelectedIndex()]){
            @Override
            protected void done() {
                super.done();
                availableGears = this.specs;
            }
        };
        worker.execute();

        //Get declared gears
        refreshDeclaredList("");

        //Get installed gears
        refreshInstalledList("");

        //Setup click listener
        AllGearsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                didSelectSearchSpecAtIndex(AllGearsList.getSelectedIndex());
            }
        });

        DeclaredList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                didSelectDeclaredSpecAtIndex(DeclaredList.getSelectedIndex());
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


                //Switch to desired tab
                switch (SearchTabbedPane.getSelectedIndex()) {
                    case 0:  refreshAvailableGearsList(searchString);
                        break;
                    case 1:  refreshDeclaredList(searchString);
                        break;
                    case 2:  refreshInstalledList(searchString);
                        break;
                    default:
                        break;
                }
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

        //Declare/Undeclare button
        DeclareUndeclareGearButton.setVisible(false);
        DeclareUndeclareGearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                toggleDependencyDeclaration();
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

    private void setupComboBoxes(){
        //Get all projects
        ProjectManager pm = ProjectManager.getInstance();
        targetProjects = pm.getOpenProjects();
        Project p = targetProjects[0];
        SettingsManager.getInstance().loadProjectSettings(p);

        //Get all modules
        ModuleManager mm = ModuleManager.getInstance(p);
        targetModules = mm.getModules();

        //Setup Project Combo Box
        TargetProjectComboBox.setModel(new ListComboBoxModel<Project>(Arrays.asList(targetProjects)));
        TargetProjectComboBox.setSelectedIndex(0);
        TargetProjectComboBox.setRenderer(new ProjectCellRenderer());
        TargetProjectComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        TargetProjectComboBox.setFocusable(false);

        //Setup Module Combo Box
        TargetModuleComboBox.setModel(new ListComboBoxModel<Module>(Arrays.asList(targetModules)));
        if (targetModules.length > 0) {
            TargetModuleComboBox.setSelectedIndex(targetModules.length-1);
        }
        TargetModuleComboBox.setRenderer(new ModuleCellRenderer());
        TargetModuleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Module module = (Module)TargetModuleComboBox.getSelectedItem();
                SettingsManager.getInstance().setMainModule(module.getName(), targetProjects[TargetProjectComboBox.getSelectedIndex()]);
            }
        });
        TargetModuleComboBox.setFocusable(false);

        //Get/Set selected module
        String mainModule = SettingsManager.getInstance().getMainModule();
        if (mainModule.equals("")){ //Save default
            SettingsManager.getInstance().setMainModule(targetModules[targetModules.length-1].getName(), p);
        }
        else { //Pull back selected module
            for (int ii = 0; ii < targetModules.length; ii++){
                if (targetModules[ii].getName().equals(mainModule)){
                    TargetModuleComboBox.setSelectedIndex(ii);
                }
            }
        }


    }

    private void setupMiscUI() {
        ChangeVersionsLabel.setFont(new Font(ChangeVersionsLabel.getFont().getName(), Font.PLAIN, 12));
        StatusLabel.setText("");
        LoadingSpinnerLabel.setVisible(false);

        //Focus search bar
        SearchTextField.setVisible(true);
        SearchTextField.requestFocusInWindow();

        //Set header logo background clear
        HeaderLogo.setOpaque(false);

        //Set up listener for change in tab state
        SearchTabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                //Switch to desired tab
                switch (SearchTabbedPane.getSelectedIndex()) {
                    case 0:  refreshAvailableGearsList(SearchTextField.getText());
                        break;
                    case 1:  refreshDeclaredList(SearchTextField.getText());
                        break;
                    case 2:  refreshInstalledList(SearchTextField.getText());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    ///////////////////////
    // Table refresh/reload
    ////////////////////////

    private void reloadSearchList(){
        AllGearsList.setListData(availableGears.toArray());
        AllGearsList.setCellRenderer(new GearSpecCellRenderer());
        AllGearsList.setVisibleRowCount(availableGears.size());
    }

    private void reloadDeclaredList(){
        DeclaredList.setListData(declaredProjects.toArray());
        DeclaredList.setCellRenderer(new GearSpecCellRenderer());
        DeclaredList.setVisibleRowCount(declaredProjects.size());

    }

    private void reloadInstalledList(){
        InstalledList.setListData(installedProjects.toArray());
        InstalledList.setCellRenderer(new GearSpecCellRenderer());
        InstalledList.setVisibleRowCount(installedProjects.size());
    }

    private void refreshAvailableGearsList(String searchString){
        //Get availableGears and reload
        SearchProjectListWorker worker = new SearchProjectListWorker(searchString, targetProjects[TargetProjectComboBox.getSelectedIndex()]){
            @Override
            protected void done() {
                super.done();
                availableGears = this.specs;
                reloadSearchList();
            }
        };
        worker.execute();
    }

    private void refreshDeclaredList(final String searchString){
        SearchDeclaredDependenciesWorker declaredProjectsWorker = new SearchDeclaredDependenciesWorker(targetProjects[TargetProjectComboBox.getSelectedIndex()], searchString){

            @Override
            protected void done() {
                super.done();

                declaredProjects = this.specs;
                reloadDeclaredList();
            }
        };
        declaredProjectsWorker.execute();
    }

    private void refreshInstalledList(final String searchString){
        GetInstalledProjectsWorker installedProjectsWorker = new GetInstalledProjectsWorker(targetProjects[TargetProjectComboBox.getSelectedIndex()], searchString){

            @Override
            protected void done() {
                super.done();

                installedProjects = this.specs;
                reloadInstalledList();
            }
        };
        installedProjectsWorker.execute();
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
        if (index >= 0 && index < availableGears.size()){
            selectedSpec = availableGears.get(index);
            setDetailsForSpec(selectedSpec, availableGears.get(index).getVersion());
            getVersionDetailsForSepc();
        }
    }

    private void didSelectDeclaredSpecAtIndex(int index){
        if (index >= 0 && index < declaredProjects.size()){
            selectedSpec = declaredProjects.get(index);
            setDetailsForSpec(selectedSpec, declaredProjects.get(index).getVersion()); //MAY NEED TO CHANGE
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

        //Set install/uninstall button
        InstallUninstallButton.setVisible(true);

        //Enable show homepage button again
        OpenInBrowserButton.setVisible(true);

        //Set declaration button based on install state
        setDeclarationStatusForSpec(spec);
    }

    private void setDeclarationStatusForSpec(final GearSpec spec){
        GetGearStateWorker worker = new GetGearStateWorker(targetProjects[TargetProjectComboBox.getSelectedIndex()], spec){
            @Override
            protected void done() {
                super.done();


                if (this.gearState == GearSpec.GearState.GearStateUninstalled){
                    DeclareUndeclareGearButton.setText("Declare Gear");
                    InstallUninstallButton.setText("Install Gear");
                    DeclareUndeclareGearButton.setVisible(true);

                }
                else if (this.gearState == GearSpec.GearState.GearStateDeclared){
                    DeclareUndeclareGearButton.setText("Undeclare Gear");
                    DeclareUndeclareGearButton.setVisible(true);
                    InstallUninstallButton.setText("Install Gear");
                }
                else if (this.gearState == GearSpec.GearState.GearStateInstalled){
                    DeclareUndeclareGearButton.setVisible(false);
                    InstallUninstallButton.setText("Uninstall Gear");
                }
            }
        };
          worker.execute();
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

        if (this.selectedSpec.isRegistered(targetProject)){
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

            uninstallDependencies(gearsToUninstall, targetProjects[TargetProjectComboBox.getSelectedIndex()], targetModules[TargetModuleComboBox.getSelectedIndex()]);
        }
        else {
            //Set UI in download state
            StatusLabel.setText("Installing Gear and its dependencies: " + this.selectedSpec.getName());
            LoadingSpinnerLabel.setVisible(true);
            InstallUninstallButton.setEnabled(false);
            SyncButton.setEnabled(false);


            InstallDependencyForSpecWorker worker = new InstallDependencyForSpecWorker(this.selectedSpec, targetProjects[TargetProjectComboBox.getSelectedIndex()], targetModules[TargetModuleComboBox.getSelectedIndex()]){

                @Override
                protected void done() {
                    super.done();

                    //Hide loading spinner and renable buttons
                    LoadingSpinnerLabel.setVisible(false);
                    InstallUninstallButton.setEnabled(true);
                    SyncButton.setEnabled(true);
                    setDeclarationStatusForSpec(ManageAndroidGearsForm.this.selectedSpec);

                    //Flip button text
                    if (this.successful){
                        DeclareUndeclareGearButton.setVisible(false);
                        InstallUninstallButton.setText("Uninstall Gear");
                        StatusLabel.setText("Successfully installed: "+ManageAndroidGearsForm.this.selectedSpec.getName());
                        refreshDeclaredList(SearchTextField.getText());
                        refreshInstalledList(SearchTextField.getText());
                        refreshAvailableGearsList(SearchTextField.getText());
                    }
                    else {
                        StatusLabel.setText("Installation failed for: "+ManageAndroidGearsForm.this.selectedSpec.getName());
                    }
                }
            };
            worker.execute();
        }
    }

    private void toggleDependencyDeclaration(){
        DeclareUndeclareGearButton.setEnabled(false);
        InstallUninstallButton.setEnabled(false);

        if (this.selectedSpec.gearState == GearSpec.GearState.GearStateDeclared){
            UndeclareSpecWorker worker = new UndeclareSpecWorker(this.selectedSpec, targetProjects[TargetProjectComboBox.getSelectedIndex()]){
                @Override
                protected void done() {
                    super.done();

                    DeclareUndeclareGearButton.setEnabled(true);
                    InstallUninstallButton.setEnabled(true);

                    if (success){
                        StatusLabel.setText("Successfully undeclared: "+ManageAndroidGearsForm.this.selectedSpec.getName());
                        DeclareUndeclareGearButton.setText("Declare Gear");

                        //Set new declaration state on local copy of selected spec
                        ManageAndroidGearsForm.this.selectedSpec.gearState = GearSpec.GearState.GearStateUninstalled;
                        setDetailsForSpec(ManageAndroidGearsForm.this.selectedSpec, ManageAndroidGearsForm.this.selectedSpec.getVersion());

                        //Reload all tables
                        refreshAvailableGearsList(SearchTextField.getText());
                        refreshDeclaredList(SearchTextField.getText());
                        refreshInstalledList(SearchTextField.getText());
                    }
                    else {
                        StatusLabel.setText("Failed to undeclare:: "+ManageAndroidGearsForm.this.selectedSpec.getName());
                    }
                }
            };
            worker.execute();
        }
        else {
            DeclareSpecWorker worker = new DeclareSpecWorker(this.selectedSpec, targetProjects[TargetProjectComboBox.getSelectedIndex()]){
                @Override
                protected void done() {
                    super.done();

                    DeclareUndeclareGearButton.setEnabled(true);
                    InstallUninstallButton.setEnabled(true);

                    if (success){
                        StatusLabel.setText("Successfully declared: "+ManageAndroidGearsForm.this.selectedSpec.getName());
                        DeclareUndeclareGearButton.setText("Undeclare Gear");

                        //Set new declaration state on local copy of selected spec
                        ManageAndroidGearsForm.this.selectedSpec.gearState = GearSpec.GearState.GearStateDeclared;
                        setDetailsForSpec(ManageAndroidGearsForm.this.selectedSpec, ManageAndroidGearsForm.this.selectedSpec.getVersion());

                        //Reload all tables
                        refreshAvailableGearsList(SearchTextField.getText());
                        refreshDeclaredList(SearchTextField.getText());
                        refreshInstalledList(SearchTextField.getText());
                    }
                    else {
                        StatusLabel.setText("Failed to declare:: "+ManageAndroidGearsForm.this.selectedSpec.getName());
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

    private void uninstallDependencies(ArrayList<GearSpec> specs, final Project project, Module module){
        //Set UI in uninstall state
        StatusLabel.setText("Uninstalling gears");
        LoadingSpinnerLabel.setVisible(true);
        InstallUninstallButton.setEnabled(false);
        SyncButton.setEnabled(false);

        UninstallDependencyForSpecWorker worker = new UninstallDependencyForSpecWorker(specs, project, module){

            @Override
            protected void done() {
                super.done();

                //Hide loading spinner and renable buttons
                LoadingSpinnerLabel.setVisible(false);
                InstallUninstallButton.setEnabled(true);
                SyncButton.setEnabled(true);
                setDeclarationStatusForSpec(ManageAndroidGearsForm.this.selectedSpec);

                //Flip button text
                if (this.successful){
                    DeclareUndeclareGearButton.setVisible(true);
                    InstallUninstallButton.setText("Install Gear");
                    StatusLabel.setText("Successfully uninstalled gear.");
                    refreshDeclaredList(SearchTextField.getText());
                    refreshInstalledList(SearchTextField.getText());
                    refreshAvailableGearsList(SearchTextField.getText());
                }
                else {
                    StatusLabel.setText("There was a problem uninstalling the gear. Please try again.");
                }
            }
        };
        worker.execute();
    }
}


