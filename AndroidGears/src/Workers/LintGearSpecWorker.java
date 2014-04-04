package Workers;

import Models.GearSpec.GearSpec;
import Models.GearSpecLinter.GearSpecLintResult;
import Utilities.GearSpecLinter;

import javax.swing.*;

/**
 * Created by matthewyork on 4/3/14.
 */
public class LintGearSpecWorker extends SwingWorker<Void, Void> {
    private GearSpec spec;
    public GearSpecLintResult result = new GearSpecLintResult();

    public LintGearSpecWorker(GearSpec spec) {
        this.spec = spec;
    }

    @Override
    protected Void doInBackground() throws Exception {
        result = GearSpecLinter.lintSpec(this.spec);
        return null;
    }
}
