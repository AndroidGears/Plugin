package Workers;

import Models.GearSpec.GearSpec;
import Utilities.OSValidator;
import Utilities.Utils;
import com.google.gson.Gson;
import com.intellij.util.Url;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/2/14.
 */
public class SearchProjectListWorker  extends SwingWorker<Void, Void> {

    private String searchString;
    private File file;
    public ArrayList<GearSpec> specs = new ArrayList<GearSpec>();

    public SearchProjectListWorker(String searchString, File file) {
        this.searchString = searchString;
        this.file = file;
    }

    @Override
    protected Void doInBackground() throws Exception {
        specs = projectsList(file, searchString);
        return null;
    }

    private ArrayList<GearSpec> projectsList(File androidGearsDirectory, final String searchString){
        //Check for empty search string
        if(searchString.equals("")){
            return new ArrayList<GearSpec>();
        }

        //If there is a searchstring, get matches!
        String directories[] =  androidGearsDirectory.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if(name.contains(".")){ //No hidden folders!
                    return  false;
                }
                else if (name.toLowerCase().contains(searchString.toLowerCase())){ //Accept only those that match your search string
                    return true;
                }

                return false;
            }
        });

        //Create gson instance for use in parsing specs
        Gson gson = new Gson();

        //Get path separator
        String pathSeparator = (OSValidator.isWindows()) ? "\\":"/";

        //Create and populate searchProjects array
        ArrayList<GearSpec> projectList = new ArrayList<GearSpec>();
        for (String directory : directories){
            //Get versions for spec
            String[] versions = versionsForProject(directory, pathSeparator);

            //Build spec location
            File specFile = new File(androidGearsDirectory.getAbsolutePath()+pathSeparator+directory+pathSeparator+versions[versions.length-1]+pathSeparator+directory+".gearspec");

            //Read file
            if(specFile.exists()) {
                String specString = Utils.stringFromFile(specFile);

                //Get spec
                GearSpec spec = gson.fromJson(specString, GearSpec.class);

                //Create project and add to project list
                projectList.add(spec);
            }
        }

        return projectList;
    }

    private String[] versionsForProject(String project, String pathSeparator){
        File versionsDirectory = new File(Utils.androidGearsDirectory().getAbsolutePath()+pathSeparator+project);
        return versionsDirectory.list();
    }
}
