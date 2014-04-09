package Workers.Lint;

import Models.GearSpec.GearSpec;
import Models.GearSpecLinter.GearSpecLintResult;
import Utilities.GearSpecLinter;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by matthewyork on 4/3/14.
 */
public class LintGearSpecWorker extends SwingWorker<Void, String> {
    private GearSpec spec;
    public GearSpecLintResult result = new GearSpecLintResult();

    public LintGearSpecWorker(GearSpec spec) {
        this.spec = spec;
    }

    @Override
    protected Void doInBackground() throws Exception {
        result = GearSpecLinter.lintSpec(this.spec, this);
        return null;
    }
}
