package Actions;

import Forms.CreateGearForm;
import Forms.ManageAndroidGearsForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

/**
 * Created by matthewyork on 4/2/14.
 */
public class CreateGearAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        JFrame frame = new JFrame("Create Android Gear");
        frame.setContentPane(new CreateGearForm().MasterPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
