package Workers.InstallUninstall;

import Models.GearSpec.GearSpec;
import Utilities.GearSpecRegistrar;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by matthewyork on 4/7/14.
 */
public class DeclareSpecWorker extends SwingWorker<Void, Void> {
    public Boolean success;
    GearSpec spec;
    Project project;

    public DeclareSpecWorker(GearSpec spec, Project project) {
        this.spec = spec;
        this.project = project;
    }

    @Override
    protected Void doInBackground() throws Exception {

        success = GearSpecRegistrar.registerGear(spec, project, GearSpec.GearState.GearStateDeclared);

        return null;
    }
}
