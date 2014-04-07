package Renderers;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

/**
 * Created by matthewyork on 4/7/14.
 */
public class ModuleCellRenderer extends JPanel implements ListCellRenderer {
    private static final Color HIGHLIGHT_COLOR = Color.decode("0x2B2B2B");
    private Color cellBackgroundColor = null;

    JLabel nameLabel;

    public ModuleCellRenderer() {
        setOpaque(true);
        cellBackgroundColor = getBackground();
    }

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
    {
        Module module = (Module)value;

        //Check for first runthrough
        if(nameLabel == null){
            //Initialize name panel
            this.setLayout(new FlowLayout());

            //Set layout
            this.setLayout(new FlowLayout(FlowLayout.LEFT));

            //Set name label
            nameLabel = new JLabel(module.getName(), JLabel.LEFT);
            //nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.PLAIN, 18));



            //Add components
            this.add(nameLabel);
        }
        else {
            //Set name label
            nameLabel.setText(module.getName());
        }

        if(isSelected) {
            setBackground(cellBackgroundColor.darker());
            setOpaque(true);
            //setForeground(Color.white);
        } else {
            setOpaque(false);
            //setForeground(Color.black);
        }
        return this;
    }
}
