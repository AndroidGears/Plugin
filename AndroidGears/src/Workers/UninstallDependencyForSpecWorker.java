package Workers;

import Models.GearSpec.GearSpec;

import javax.swing.*;

/**
 * Created by matthewyork on 4/4/14.
 */
public class UninstallDependencyForSpecWorker extends SwingWorker<Void, Void> {

    private GearSpec spec;
    public boolean successful;

    public UninstallDependencyForSpecWorker(GearSpec spec) {
        this.spec = spec;
    }

    @Override
    protected Void doInBackground() throws Exception {
        return null;
    }
}
