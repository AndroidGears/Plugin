package Utilities;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecSource;
import com.google.gson.Gson;
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
        return String.format("<html><div style=\"width:%dpx;\">%s</div><html>", wrapWidth, inputString);
    }

    public static File androidGearsDirectory(){
        File androidGearsDirectory = null;
        //Setup file
        if (OSValidator.isWindows()) {
            androidGearsDirectory = new File(System.getProperty("user.home")+"/AndroidGears"); //C drive
        } else if (OSValidator.isMac()) {
            androidGearsDirectory = new File(System.getProperty("user.home")+"/.androidgears"); //Home folder
        } else if (OSValidator.isUnix()) {
            androidGearsDirectory = new File("~/.androidgears"); //Home folder
        } else if (OSValidator.isSolaris()) {
            androidGearsDirectory = new File("~/AndroidGears");//Home folder
        } else {
            androidGearsDirectory = new File("~/AndroidGears");//Home folder
        }

        return androidGearsDirectory;
    }

    public static String pathSeparator(){
        return (OSValidator.isWindows()) ? "\\":"/";
    }

    public static boolean ping(String url, int timeout) {
        //url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
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
                spec = new Gson().fromJson(specString, GearSpec.class);
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
        if (source.getSource_files().contains("/")){
            int lastPathSeparatorIndex = source.getSource_files().lastIndexOf("/");
            String fileName = source.getSource_files().substring(lastPathSeparatorIndex+1);

            return fileName;
        }
        else {
            return source.getSource_files();
        }
    }

    public static GearSpec.GearState specStateForSpec(GearSpec spec, Project project){
        if (spec.isRegistered(project)){
            if (spec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
                if (new File(project.getBasePath()+Utils.pathSeparator()+"Gears"+Utils.pathSeparator()+"Jars"+Utils.pathSeparator()+Utils.jarFileNameForSpecSource(spec.getSource())).exists()){
                    return GearSpec.GearState.GearStateInstalled;
                }
                else {
                    return GearSpec.GearState.GearStateDeclared;
                }
            }
            else if(spec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                if(new File(project.getBasePath()+Utils.pathSeparator()+"Gears"+Utils.pathSeparator()+"Modules"+Utils.pathSeparator()+spec.getName()).exists()){
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
}
