package Workers;

import Models.GearSpec.GearSpec;
import Models.GearSpecRegister.GearSpecRegister;
import Utilities.GearSpecRegistrar;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/5/14.
 */
public class GetInstalledProjectsWorker extends SwingWorker<Void, Void>{

    private Project project;
    public ArrayList<GearSpec> specs = new ArrayList<GearSpec>();

    public GetInstalledProjectsWorker(Project project) {
        this.project = project;
    }

    @Override
    protected Void doInBackground() throws Exception {

        GearSpecRegister register = GearSpecRegistrar.getRegister(this.project);

        if (register != null){
            this.specs = (register.installedGears != null) ? register.installedGears : new ArrayList<GearSpec>();
            return null;
        }

        return null;
    }
}
