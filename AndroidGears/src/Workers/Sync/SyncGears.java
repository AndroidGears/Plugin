package Workers.Sync;

import Models.GearSpec.GearSpec;
import Models.GearSpecRegister.GearSpecRegister;
import Utilities.GearSpecManager;
import Utilities.GearSpecRegistrar;
import Utilities.Utils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by matthewyork on 4/10/14.
 */
public class SyncGears extends SwingWorker<Void, Void>{

    Project project;
    Module module;
    public boolean success = false;

    public SyncGears(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    @Override
    protected Void doInBackground() throws Exception {

        //Get gear spec register
        GearSpecRegister register = GearSpecRegistrar.getRegister(project);

        if (register != null){
            for (GearSpec spec : register.declaredGears){
                //Set gear state
                spec.setGearState(Utils.specStateForSpec(spec, project));

                //Check to see if it is declared, if it is install it and its dependencies
                if (spec.getGearState() == GearSpec.GearState.GearStateDeclared){
                    //Install gear
                    if (GearSpecManager.installGear(spec, project, module));
                }
            }
        }


        return null;
    }
}
