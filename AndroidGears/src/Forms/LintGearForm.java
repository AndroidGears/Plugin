package Forms;

import Models.GearSpec.GearSpec;
import Models.GearSpecLinter.GearSpecLintResult;
import Utilities.Utils;
import Workers.Lint.LintGearSpecWorker;

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
    private JPanel LintProgressPanel;
    private JLabel LoadingSpinnerLabel;
    private JLabel LintingStatusLabel;

    public LintGearForm() {
       setupButtons();
        setupMiscUI();
    }

    private void setupButtons() {
        FindURLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Get top level frame
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(MasterPanel);

                //Create dialog for choosing gearspec file
                System.setProperty("apple.awt.fileDialogForDirectories", "false");
                FileDialog fd = new FileDialog(topFrame, "Choose a .gearspec file", FileDialog.LOAD);
                fd.setDirectory(System.getProperty("user.home"));
                fd.setFile("*.gearspec");
                fd.setVisible(true);
                //Get file
                String filename = fd.getFile();
                if (filename == null)
                    System.out.println("You cancelled the choice");
                else {
                    System.out.println("You chose " + filename);

                    //Get spec file
                    File specFile = new File(fd.getDirectory()+Utils.pathSeparator()+filename);

                    //If it exists, set it as the selected file path
                    if (specFile.exists()){
                        SpecUrlTextField.setText(specFile.getAbsolutePath());
                    }
                }
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

        //Hide linting spinner at the bottom
        LintingStatusLabel.setVisible(false);
        LoadingSpinnerLabel.setVisible(false);
    }



    /////////////////////
    // Spec Linting
    /////////////////////

    private void lintSpec() {
        //Show linting spinner
        LintingStatusLabel.setVisible(true);
        LoadingSpinnerLabel.setVisible(true);

        //Get spec file from url text field
        File specFile = new File(SpecUrlTextField.getText());

        //Generate spec
        GearSpec spec = Utils.specForFile(specFile);

        if(spec != null){
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

                    //Hide linting spinner at the bottom
                    LintingStatusLabel.setVisible(false);
                    LoadingSpinnerLabel.setVisible(false);
                }
            };
            worker.execute();
        }
        else {
            LintResultsTextArea.setText("Linting Error\n\n- JSON syntax error.\n  Please ensure your gearspec is a valid JSON object");

            //Hide linting spinner at the bottom
            LintingStatusLabel.setVisible(false);
            LoadingSpinnerLabel.setVisible(false);
        }
    }


}
