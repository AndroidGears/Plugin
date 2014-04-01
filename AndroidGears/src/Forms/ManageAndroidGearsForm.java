package Forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;

import Utilities.OSValidator;

/**
 * Created by matthewyork on 4/1/14.
 */
public class ManageAndroidGearsForm{
    private JTextField searchAndroidGearsTextField;
    private JTabbedPane tabbedPane1;
    private JEditorPane editorPane1;
    private JButton doneButton;
    public JPanel MasterPanel;
    private JPanel SearchPanel;
    private JPanel ReadmePanel;
    private JPanel DetailsPanel;
    private JTable InstalledTable;
    private JTable SearchTable;

    private void createUIComponents() {

    }

    public ManageAndroidGearsForm() {
        setupSearchTable();
        setupDoneButton();
    }

    private void setupSearchTable() {
        //Setup file
        File androidGearsDirectory = null;

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

        String[] directories = projectsList(androidGearsDirectory);
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

    private String[] projectsList(File androidGearsDirectory){
        return  androidGearsDirectory.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if(name.contains(".")){
                    return  false;
                }
                return new File(file, name).isDirectory();
            }
        });
    }
}
