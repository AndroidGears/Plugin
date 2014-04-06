package Actions;

import Forms.ManageAndroidGearsForm;
import Forms.SettingsForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

/**
 * Created by matthewyork on 4/6/14.
 */
public class SettingsAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        JFrame frame = new JFrame("Android Gears Settings");
        frame.setContentPane(new SettingsForm().MasterPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
