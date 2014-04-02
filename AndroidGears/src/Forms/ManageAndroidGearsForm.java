package Forms;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.io.*;

import Panels.SpecDetailsPanel;
import Renderers.GearSpecCellRenderer;
import Utilities.Utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import Models.GearSpec.GearSpec;
import Utilities.OSValidator;
import Workers.GitWorker;
import Workers.SearchProjectListWorker;
import com.google.gson.Gson;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.fit.cssbox.swingbox.BrowserPane;

/**
 * Created by matthewyork on 4/1/14.
 */
public class ManageAndroidGearsForm{
    public static final int DETAILS_INNER_WIDTH = 240;

    File androidGearsDirectory;
    private ArrayList<GearSpec> searchProjects;
    private ArrayList<GearSpec> installedProjects;
    private BrowserPane swingbox;

    private JTextField SearchTextField;
    private JTabbedPane tabbedPane1;
    private JButton doneButton;
    public JPanel MasterPanel;
    private JPanel SearchPanel;
    private JPanel ReadmePanel;
    private JPanel DetailsPanel;
    private JList SearchList;
    private JList InstalledList;
    private JScrollPane DetailsScrollPane;
    private JScrollPane ReadmeScrollPane;
    private JButton SyncButton;
    private JLabel StatusLabel;
    private JTable SearchTable;

    private void createUIComponents() {

    }

    public ManageAndroidGearsForm() {
        setupSearchTable();
        setupSearchTextField();
        setupButtons();
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
    }

    private void reloadList(){
        SearchList.setListData(searchProjects.toArray());
        SearchList.setCellRenderer(new GearSpecCellRenderer());
        SearchList.setVisibleRowCount(searchProjects.size());

    }


    ///////////////////////
    // Spec Selection
    ////////////////////////

    private void didSelectSearchSpecAtIndex(int index){
        setDetailsForSpec(searchProjects.get(index));
    }

    private void didSelectInstalledSpecAtIndex(int index){
        setDetailsForSpec(installedProjects.get(index));
    }


    ////////////////////////
    // Details Management
    ///////////////////////

    private void setDetailsForSpec(GearSpec spec){
        SpecDetailsPanel specDetailsPanel = new SpecDetailsPanel(spec);

        if(spec.getHomepage() != null){
            //Fetch page/readme
            String fetchUrl = spec.getHomepage();
            Boolean isGithub = false;
            if (spec.getHomepage().contains("github.com")) {
                isGithub = true;
                fetchUrl = fetchUrl + "/blob/master/README.md";
            }

            final String url = fetchUrl;

            if(swingbox == null){
                swingbox = new BrowserPane();
                ReadmeScrollPane.setViewportView(swingbox);
                ReadmeScrollPane.revalidate();
                ReadmeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                ReadmeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            }
        }

        //Set panel in scrollpane
        DetailsScrollPane.setViewportView(specDetailsPanel);
        DetailsScrollPane.revalidate();
        DetailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        DetailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //DetailsScrollPane.setPreferredSize(new Dimension(panel.getSize().width, panel.getSize().height));
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

}


