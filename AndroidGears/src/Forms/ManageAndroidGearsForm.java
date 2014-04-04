package Forms;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import Panels.SpecDetailsPanel;
import Renderers.GearSpecCellRenderer;
import Utilities.OSValidator;
import Utilities.Utils;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import Models.GearSpec.GearSpec;
import Workers.GetProjectVersionsWorker;
import Workers.GetSpecIconWorker;
import Workers.GitWorker;
import Workers.SearchProjectListWorker;
import com.google.gson.Gson;

/**
 * Created by matthewyork on 4/1/14.
 */
public class ManageAndroidGearsForm{
    public static final int DETAILS_INNER_WIDTH = 320;

    File androidGearsDirectory;
    private GearSpec selectedSpec;
    private ArrayList<GearSpec> searchProjects;
    private ArrayList<GearSpec> installedProjects;
    private ArrayList<String> projectVersions;

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
    private JLabel IconLabel;
    private JTable SearchTable;

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
        worker.run();


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
                else {
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
                worker.run();

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

                //Synchronize Specs
                GitWorker worker = new GitWorker(){
                    @Override
                    protected void done() {
                        super.done();
                        StatusLabel.setText("Gears successfully synced with server");
                    }
                };
                worker.run();
            }
        });

        //Install/Uninstall button
        InstallUninstallButton.setVisible(false);

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
    }

    private void reloadList(){
        SearchList.setListData(searchProjects.toArray());
        SearchList.setCellRenderer(new GearSpecCellRenderer());
        SearchList.setVisibleRowCount(searchProjects.size());

    }


    ///////////////////////
    // JList Selection
    ////////////////////////

    private void didSelectSearchSpecAtIndex(int index){
        selectedSpec = searchProjects.get(index);
        setDetailsForSpec(selectedSpec, searchProjects.get(index).getVersion());
        getVersionDetailsForSepc();
    }

    private void didSelectInstalledSpecAtIndex(int index){
        selectedSpec = installedProjects.get(index);
        setDetailsForSpec(selectedSpec, installedProjects.get(index).getVersion()); //MAY NEED TO CHANGE
        getVersionDetailsForSepc();
    }

    private void didSelectSpecVersion(int index) {
        setDetailsForSpec(selectedSpec, projectVersions.get(index));
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
        InstallUninstallButton.setVisible(true);

        //Enable show homepage button again
        OpenInBrowserButton.setVisible(true);

        //Set icon
        GetSpecIconWorker worker = new GetSpecIconWorker(spec.getIcon()){
            @Override
            protected void done() {
                super.done();

                IconLabel.setIcon(this.icon);
            }
        };
        worker.run();

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
        worker.run();
    }

    private GearSpec specForVersion(String specName, String version){
        //Get path separator
        String pathSeparator = (OSValidator.isWindows()) ? "\\":"/";

        File specFile = new File(Utils.androidGearsDirectory().getAbsolutePath()+pathSeparator+specName+pathSeparator+version+pathSeparator+specName+".gearspec");

        if(specFile.exists()){
            String specString = Utils.stringFromFile(specFile);

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
}


