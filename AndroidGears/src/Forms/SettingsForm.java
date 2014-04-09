package Forms;

import Models.GearSpec.GearSpec;
import Singletons.SettingsManager;
import Workers.Settings.SetCreateIgnoreEntryWorker;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/6/14.
 */
public class SettingsForm {

    private static int SETTINGS_FRAME_WIDTH = 500;

    public JPanel MasterPanel;
    private JButton resynchronizeAndroidGearsButton;
    private JTextPane allowAndroidGearsToTextPane;
    private JTextPane normallyTheGearsPluginTextPane;
    private JCheckBox createGitignoreEntryCheckBox;
    private JCheckBox autoSyncGearsCheckBox;
    private JTextPane byCheckingYesAndroidTextPane;

    public SettingsForm() {
        setupCheckBoxes();
        setupMiscUI();
    }

    private void setupCheckBoxes() {
        //Set ignore checkbox. Add check/uncheck listener
        createGitignoreEntryCheckBox.setSelected(SettingsManager.getInstance().getAutoIgnore());
        createGitignoreEntryCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                setCreateIgnoreSelected(createGitignoreEntryCheckBox.isSelected());
            }
        });

        //Set ignore checkbox. Add check/uncheck listener
        autoSyncGearsCheckBox.setSelected(SettingsManager.getInstance().getAutoIgnore());
        autoSyncGearsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (autoSyncGearsCheckBox.isSelected()){

                }
            }
        });
    }

    private void setupMiscUI() {

    }

    private void setCreateIgnoreSelected(final Boolean selected){
        SetCreateIgnoreEntryWorker worker = new SetCreateIgnoreEntryWorker(selected){
            @Override
            protected void done() {
                super.done();

                //Give some feedback as to the success or failure of the ignore entry
                if (success){
                    if (selected){
                        showAddedIgnoreDialog(MasterPanel);
                    }
                    else {
                        showRemovedIgnoreDialog(MasterPanel);
                    }
                }
                else {
                    if (selected){
                        showFailedToAddIgnoreDialog(MasterPanel);
                    }
                    else {
                        showFailedToRemoveIgnoreDialog(MasterPanel);
                    }
                }
            }
        };
        worker.execute();
    }

    ///////////////////////
    // Dialogs
    ///////////////////////

    private void showAddedIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears successfully added an entry to your ignore file",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showFailedToAddIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears failed to add an entry to your ignore file, but will still attempt to do so for new projects. For best results, ignore the \"Gears\" folder in your project directory.",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showRemovedIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears successfully removed its entry in your ignore file",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showFailedToRemoveIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears failed to remove its entry in your ignore file. Please find and remove the \"Gears\" folder entry if you would like to stop the ignoring of Android Gears.",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showSyncNowDialog(){

    }

    private void showResyncDialog(){

    }
}
