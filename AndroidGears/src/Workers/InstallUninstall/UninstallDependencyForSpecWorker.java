package Workers.InstallUninstall;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecDependency;
import Utilities.GearSpecManager;
import Utilities.GearSpecRegistrar;
import Utilities.Utils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/4/14.
 */
public class UninstallDependencyForSpecWorker extends SwingWorker<Void, Void> {

    private ArrayList<GearSpec> selectedSpecs;
    private Project project;
    private Module module;
    public boolean successful;

    public UninstallDependencyForSpecWorker(ArrayList<GearSpec> selectedSpecs, Project project, Module module) {
        this.selectedSpecs = selectedSpecs;
        this.project = project;
        this.module = module;
    }

    @Override
    protected Void doInBackground() throws Exception {

        for (GearSpec selectedSpec : this.selectedSpecs){
            if(!GearSpecManager.uninstallGear(selectedSpec, project, module)){
                successful = false;
                break;
            }
        }


        successful = true;
        return null;
    }
}
