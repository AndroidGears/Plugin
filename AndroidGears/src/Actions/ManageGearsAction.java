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
        JFrame frame = new JFrame("ManageAndroidGearsForm");
        frame.setContentPane(new ManageAndroidGearsForm().MasterPanel);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //frame.setLocation((dim.width-frame.getSize().width)/4, (dim.height-frame.getSize().height)/4);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
