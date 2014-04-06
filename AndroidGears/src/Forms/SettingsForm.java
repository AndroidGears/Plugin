package Forms;

import Singletons.SettingsManager;
import Utilities.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by matthewyork on 4/6/14.
 */
public class SettingsForm {

    private static int SETTINGS_FRAME_WIDTH = 500;

    public JPanel MasterPanel;
    private JCheckBox GitIgnoreCheckBox;
    private JLabel IgnoreExplanationLabel;

    public SettingsForm() {
        setupCheckBoxes();
        setupMiscUI();
    }

    private void setupCheckBoxes() {
        //Set ignore checkbox. Add check/uncheck listener
        GitIgnoreCheckBox.setSelected(SettingsManager.getInstance().getAutoIgnore());
        GitIgnoreCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (GitIgnoreCheckBox.isSelected()){

                }
            }
        });
    }

    private void setupMiscUI() {
        //Setup ignore wrapping
        IgnoreExplanationLabel.setText(Utils.wrappedStringForString(IgnoreExplanationLabel.getText(), SETTINGS_FRAME_WIDTH - 10));
    }
}
