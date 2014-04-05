package Workers;

import Models.GearSpec.GearSpec;

import javax.swing.*;

/**
 * Created by matthewyork on 4/4/14.
 */
public class InstallDependencyForSpecWorker extends SwingWorker<Void, Void> {

    private GearSpec spec;
    public boolean successful;

    public InstallDependencyForSpecWorker(GearSpec spec) {
        this.spec = spec;
    }

    @Override
    protected Void doInBackground() throws Exception {

        if (spec != null){

            //Install dependency and sub-dependencies

            successful = true;
            return null;
        }

        successful = false;
        return null;
    }
}
