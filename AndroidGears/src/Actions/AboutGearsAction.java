package Actions;

import Forms.AboutGearsForm;
import Forms.SettingsForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

/**
 * Created by matthewyork on 12/3/14.
 */
public class AboutGearsAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        JFrame frame = new JFrame("About Android Gears");
        frame.setContentPane(new AboutGearsForm().MasterPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
