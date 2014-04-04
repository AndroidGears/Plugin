package Actions;

import Forms.LintGearForm;
import Forms.ManageAndroidGearsForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

/**
 * Created by matthewyork on 4/3/14.
 */
public class LintGearAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        JFrame frame = new JFrame("Lint Android Gear");
        frame.setContentPane(new LintGearForm().MasterPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
