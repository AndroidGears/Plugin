package Utilities;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecSource;
import com.google.gson.Gson;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by matthewyork on 4/1/14.
 */
public class Utils {

    public static String stringFromFile(File file){
        if (file != null){
            if (file.exists()){
                if (file.isFile()){
                    //Get extension
                    String extension = "";
                    String fileName = file.getAbsolutePath();
                    int i = fileName.lastIndexOf(".");
                    if (i > 0){
                        extension = fileName.substring(i+1);
                    }

                    //Check extension
                    if (extension.equals("gearspec")){
                        //Open file
                        BufferedReader br = null;
                        try {
                            br = new BufferedReader(new FileReader(file.getAbsolutePath()));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        //Build string from file
                        StringBuilder sb = new StringBuilder();
                        try {
                            String line = br.readLine();

                            while (line != null) {
                                sb.append(line);
                                line = br.readLine();
                            }
                            String everything = sb.toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                br.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        return sb.toString();
                    }
                }
            }
        }

        return "";
    }

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
                String specString = Utils.stringFromFile(specFile);

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
}
