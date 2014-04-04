package Forms;

import Models.GearSpec.GearSpec;
import Models.GearSpecLinter.GearSpecLintResult;
import Utilities.Utils;
import Workers.LintGearSpecWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by matthewyork on 4/3/14.
 */
public class LintGearForm extends Component {
    public JPanel MasterPanel;
    private JButton lintButton;
    private JTextField SpecUrlTextField;
    private JLabel LintExplanationLabel;
    private JButton FindURLButton;
    private JTextArea LintResultsTextArea;

    public LintGearForm() {
       setupButtons();
        setupMiscUI();
    }

    private void setupButtons() {
        FindURLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setControlButtonsAreShown(true);

                int returnVal = fc.showOpenDialog(LintGearForm.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    //Check one more time for existence
                    if (file.exists()){

                        SpecUrlTextField.setText(file.getAbsolutePath());
                    }
                    else {
                        //SHOW DIALOG ABOUT NO FILE EXISTING
                    }
                }

                //Bring window back to the front
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(MasterPanel);
                topFrame.toFront();
            }
        });

        lintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                lintSpec();
            }
        });
    }

    private void setupMiscUI() {
        LintExplanationLabel.setText(Utils.wrappedStringForString(LintExplanationLabel.getText(), 500));
        SpecUrlTextField.setText(System.getProperty("user.home"));
    }

    /////////////////////
    // Spec Linting
    /////////////////////

    private void lintSpec() {
        //Get spec file from url text field
        File specFile = new File(SpecUrlTextField.getText());

        //Generate spec
        GearSpec spec = Utils.specForFile(specFile);

        //Lint spec
        LintGearSpecWorker worker = new LintGearSpecWorker(spec){

            @Override
            protected void done() {
                super.done();

                GearSpecLintResult result = this.result;

                //Show final linting results
                if (result != null){

                    LintResultsTextArea.setText(result.getResponseMessage());
                }
                else {
                    LintResultsTextArea.setText("Linting Error. Please try again");
                }

            }
        };
        worker.run();
    }
}
