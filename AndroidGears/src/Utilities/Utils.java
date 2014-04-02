package Utilities;

import java.io.*;

/**
 * Created by matthewyork on 4/1/14.
 */
public class Utils {
    public static String stringFromFile(File file){
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

    public static String wrappedStringForString(String inputString, int wrapWidth){
        return String.format("<html><div style=\"width:%dpx;\">%s</div><html>", 190, inputString);
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
}
