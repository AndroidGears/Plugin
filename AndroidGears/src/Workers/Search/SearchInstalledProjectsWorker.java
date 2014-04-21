package Workers.Search;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecAuthor;
import Models.GearSpecRegister.GearSpecRegister;
import Utilities.GearSpecRegistrar;
import Utilities.Utils;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/5/14.
 */
public class SearchInstalledProjectsWorker extends SwingWorker<Void, Void>{

    private Project project;
    String searchString;
    public ArrayList<GearSpec> specs = new ArrayList<GearSpec>();

    public SearchInstalledProjectsWorker(Project project, String searchString) {
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
                    declaredSpec.setGearState(Utils.specStateForSpec(declaredSpec, project));

                    String[] searchParameters = searchString.split(" ");
                    for (String searchParamter : searchParameters){
                        String filterString = declaredSpec.getName().toLowerCase() + " " + declaredSpec.getVersion().toLowerCase();

                        //Gather tags
                        if (declaredSpec.getTags() != null){
                            for (String tag : declaredSpec.getTags()) {
                                filterString = filterString+tag.toLowerCase()+" ";
                            }
                        }

                        //Gather authors
                        for (GearSpecAuthor author : declaredSpec.getAuthors()) {
                            filterString = filterString+author.getName().toLowerCase()+" ";
                        }

                        //Filter with the search string over spec metadata
                        if(filterString.contains(searchParamter.toLowerCase())){
                            //Add sepec
                            installedSpecs.add(declaredSpec);
                        }
                    }
                }
            }

            this.specs = installedSpecs;
            return null;
        }

        return null;
    }
}
