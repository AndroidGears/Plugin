package Forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Created by matthewyork on 4/1/14.
 */
public class ManageAndroidGearsForm{
    private JTextField searchAndroidGearsTextField;
    private JTabbedPane tabbedPane1;
    private JList list1;
    private JList list2;
    private JEditorPane editorPane1;
    private JButton doneButton;
    public JPanel MasterPanel;
    private JPanel SearchPanel;
    private JPanel ReadmePanel;
    private JPanel DetailsPanel;

    private void createUIComponents() {

    }

    public ManageAndroidGearsForm() {
        setupDoneButton();
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
}
