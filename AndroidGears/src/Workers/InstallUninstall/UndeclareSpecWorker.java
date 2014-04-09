package Workers.InstallUninstall;

import Models.GearSpec.GearSpec;
import Utilities.GearSpecRegistrar;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by matthewyork on 4/7/14.
 */
public class UndeclareSpecWorker extends SwingWorker<Void, Void> {
    public Boolean success;
    GearSpec spec;
    Project project;

    public UndeclareSpecWorker(GearSpec spec, Project project) {
        this.spec = spec;
        this.project = project;
    }

    @Override
    protected Void doInBackground() throws Exception {

        success = GearSpecRegistrar.unregisterGear(spec, project);

        return null;
    }
}
