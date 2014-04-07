package Singletons;

import Models.GearSpecRegister.GearSpecRegister;
import Models.Settings.GearSpecSettings;
import Models.Settings.ProjectSettings;
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
    private static String IGNORE_STRING = "#Android Gears\nGears/";

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
        File settingsFile = new File(System.getProperty("user.home")+"/.androidgearssettings");

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

        return true;
    }

    public Boolean saveSettings(){
        //Get settings file
        File settingsFile = new File(System.getProperty("user.home")+"/.androidgearssettings");

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

    public Boolean setAutoIgnore(boolean autoIgnore, JPanel panel){
        //Set setting
        settings.autoIgnore = autoIgnore;

        if (autoIgnore == true){
            if (addIgnoreEntry()){
                showAddedIgnoreDialog(panel);
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

        return true;
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

    private void showAddedIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears successfully added an entry to your ignore file",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showFailedToAddIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears failed to add an entry to your ignore file",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showRemovedIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears successfully removed its entry in your ignore file",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showFailedToRemoveIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears failed to remove its entry in your ignore file",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private Boolean addIgnoreEntry(){
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

                    //Check if it exists already. If it does, we're good
                    if (!ignoreFileString.contains(IGNORE_STRING)){
                        //Add entry
                        ignoreFileString = ignoreFileString+"\n"+IGNORE_STRING;

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
                if (createIgnore(ignoreFile)){
                    return true;
                }
            }
        }

        return false;
    }

    private Boolean removeIgnoreEntry(){
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

                    //Add entry
                    ignoreFileString = ignoreFileString.replace(IGNORE_STRING, "");

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

        return false;
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
}

