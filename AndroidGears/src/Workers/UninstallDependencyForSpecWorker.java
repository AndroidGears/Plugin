package Workers;

import Models.GearSpec.GearSpec;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/4/14.
 */
public class UninstallDependencyForSpecWorker extends SwingWorker<Void, Void> {

    private ArrayList<GearSpec> selectedSpecs;
    public boolean successful;

    public UninstallDependencyForSpecWorker(ArrayList<GearSpec> selectedSpecs) {
        this.selectedSpecs = selectedSpecs;
    }

    @Override
    protected Void doInBackground() throws Exception {

        for (GearSpec selectedSpec : this.selectedSpecs){
            if (selectedSpec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
                if (uninstallJar(selectedSpec)){
                    successful = true;
                    return null;
                }
            }
            else if (selectedSpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                if (uninstallModule(selectedSpec)){
                    successful = true;
                    return null;
                }
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
