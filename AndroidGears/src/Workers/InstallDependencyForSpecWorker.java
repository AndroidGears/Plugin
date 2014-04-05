package Workers;

import Models.GearSpec.GearSpec;
import Utilities.Utils;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.rmi.CORBA.Util;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by matthewyork on 4/4/14.
 */
public class InstallDependencyForSpecWorker extends SwingWorker<Void, Void> {

    private GearSpec spec;
    private Project project;
    public boolean successful;

    public InstallDependencyForSpecWorker(GearSpec spec, Project project) {
        this.spec = spec;
        this.project = project;
    }

    @Override
    protected Void doInBackground() throws Exception {

        if (spec != null){
            if (spec.getType().equals(GearSpec.SPEC_TYPE_JAR)){

            }
            else if (spec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                if (installModule()){
                    successful = true;
                    return null;
                }
            }


        }

        successful = false;
        return null;
    }


    private Boolean installModule(){
        //Install dependency and sub-dependencies
        File specDirectory = new File(project.getBasePath() + Utils.pathSeparator() + spec.getName());

        //Delete the directory. This is for other versions installed
        if (specDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(specDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        //Clone down repo
        try {
            Git.cloneRepository()
                    .setURI(spec.getSource().getUrl())
                    .setBranch(spec.getSource().getTag())
                    .setDirectory(specDirectory)
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }

        //Check out appropriate branch
        File gitDirectory = new File(specDirectory.getAbsolutePath() + Utils.pathSeparator() + ".git");
        try {
            Git git = Git.open(gitDirectory);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //Move specified folder to root, if paremeter exists
        if (spec.getSource().getSource_files() != null){
            if (!spec.getSource().getSource_files().equals("")){
                //Replaces path seperators with system dependent ones (windows, mac, etc.)
                String systemSpecificInnerPath = spec.getSource().getSource_files().replace("/", Utils.pathSeparator());

                //Get module directory
                File moduleDirectory = new File(specDirectory.getAbsolutePath()+Utils.pathSeparator()+systemSpecificInnerPath);

                //Delete other folders, including source control
                for(File file : specDirectory.listFiles()){
                    //Delete all folders that aren't the lib folder
                    if (!file.getAbsolutePath().equals(moduleDirectory.getAbsolutePath())){
                        try {
                            if (file.isDirectory()){
                                FileUtils.deleteDirectory(file);
                            }
                            else {
                                FileUtils.forceDelete(file);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }

                //Move module to root
                for(File file : moduleDirectory.listFiles()){
                    try {
                        if (file.isDirectory()){
                            FileUtils.copyDirectoryToDirectory(file, specDirectory);

                        }
                        else {
                            FileUtils.copyFileToDirectory(file, specDirectory);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                //Finally, delete old inner module folder
                try {
                    FileUtils.deleteDirectory(moduleDirectory);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }




        return true;
    }

    private void installJar(){

    }
}
