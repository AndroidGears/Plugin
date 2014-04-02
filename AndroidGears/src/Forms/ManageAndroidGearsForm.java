package Forms;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import Models.GearSpec.GearSpecAuthor;
import Models.GearSpec.GearSpecDependency;
import Renderers.GearSpecCellRenderer;
import Utilities.Utils;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Models.GearSpec.GearSpec;
import Utilities.OSValidator;
import com.google.gson.Gson;

/**
 * Created by matthewyork on 4/1/14.
 */
public class ManageAndroidGearsForm{
    private final int DETAILS_INNER_WIDTH = 240;

    File androidGearsDirectory;
    private ArrayList<GearSpec> searchProjects;
    private ArrayList<GearSpec> installedProjects;

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
    private JTable SearchTable;

    private void createUIComponents() {

    }

    public ManageAndroidGearsForm() {
        setupSearchTable();
        setupSearchTextField();
        setupDoneButton();
    }

    private void setupSearchTable() {
        //Setup file
        if (OSValidator.isWindows()) {
            androidGearsDirectory = new File(System.getProperty("user.home")+"/AndroidGears"); //C drive
        } else if (OSValidator.isMac()) {
            androidGearsDirectory = new File(System.getProperty("user.home")+"/.androidgears"); //Home folder
        } else if (OSValidator.isUnix()) {
            androidGearsDirectory = new File("~/.androidgears"); //Home folder
        } else if (OSValidator.isSolaris()) {
            androidGearsDirectory = new File("~/AndroidGears");//Home folder
        } else {
            androidGearsDirectory = new File("~/AndroidGears");//Home folder
        }

        //Add directories model
        searchProjects = projectsList(androidGearsDirectory, "");

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
                searchProjects = projectsList(androidGearsDirectory, searchString);
                reloadList();
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
    }

