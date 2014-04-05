package Workers;

import Models.GearSpec.GearSpec;

import javax.swing.*;

/**
 * Created by matthewyork on 4/4/14.
 */
public class UninstallDependencyForSpecWorker extends SwingWorker<Void, Void> {

    private GearSpec selectedSpec;
    public boolean successful;

    public UninstallDependencyForSpecWorker(GearSpec spec) {
        this.selectedSpec = spec;
    }

    @Override
    protected Void doInBackground() throws Exception {

        if (this.selectedSpec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
            if (uninstallJar(this.selectedSpec)){
                successful = true;
                return null;
            }
        }
        else if (this.selectedSpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
            if (uninstallModule(this.selectedSpec)){
                successful = true;
                return null;
            }
        }

        successful = false;
        return null;
    }

    private Boolean uninstallJar(GearSpec spec){


        //Remove dependencies, if nothing else depends on them

        return true;
    }

    private Boolean uninstallModule(GearSpec spec){


        //Remove dependencies, if nothing else depends on them

        return true;
    }
}
