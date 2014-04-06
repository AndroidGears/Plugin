package Workers;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecDependency;
import Utilities.GearSpecRegistrar;
import Utilities.Utils;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/4/14.
 */
public class UninstallDependencyForSpecWorker extends SwingWorker<Void, Void> {

    private ArrayList<GearSpec> selectedSpecs;
    private Project project;
    public boolean successful;

    public UninstallDependencyForSpecWorker(ArrayList<GearSpec> selectedSpecs, Project project) {
        this.selectedSpecs = selectedSpecs;
        this.project = project;
    }

    @Override
    protected Void doInBackground() throws Exception {

        for (GearSpec selectedSpec : this.selectedSpecs){
            if (selectedSpec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
                if (!uninstallJar(selectedSpec, this.project)){
                    successful = false;
                    return null;
                }
            }
            else if (selectedSpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                if (!uninstallModule(selectedSpec, this.project)){
                    successful = false;
                    return null;
                }
            }
        }


        successful = true;
        return null;
    }

    private Boolean uninstallJar(GearSpec spec, Project project){

        //Get the gears jar directory. If it doesn't exist, then we will count that as a win
        File libsDirectory = new File(project.getBasePath()+ Utils.pathSeparator()+ "Gears"+ Utils.pathSeparator() + "Jars");
        if (!libsDirectory.exists()){
            //Unregister just in case
            if (GearSpecRegistrar.unregisterGear(spec, project)){
                return true;
            }
            else {
                return false;
            }
        }

        //Get the jar file
        File jarFile = new File(libsDirectory.getAbsolutePath()+Utils.pathSeparator()+Utils.jarFileNameForSpecSource(spec.getSource()));

        //Delete the jar
        if (jarFile.exists()){
            try {
                FileUtils.forceDelete(jarFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        //Finally, unregister gear
        if (GearSpecRegistrar.unregisterGear(spec, project)){
            return true;
        }
        else {
            return false;
        }
    }

    private Boolean uninstallModule(GearSpec spec, Project project){


        //Remove dependencies, if nothing else depends on them

        //Finally, unregister gear
        if (GearSpecRegistrar.unregisterGear(spec, project)){
            return true;
        }
        else {
            return false;
        }
    }
}