    private void setupDoneButton(){
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame frame  = (JFrame)SwingUtilities.getWindowAncestor(MasterPanel);
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    private void reloadList(){
        SearchList.setListData(searchProjects.toArray());
        SearchList.setCellRenderer(new GearSpecCellRenderer());
        SearchList.setVisibleRowCount(searchProjects.size());

    }

    private ArrayList<GearSpec> projectsList(File androidGearsDirectory, final String searchString){
        //Check for empty search string
        if(searchString.equals("")){
            return new ArrayList<GearSpec>();
        }

        //If there is a searchstring, get matches!
        String directories[] =  androidGearsDirectory.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if(name.contains(".")){ //No hidden folders!
                    return  false;
                }
                else if (name.toLowerCase().contains(searchString.toLowerCase())){ //Accept only those that match your search string
                    return true;
                }

                return false;
            }
        });

        //Create gson instance for use in parsing specs
        Gson gson = new Gson();

        //Get path separator
        String pathSeparator = (OSValidator.isWindows()) ? "\\":"/";

        //Create and populate searchProjects array
        ArrayList<GearSpec> projectList = new ArrayList<GearSpec>();
        for (String directory : directories){
            //Get versions for spec
            String[] versions = versionsForProject(directory, pathSeparator);

            //Build spec location
            File specFile = new File(androidGearsDirectory.getAbsolutePath()+pathSeparator+directory+pathSeparator+versions[versions.length-1]+pathSeparator+directory+".gearspec");

            //Read file
            String specString = Utils.stringFromFile(specFile);

            //Get spec
            GearSpec spec = gson.fromJson(specString, GearSpec.class);

            //Create project and add to project list
            projectList.add(spec);
        }

        return projectList;
    }

    private String[] versionsForProject(String project, String pathSeparator){
        File versionsDirectory = new File(androidGearsDirectory.getAbsolutePath()+pathSeparator+project);
        return versionsDirectory.list();
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
        //Clear details panel
        //DetailsScrollPane.removeAll();

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,15));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setSize(200, -1);

        //Add repo name
        JLabel nameLabel = new JLabel(spec.getName(), JLabel.LEFT);
        nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 14));
        panel.add(nameLabel);

        //Add version and type
        JLabel versionLabel = new JLabel(spec.getVersion()+" - "+spec.getType(), JLabel.LEFT);
        versionLabel.setFont(new Font(versionLabel.getFont().getName(), Font.BOLD, 12));
        panel.add(versionLabel);

        //Add repo name
        if(spec.getSummary() != null){
            JLabel summaryLabel = new JLabel(Utils.wrappedStringForString(spec.getSummary(), DETAILS_INNER_WIDTH), JLabel.LEFT);
            summaryLabel.setFont(new Font(summaryLabel.getFont().getName(), Font.PLAIN, 12));
            panel.add(summaryLabel);
        }

        //Add authors
        if (spec.getAuthors() != null){
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>Authors", DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            panel.add(header);

            //Add authors
            for (GearSpecAuthor author : spec.getAuthors()){
                JLabel authorLabel = new JLabel(Utils.wrappedStringForString(author.getName()+" - "+author.getEmail(), DETAILS_INNER_WIDTH), JLabel.LEFT);
                authorLabel.setFont(new Font(authorLabel.getFont().getName(), Font.PLAIN, 12));
                panel.add(authorLabel);
            }
        }

        //Add Dependencies
        if (spec.getDependencies() != null){
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>Dependencies", DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            panel.add(header);

            //Add authors
            for (GearSpecDependency dependency : spec.getDependencies()){
                JLabel authorLabel = new JLabel(Utils.wrappedStringForString(dependency.getName()+" - "+dependency.getVersion(), DETAILS_INNER_WIDTH), JLabel.LEFT);
                authorLabel.setFont(new Font(authorLabel.getFont().getName(), Font.PLAIN, 12));
                panel.add(authorLabel);
            }
        }

        //Add License
        if (spec.getLicense() != null){
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>License", DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            panel.add(header);

            //Add authors header
            JLabel licenseLabel = new JLabel(spec.getLicense(), JLabel.LEFT);
            licenseLabel.setFont(new Font(licenseLabel.getFont().getName(), Font.PLAIN, 12));
            panel.add(licenseLabel);
        }

        //Add homepage
        if (spec.getHomepage() != null){
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>Homepage", DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            panel.add(header);

            //Add homepage
            JLabel homepageLabel = new JLabel(spec.getHomepage(), JLabel.LEFT);
            homepageLabel.setFont(new Font(homepageLabel.getFont().getName(), Font.PLAIN, 12));
            panel.add(homepageLabel);

            //Fetch page/readme
            String fetchUrl = spec.getHomepage();
            if (spec.getHomepage().contains("github.com")){
                fetchUrl = fetchUrl+"/blob/master/README.md";
            }
            fetchUrl = fetchUrl.replaceFirst("https", "http");

            //If url is reachable, go ahead and pull it down. Otherwise, show not found
            JEditorPane readmeEditorPane = new JEditorPane();
            if(ping(fetchUrl, 1000)){
                readmeEditorPane.setEditable(false);
                ReadmeScrollPane.setViewportView(readmeEditorPane);
                ReadmeScrollPane.revalidate();
                ReadmeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                ReadmeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

                try {
                    readmeEditorPane.setPage(fetchUrl);
                }catch (IOException e) {
                    readmeEditorPane.setContentType("text/html");
                    readmeEditorPane.setText("<html>Could not load</html>");
                }
            }
            else {
                readmeEditorPane.setContentType("text/html");
                readmeEditorPane.setText("<html>Could not load</html>");
            }

        }





        //Set panel in scrollpane
        DetailsScrollPane.setViewportView(panel);
        DetailsScrollPane.revalidate();
        DetailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        DetailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //DetailsScrollPane.setPreferredSize(new Dimension(panel.getSize().width, panel.getSize().height));
    }

    public static boolean ping(String url, int timeout) {
        url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

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
