package Renderers;

import Models.GearSpec.GearSpec;

import javax.swing.*;
import java.awt.*;

import static javax.swing.text.StyleConstants.setIcon;

/**
 * Created by matthewyork on 4/1/14.
 */

public class GearSpecCellRenderer extends JPanel implements ListCellRenderer {
    private static final Color HIGHLIGHT_COLOR = Color.decode("0x2B2B2B");

    JLabel nameLabel;

    public GearSpecCellRenderer() {
        setOpaque(true);

    }

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
    {
        GearSpec spec = (GearSpec)value;

        //Add name
        if(nameLabel == null){
            nameLabel = new JLabel(spec.getName(), JLabel.LEFT);
            nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.PLAIN, 21));
            this.add(nameLabel);
        }
        else {
            nameLabel.setText(spec.getName());
        }

        if(isSelected) {
            setBackground(HIGHLIGHT_COLOR);
            setOpaque(true);
            //setForeground(Color.white);
        } else {
            setOpaque(false);
            //setForeground(Color.black);
        }
        return this;
    }
}
