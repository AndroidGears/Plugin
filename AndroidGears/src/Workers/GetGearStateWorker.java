package Workers;

import Models.GearSpec.GearSpec;
import Models.GearSpecRegister.GearSpecRegister;
import Utilities.GearSpecRegistrar;
import Utilities.Utils;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by matthewyork on 4/6/14.
 */
public class GetGearStateWorker extends SwingWorker<Void, Void> {

    Project project;
    GearSpec selectedSpec;
    public GearSpec.GearState gearState;

    public GetGearStateWorker(Project project, GearSpec spec) {
        this.project = project;
        this.selectedSpec = spec;
    }

    @Override
    protected Void doInBackground() throws Exception {

        //Get register
        gearState = Utils.specStateForSpec(selectedSpec, project);

        return null;
    }
}
