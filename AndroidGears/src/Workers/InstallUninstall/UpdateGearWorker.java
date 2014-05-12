package Workers.InstallUninstall;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecUpdate;
import Utilities.GearSpecManager;
import Utilities.Utils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by matthewyork on 4/9/14.
 */
public class UpdateGearWorker extends SwingWorker<Void, Void> {

    private GearSpecUpdate selectedSpec;
    private Project project;
    private Module module;
    public boolean successful = false;

    public UpdateGearWorker(GearSpecUpdate selectedSpec, Project project, Module module) {
        this.selectedSpec = selectedSpec;
        this.project = project;
        this.module = module;
    }

    @Override
    protected Void doInBackground() throws Exception {

        //Uninstall jar or module
        if (selectedSpec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
            if (!GearSpecManager.uninstallJar(selectedSpec, project, module)){
                return null;
            }
        }
        else if (selectedSpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
            if (!GearSpecManager.uninstallModule(selectedSpec, project, module)){
                return null;
            }
        }

        //Get the updatedSpec
        GearSpec updateSpec = Utils.specForInfo(selectedSpec.getName(), selectedSpec.getUpdateVersionNumber());

        if (updateSpec != null){
            //Install either the new jar or module. We have to do it this way in case someone changes library types
            if (updateSpec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
                this.successful = GearSpecManager.installJar(updateSpec, project, module, null);
            }
            else if (selectedSpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                this.successful = GearSpecManager.installModule(updateSpec, project, module, null);
            }
        }

        return null;
    }
}
