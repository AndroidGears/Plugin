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
}
