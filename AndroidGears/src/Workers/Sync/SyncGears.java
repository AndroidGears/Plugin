package Workers.Sync;

import Models.GearSpec.GearSpec;
import Models.GearSpecRegister.GearSpecRegister;
import Utilities.GearSpecManager;
import Utilities.GearSpecRegistrar;
import Utilities.Utils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.io.File;

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

        //Handle any new gears that need to be installed
        handleNewAdditions(register);

        //Handle any gears that should be deleted (i.e. aren't in the specs register)
        handleNewDeletions(register);

        return null;
    }

    private void handleNewAdditions(GearSpecRegister register){


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
    }

    private void handleNewDeletions(GearSpecRegister register) {
        /*
        //Get Gear Directories
        File jarsDirectory = new File(project.getBasePath()+Utils.pathSeparator()+ "Gears"+ Utils.pathSeparator() + "Jars");
        File modulesDirectory = new File(project.getBasePath()+Utils.pathSeparator()+ "Gears"+ Utils.pathSeparator() + "Modules");

        //Remove any unnecessary JARS
        if (jarsDirectory.exists()){

            for (File jarFile : jarsDirectory.listFiles()){
                Boolean match = false;

                for (GearSpec spec : register.declaredGears){
                    if (jarFile.getName().equals(Utils.jarFileNameForSpecSource(spec.getSource()))){
                        return;
                    }
                }
            }
        }*/
    }
}
