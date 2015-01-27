package Singletons;

import Models.GearSpecRegister.GearSpecRegister;
import Models.Settings.GearSpecSettings;
import Models.Settings.ProjectSettings;
import Utilities.OSValidator;
import Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by matthewyork on 4/6/14.
 */
public class SettingsManager {
    //Singleton instance for settings
    private static SettingsManager settingsManager;
    private Project[] targetProjects;
    private static String IGNORE_STRING = Utils.newLine()+Utils.newLine()+"#Android Gears"+Utils.newLine()+"Gears/";
    private static String IGNORE_COMMENT_STRING = "#Android Gears";
    private static String IGNORE_CONTENT_STRING = "Gears/";

    //Project Settings
    private ProjectSettings projectSettings = new ProjectSettings();

    private GearSpecSettings settings = new GearSpecSettings();

    protected SettingsManager() {

    }

    public static SettingsManager getInstance() {
        if (settingsManager == null) {
            settingsManager =  new SettingsManager();
        }

        return settingsManager;
    }

    ///////////////////////
    // Loading / Saving
    ///////////////////////

    public Boolean loadSettings(){

        //Get settings file
        File settingsFile = new File(Utils.getDefaultDirectory().getAbsolutePath()+Utils.pathSeparator()+".gearssettings");

        if (settingsFile.exists()){
            //Create new Gson instance for use
            Gson gson = new Gson();

            String settingsString = null;
            try {
                settingsString = FileUtils.readFileToString(settingsFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            //Load file into settings
            this.settings = new Gson().fromJson(settingsString, GearSpecSettings.class);

        }
        else {
            //Save a new default copy of the settings
            saveSettings();
        }

        return true;
    }

    public Boolean saveSettings(){


        //Get settings file
        File settingsFile = new File(Utils.getDefaultDirectory().getAbsolutePath()+Utils.pathSeparator()+".gearssettings");

        //Create new Gson instance for use
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            //Delete the settings, if they exist
            if (settingsFile.exists()){
                FileUtils.forceDelete(settingsFile);
            }

            //Write settings to file
            FileUtils.write(settingsFile, gson.toJson(settings));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean loadProjectSettings(Project project){
        //Get settings file
        File settingsFile = new File(project.getBasePath()+"/.gearsproject");

        if (settingsFile.exists()){
            //Create new Gson instance for use
            Gson gson = new Gson();

            String settingsString = null;
            try {
                settingsString = FileUtils.readFileToString(settingsFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            //Load file into settings
            this.projectSettings = new Gson().fromJson(settingsString, ProjectSettings.class);

        }

        return true;
    }

    public Boolean saveProjectSettings(Project project){
        //Get settings file
        File settingsFile = new File(project.getBasePath()+"/.gearsproject");

        //Create new Gson instance for use
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            //Delete the settings, if they exist
            if (settingsFile.exists()){
                FileUtils.forceDelete(settingsFile);
            }

            //Write settings to file
            FileUtils.write(settingsFile, gson.toJson(projectSettings));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    ///////////////////////
    // Auto Ignore
    ///////////////////////

    public Boolean getAutoIgnore(){
        return settings.autoIgnore;
    }

    public Boolean setCreateIgnore(boolean createIgnore){


        /*
        if (autoIgnore == true){
            if (addIgnoreEntry()){
                //showAddedIgnoreDialog(panel);
            }
            else {
                showFailedToAddIgnoreDialog(panel);
            }
        }
        else {
            if (removeIgnoreEntry()){
                showRemovedIgnoreDialog(panel);
            }
            else {
                showFailedToRemoveIgnoreDialog(panel);
            }
        }

        return true;*/

        //Set setting
        if (createIgnore){
            if (addIgnoreEntry()){
                settings.autoIgnore = createIgnore;
                saveSettings();
                return true;
            }
        }
        else {
            if (removeIgnoreEntry()){
                settings.autoIgnore = createIgnore;
                saveSettings();
                return true;
            }
        }

        return false;
    }

    public Boolean ignoreExists(){

        ProjectManager pm = ProjectManager.getInstance();
        targetProjects = pm.getOpenProjects();

        if (targetProjects.length > 0){
            Project project = targetProjects[0];

            //Get ignore file
            File ignoreFile = new File(project.getBasePath()+ Utils.pathSeparator()+".gitignore");

            if (ignoreFile.exists()){
                try {
                    //Read back ignore file
                    String ignoreFileString = FileUtils.readFileToString(ignoreFile);

                    //Return true if it exists
                    return  ignoreFileString.contains(IGNORE_STRING);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            else {
                if (createIgnore(ignoreFile)){
                    return true;
                }
            }
        }

        return false;
    }

    private Boolean addIgnoreEntry(){
        ProjectManager pm = ProjectManager.getInstance();
        targetProjects = pm.getOpenProjects();

        if (targetProjects.length > 0){
            Project project = targetProjects[0];

            //Get ignore file
            File gitignore = new File(project.getBasePath()+ Utils.pathSeparator()+".gitignore");
            File hgignoreFile = new File(project.getBasePath()+ Utils.pathSeparator()+".hgignore");
            File ignoreFile =null;

            //Find valid ignore file
            if (gitignore.exists()){
                ignoreFile = gitignore;
            }
            else if (hgignoreFile.exists()){
                ignoreFile = hgignoreFile;
            }

            if (ignoreFile.exists()){
                try {
                    //Read back ignore file
                    String ignoreFileString = FileUtils.readFileToString(ignoreFile);

                    //Check if it exists already. If it does, we're good
                    if (!ignoreFileString.contains(IGNORE_COMMENT_STRING) && !ignoreFileString.contains(IGNORE_CONTENT_STRING)){
                        //Add entry
                        ignoreFileString = ignoreFileString+IGNORE_STRING;

                        //Write back changes to gitignore
                        FileUtils.forceDelete(ignoreFile);
                        FileUtils.write(ignoreFile, ignoreFileString);
                    }

                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            else {
                //If no gitignore exists, create one!
                if (createIgnore(gitignore)){
                    return true;
                }
            }
        }

        return true;
    }

    private Boolean removeIgnoreEntry(){
        ProjectManager pm = ProjectManager.getInstance();
        targetProjects = pm.getOpenProjects();

        if (targetProjects.length > 0){
            Project project = targetProjects[0];

            //Get ignore file
            File gitignore = new File(project.getBasePath()+ Utils.pathSeparator()+".gitignore");
            File hgignoreFile = new File(project.getBasePath()+ Utils.pathSeparator()+".hgignore");
            File ignoreFile =null;

            //Find valid ignore file
            if (gitignore.exists()){
                ignoreFile = gitignore;
            }
            else if (hgignoreFile.exists()){
                ignoreFile = hgignoreFile;
            }

            if (ignoreFile != null){
                try {
                    //Read back ignore file
                    String ignoreFileString = FileUtils.readFileToString(ignoreFile);

                    //remove entry
                    ignoreFileString = ignoreFileString.replace(IGNORE_COMMENT_STRING, "");
                    ignoreFileString = ignoreFileString.replace(IGNORE_CONTENT_STRING, "");

                    //Write back changes to gitignore
                    FileUtils.forceDelete(ignoreFile);
                    FileUtils.write(ignoreFile, ignoreFileString);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            else {
                if (createIgnore(ignoreFile)){
                    return true;
                }
            }
        }

        return true;
    }

    private Boolean createIgnore(File ignoreFile){
        try {
            //Delete the settings, if they exist
            if (ignoreFile.exists()){
                FileUtils.forceDelete(ignoreFile);
            }

            //Write settings to file
            FileUtils.write(ignoreFile, IGNORE_STRING);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    ///////////////////////
    // Auto Sync
    ///////////////////////

    public Boolean getAutoSync(){
        return this.settings.autosyncGears;
    }

    public Boolean setAutoSync(Boolean autoSync){
        this.settings.autosyncGears = autoSync;

        return saveSettings();
    }

    ///////////////////////
    // Modules
    ///////////////////////

    public String getMainModule(){
        if (projectSettings != null){
            if (projectSettings.mainModule != null){
                return projectSettings.mainModule;
            }
        }

        return "";
    }

    public void setMainModule(String mainModule, Project project){
        if (mainModule != null){
            if (projectSettings != null){
                projectSettings.mainModule = mainModule;
            }
            saveProjectSettings(project);
        }
    }

    ///////////////////////
    // Spec Repository Path
    ///////////////////////

    public String getSpecsPath(){
        if (this.settings.specsPath == null){
            this.settings.specsPath = Utils.getDefaultDirectory().getAbsolutePath();
            saveSettings();
        }

        return this.settings.specsPath;
    }

    public void setSpecsPath(String path){
        if (path != null){
            this.settings.specsPath = path;
            saveSettings();
        }
    }
}

