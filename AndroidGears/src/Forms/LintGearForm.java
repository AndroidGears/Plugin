package Forms;

import Models.GearSpecLinter.GearSpecLintResult;
import Workers.LintGearSpecWorker;
import com.intellij.util.containers.ArrayListSet;

import javax.swing.*;
import java.util.List;

/**
 * Created by matthewyork on 4/3/14.
 */
public class LintGearForm {
    public JPanel MasterPanel;

    public LintGearForm() {
        LintGearSpecWorker worker = new LintGearSpecWorker(null){
            @Override
            protected void process(List<String> strings) {
                super.process(strings);
            }

            @Override
            protected void done() {
                super.done();

                GearSpecLintResult result = this.result;
            }
        };
        worker.run();
    }
}
