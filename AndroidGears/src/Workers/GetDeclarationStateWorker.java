package Workers;

import Models.GearSpec.GearSpec;
import Models.GearSpecRegister.GearSpecRegister;
import Utilities.GearSpecRegistrar;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by matthewyork on 4/6/14.
 */
public class GetDeclarationStateWorker extends SwingWorker<Void, Void> {

    Project project;
    GearSpec selectedSpec;
    public boolean declared = false;

    @Override
    protected Void doInBackground() throws Exception {

        //Get register
        GearSpecRegister register = GearSpecRegistrar.getRegister(this.project);

        if (register != null){
            if (register.declaredGears != null){
                //Iterate over declared specs to see if the selected spec is declared
                for (GearSpec spec : register.declaredGears){
                    if (selectedSpec.getName().equals(spec.getName()) && selectedSpec.getVersion().equals(spec.getVersion())){
                        declared = true;
                        break;
                    }
                }
            }
        }

        return null;
    }
}
