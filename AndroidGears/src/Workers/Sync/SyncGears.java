package Workers.Sync;

import Models.GearSpec.GearSpec;
import Models.GearSpecRegister.GearSpecRegister;
import Utilities.GearSpecManager;
import Utilities.GearSpecRegistrar;
import Utilities.Utils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

        //Handle any gears that should be deleted (i.e. aren't in the specNames register)
        handleNewDeletions(register);

        //Handle any new gears that need to be installed
        handleNewAdditions(register);

        return null;
    }

    private void handleNewDeletions(GearSpecRegister register) {
        //Make local copy for speed
        String pathSeparator = Utils.pathSeparator();

        //Get Gear Directories
        File jarsDirectory = new File(project.getBasePath() + pathSeparator+ "Gears"+ pathSeparator + "Jars");
        File modulesDirectory = new File(project.getBasePath() + pathSeparator+ "Gears"+ pathSeparator + "Modules");

        //Array to hold all gears to be removed
        ArrayList<File> gearsToBeRemoved = new ArrayList<File>();

        //Collect unnecessary jars
        gearsToBeRemoved.addAll(collectGearsToRemove(jarsDirectory, register, pathSeparator));

        //Collect unnecessary modules
        gearsToBeRemoved.addAll(collectGearsToRemove(modulesDirectory, register, pathSeparator));

        //Remove all jars and modules that were not listed in the Gearspec
        for (File gearDirectory : gearsToBeRemoved){
            String versionNumber = "";

            //Find the version from the gear directory (should only be one directory!)
            for (File versionDirectory : gearDirectory.listFiles()){
                if (versionDirectory.isDirectory()){
                    versionNumber = versionDirectory.getName();
                }
            }

            //Fetch gear from name and version number
            GearSpec spec = Utils.specForInfo(gearDirectory.getName(), versionNumber);

            //Uninstall spec
            GearSpecManager.uninstallGear(spec, project, module);
        }

    }

    private ArrayList<File> collectGearsToRemove(File gearTypeDirectory, GearSpecRegister register, String pathSeparator){

        //Array to hold all gears to be removed
        ArrayList<File> gearsToBeRemoved = new ArrayList<File>();

        //Remove unneccessary gears
        if (gearTypeDirectory.exists()){
            //Iterate over all jars
            for (File gearDirectory : gearTypeDirectory.listFiles()){
                if (gearDirectory.isDirectory()){
                    Boolean match = false;

                    //Iterate through specNames register to find a match.
                    for (GearSpec spec : register.declaredGears){
                        //If there is a spec listed, by that name, check its version
                        if (gearDirectory.getName().equals(spec.getName())){
                            File versionDirectory = new File(gearDirectory.getAbsolutePath() + pathSeparator + spec.getVersion());

                            if (versionDirectory.exists()){
                                match = true;
                                break;
                            }
                        }
                    }

                    //If no match is found, trash the directory
                    if (!match){
                        gearsToBeRemoved.add(gearDirectory);
                    }
                }
            }
        }

        return gearsToBeRemoved;
    }

    private void handleNewAdditions(GearSpecRegister register){
        if (register != null){
            for (GearSpec spec : register.declaredGears){
                //Set gear state
                spec.setGearState(Utils.specStateForSpec(spec, project));

                //Check to see if it is declared, if it is install it and its dependencies
                if (spec.getGearState() == GearSpec.GearState.GearStateDeclared){
                    //Install gear
                    GearSpecManager.installGear(spec, project, module);
                }
            }
        }
    }
}
