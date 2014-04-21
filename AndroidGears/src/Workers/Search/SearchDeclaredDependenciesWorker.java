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
 * Created by matthewyork on 4/6/14.
 */
public class SearchDeclaredDependenciesWorker extends SwingWorker<Void, Void> {

    private Project project;
    private String searchString;
    public ArrayList<GearSpec> specs = new ArrayList<GearSpec>();

    public SearchDeclaredDependenciesWorker(Project project, String searchString) {
        this.project = project;
        this.searchString = searchString;
    }

    @Override
    protected Void doInBackground() throws Exception {

        //Get register
        GearSpecRegister register = GearSpecRegistrar.getRegister(this.project);


        if (register != null){
            //If not empty, search over all fields for matches
            String[] searchParameters = searchString.split(" ");
            ArrayList<GearSpec> gears = new ArrayList<GearSpec>();
            for (GearSpec spec : register.declaredGears){
                for (String searchParamter : searchParameters){
                    String filterString = spec.getName().toLowerCase() + " " + spec.getVersion().toLowerCase();

                    //Gather tags
                    if (spec.getTags() != null){
                        for (String tag : spec.getTags()) {
                            filterString = filterString+tag.toLowerCase()+" ";
                        }
                    }

                    //Gather authors
                    for (GearSpecAuthor author : spec.getAuthors()) {
                        filterString = filterString+author.getName().toLowerCase()+" ";
                    }

                    //Filter with the search string over spec metadata
                    if(filterString.contains(searchParamter.toLowerCase())){
                        //Set spec state
                        spec.setGearState(Utils.specStateForSpec(spec, project));

                        //Add gear
                        gears.add(spec);
                        break;
                    }
                }
            }

            this.specs = gears;
        }

        return null;
    }


}
