package Utilities;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecDependency;
import Models.GearSpecRegister.GearSpecRegister;
import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/5/14.
 */
public class GearSpecRegistrar {
    public static Boolean registerGear(GearSpec spec, Project project){

        //Get specregister file
        File registrationFile = new File(project.getBasePath()+Utils.pathSeparator()+".specregister");

        //Create new Gson instance for use
        Gson gson = new Gson();

        //Create spec register
        GearSpecRegister register = null;

        //If the file exists, pull it back and add the dependency to it
        if (registrationFile.exists()){
            //Read back register from file
            String registerString;
            try {
                registerString = FileUtils.readFileToString(registrationFile);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            register = gson.fromJson(registerString, GearSpecRegister.class);

            //Check for array existence for safety
            if (register.installedGears == null){
                register.installedGears = new ArrayList<GearSpec>();
            }

            //Finally, add the installed gear
            register.installedGears.add(spec);
        }
        else {
            //Create register and
            register = new GearSpecRegister();
            register.installedGears = new ArrayList<GearSpec>() {};
            register.installedGears.add(spec);
        }

        //Write specs to file
        try {
            FileUtils.write(getRegisterPath(project), gson.toJson(register));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Boolean unregisterGear(GearSpec spec, Project project){
        //Get register
        GearSpecRegister register = GearSpecRegistrar.getRegister(project);

        if (register != null){
            if (register.installedGears != null){
               if (register.installedGears.remove(spec)){
                   //Create new Gson instance for use
                   Gson gson = new Gson();

                   //Write register to file
                   try {
                       FileUtils.write(getRegisterPath(project), gson.toJson(register));
                   } catch (IOException e) {
                       e.printStackTrace();
                       return false;
                   }


                   return true;
               }
            }
        }


        return false;
    }

    public static GearSpecRegister getRegister(Project project){
        //Get specregister file
        File registrationFile = new File(project.getBasePath()+Utils.pathSeparator()+".specregister");

        //If registration file exists, go get it!
        if (registrationFile.exists()){
            //Read back register from file
            String registerString = null;
            try {
                registerString = FileUtils.readFileToString(registrationFile);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return new Gson().fromJson(registerString, GearSpecRegister.class);
        }

        return null;
    }

    public static File getRegisterPath(Project project){
        return new File(project.getBasePath()+Utils.pathSeparator()+".specregister");
    }
}
