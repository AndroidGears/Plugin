package Workers.Search;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecUpdate;
import Models.GearSpecRegister.GearSpecRegister;
import Utilities.GearSpecRegistrar;
import Utilities.Utils;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/9/14.
 */
public class GetUpdateableProjectsWorker extends SwingWorker<Void, Void>{

    private Project project;
    private String searchString;
    public ArrayList<GearSpecUpdate> specs = new ArrayList<GearSpecUpdate>();

    public GetUpdateableProjectsWorker(Project project, String searchString) {
        this.project = project;
        this.searchString = searchString;
    }

    @Override
    protected Void doInBackground() throws Exception {

        GearSpecRegister register = GearSpecRegistrar.getRegister(this.project);

        if (register != null){
            ArrayList<GearSpec> installedSpecs = new ArrayList<GearSpec>();
            for (GearSpec declaredSpec : register.declaredGears){
                if (Utils.specStateForSpec(declaredSpec, project) == GearSpec.GearState.GearStateInstalled){
                    declaredSpec.gearState = Utils.specStateForSpec(declaredSpec, project);
                    installedSpecs.add(declaredSpec);
                }
            }

            filterInstalledGears(installedSpecs);

            return null;
        }

        return null;
    }

    private  void filterInstalledGears(ArrayList<GearSpec> installedGears) {
        //Iterate over all installed gears
        for (GearSpec spec : installedGears){

            //Get all versions for spec
            String[] versions = Utils.versionsForProject(spec.getName());

            //If the version does not match the latest version, mark it as updateable and
            if (versions.length > 0){
                //If the version does not equal the last available version, then a
                if (!versions[versions.length -1].equals(spec.getVersion())){
                    GearSpecUpdate updateSpec = (GearSpecUpdate)spec;
                    updateSpec.setUpdateVersionNumber(versions[versions.length -1]);

                    //Add spec
                    this.specs.add(updateSpec);
                }
            }
        }
    }
}
