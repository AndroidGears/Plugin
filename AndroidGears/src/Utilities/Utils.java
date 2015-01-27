package Utilities;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecSource;
import Singletons.SettingsManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by matthewyork on 4/1/14.
 */
public class Utils {

    public static String wrappedStringForString(String inputString, int wrapWidth){
        inputString = inputString.replace("\n", "<br/>");
        return String.format("<html><div style=\"width:%dpx;\">%s</div><html>", wrapWidth, inputString);
    }

    public static File androidGearsDirectory(){

        return new File(SettingsManager.getInstance().getSpecsPath()+Utils.pathSeparator()+"repos");
    }

    public static File getDefaultDirectory(){
        File defaultDirectory;
        //Setup file
        if (OSValidator.isWindows()) {
            defaultDirectory = new File(System.getProperty("user.home")+"/AndroidGears"); //C drive
        } else if (OSValidator.isMac()) {
            defaultDirectory = new File(System.getProperty("user.home")+"/.androidgears"); //Home folder
        } else if (OSValidator.isUnix()) {
            defaultDirectory = new File("~/.androidgears"); //Home folder
        } else if (OSValidator.isSolaris()) {
            defaultDirectory = new File("~/AndroidGears");//Home folder
        } else {
            defaultDirectory = new File("~/AndroidGears");//Home folder
        }

        return defaultDirectory;
    }

    public static String pathSeparator(){
        return (OSValidator.isWindows()) ? "\\":"/";
    }

    public static String newLine(){
        return (OSValidator.isWindows()) ? "\r\n":"\n";
    }

    public static boolean ping(String url, int timeout) {
        //url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }

    public static GearSpec specForFile(File specFile){
        GearSpec spec = null;

        if (specFile != null){
            if(specFile.exists()) {
                //Get string data
                String specString = null;
                try {
                    specString = FileUtils.readFileToString(specFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                //Get spec
                try{
                    spec = new Gson().fromJson(specString, GearSpec.class);
                }
                catch (JsonSyntaxException e){
                    e.printStackTrace();
                }
            }
        }

        return spec;
    }

    public static GearSpec specForInfo(String name, String version){
        //Create spec file
        File specFile = null;
        if (version != null){
            specFile = new File(Utils.androidGearsDirectory()+Utils.pathSeparator()+name+Utils.pathSeparator()+version+Utils.pathSeparator()+name+".gearspec");
        }
        else {
            String[] versions = versionsForProject(name);
            if (versions.length > 0){
                specFile = new File(Utils.androidGearsDirectory()+Utils.pathSeparator()+name+Utils.pathSeparator()+versions[versions.length-1]+Utils.pathSeparator()+name+".gearspec");
            }
        }

        return specForFile(specFile);
    }

    public static String[] versionsForProject(String project){
        File versionsDirectory = new File(Utils.androidGearsDirectory().getAbsolutePath()+Utils.pathSeparator()+project);
        return versionsDirectory.list();
    }

    public static String jarFileNameForSpecSource(GearSpecSource source){

        //Get element with jar name in it
        String sourceString;
        if(source.getUrl().toLowerCase().contains(".jar")){
            sourceString = source.getUrl();
        }
        else {
            sourceString = source.getSource_files();
        }

        //Parse out jar name
        if (sourceString.contains("/")){
            int lastPathSeparatorIndex = sourceString.lastIndexOf("/");
            String fileName = sourceString.substring(lastPathSeparatorIndex+1);

            return fileName;
        }
        else {
            return source.getSource_files();
        }
    }

    public static GearSpec.GearState specStateForSpec(GearSpec spec, Project project){
        if (spec.isRegistered(project)){
            //Make local separator for speed
            String pathSeparator = Utils.pathSeparator();

            if (spec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
                //TODO: Only checks for name, not version...
                if (new File(project.getBasePath()+pathSeparator+"Gears"+pathSeparator+"Jars"+pathSeparator+spec.getName()+pathSeparator+spec.getVersion()+pathSeparator+Utils.jarFileNameForSpecSource(spec.getSource())).exists()){
                    return GearSpec.GearState.GearStateInstalled;
                }
                else {
                    return GearSpec.GearState.GearStateDeclared;
                }
            }
            else if(spec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                //TODO: Only checks for name, not version...
                if(new File(project.getBasePath()+Utils.pathSeparator()+"Gears"+pathSeparator+"Modules"+pathSeparator+spec.getName()+pathSeparator+spec.getVersion()).exists()){
                    return GearSpec.GearState.GearStateInstalled;
                }
                else{
                    return GearSpec.GearState.GearStateDeclared;
                }
            }
            else {
                return GearSpec.GearState.GearStateDeclared;
            }
        }

        return GearSpec.GearState.GearStateUninstalled;
    }

    public static File fileInstallPathForSpec(GearSpec spec, Project project){
        if (spec != null & project != null){
            //Make local separator for speed
            String pathSeparator = Utils.pathSeparator();

            if (spec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
                return new File(project.getBasePath()+pathSeparator+"Gears"+pathSeparator+"Jars"+pathSeparator+spec.getName()+pathSeparator+spec.getVersion());
            }
            else if (spec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                return new File(project.getBasePath()+pathSeparator+"Gears"+pathSeparator+"Modules"+pathSeparator+spec.getName()+pathSeparator+spec.getVersion());
            }
        }

        return null;
    }
}
