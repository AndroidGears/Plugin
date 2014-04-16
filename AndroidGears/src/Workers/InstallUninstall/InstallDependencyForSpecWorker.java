package Workers.InstallUninstall;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecDependency;
import Utilities.GearSpecManager;
import Utilities.GearSpecRegistrar;
import Utilities.Utils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by matthewyork on 4/4/14.
 */
public class InstallDependencyForSpecWorker extends SwingWorker<Void, Void> {

    private GearSpec selectedSpec;
    private Project project;
    private Module module;
    public boolean successful;

    public InstallDependencyForSpecWorker(GearSpec spec, Project project, Module module) {
        this.selectedSpec = spec;
        this.project = project;
        this.module = module;
    }

    @Override
    protected Void doInBackground() throws Exception {

        if (selectedSpec != null){
            successful = GearSpecManager.installGear(selectedSpec, project, module);
        }

        return null;
    }
}
