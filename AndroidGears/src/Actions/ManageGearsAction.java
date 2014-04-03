package Actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import Forms.ManageAndroidGearsForm;

import javax.swing.*;
import java.awt.*;

/**
 * Created by matthewyork on 3/31/14.
 */
public class ManageGearsAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        JFrame frame = new JFrame("Manage Android Gears");
        frame.setContentPane(new ManageAndroidGearsForm().MasterPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
