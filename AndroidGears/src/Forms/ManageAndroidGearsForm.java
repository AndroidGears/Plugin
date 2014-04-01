package Forms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Utilities;
import java.awt.event.*;
import java.io.File;

import Renderers.GearSpecCellRenderer;
import Utilities.Utils;
import java.io.FilenameFilter;
import java.util.ArrayList;

import Models.GearSpec.GearSpec;
import Utilities.OSValidator;
import com.google.gson.Gson;

/**
 * Created by matthewyork on 4/1/14.
 */
public class ManageAndroidGearsForm{
    File androidGearsDirectory;
    private ArrayList<GearSpec> projects;

    private JTextField SearchTextField;
    private JTabbedPane tabbedPane1;
    private JEditorPane editorPane1;
    private JButton doneButton;
    public JPanel MasterPanel;
    private JPanel SearchPanel;
    private JPanel ReadmePanel;
    private JPanel DetailsPanel;
    private JList SearchList;
    private JList InstalledList;
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
        projects = projectsList(androidGearsDirectory, "");

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

                //Get projects and reload
                projects = projectsList(androidGearsDirectory, searchString);
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
        SearchList.setListData(projects.toArray());
        SearchList.setCellRenderer(new GearSpecCellRenderer());
        SearchList.setVisibleRowCount(projects.size());

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

        //Create and populate projects array
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


}
