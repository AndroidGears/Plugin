package Workers;

import Models.GearSpec.GearSpec;
import Utilities.Utils;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aaronfleshner on 4/27/14.
 */
public class GetAllSpecsListWorker extends SwingWorker<Void, Void> {

    public ArrayList<String> specNames = new ArrayList<String>();

    public GetAllSpecsListWorker() {

    }

    @Override
    protected Void doInBackground() throws Exception {
        specNames = libsList(Utils.androidGearsDirectory());
        return null;
    }

    private ArrayList<String> libsList(File androidGearsDirectory) {
        /*
        //Check for empty search string
        if(searchString.equals("")){
            return new ArrayList<GearSpec>();
        }*/

        //Create gson instance for use in parsing specs
        final Gson gson = new Gson();

        //Create array for storing matched specs
        final ArrayList<GearSpec> projectList = new ArrayList<GearSpec>();

        //If there is a searchstring, get matches!
        String directories[] = androidGearsDirectory.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if (name.contains(".")) { //No hidden folders!
                    return false;
                } else if (!(new File(file.getAbsolutePath() + Utils.pathSeparator() + name).isDirectory())) {
                    return false;
                }
                //If it matches the filename, lets skip the metadata search and add it directly. Save the cycles!
                else {
                    return true;
                }
            }
        });
        ArrayList<String> specs = new ArrayList<String>();
        for (String lib : directories) {
            specs.add(lib);
        }
        return specs;
    }
}

