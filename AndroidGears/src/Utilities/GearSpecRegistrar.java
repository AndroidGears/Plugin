package Utilities;

import Models.GearSpec.GearSpec;
import Models.GearSpecRegister.GearSpecRegister;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/5/14.
 */
public class GearSpecRegistrar {
    public static Boolean registerGear(GearSpec spec, Project project, GearSpec.GearState gearState){

        //Get specregister file
        File registrationFile = new File(project.getBasePath()+Utils.pathSeparator()+"GearSpecRegister");

        //Create new Gson instance for use
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Create spec register
        GearSpecRegister register = null;

        //Set gear spec state
        spec.setGearState(gearState);

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
            if (register.declaredGears == null){
                register.declaredGears = new ArrayList<GearSpec>();
            }

            //Check for matches already in the register (say, if you are installing an already declared spec)
            boolean match = false;
            int matchIndex = -1;
            for(int ii = 0; ii < register.declaredGears.size(); ii++){
                GearSpec declaredGear = register.declaredGears.get(ii);
                //Check for exact match
                if (declaredGear.getName().equals(spec.getName()) && declaredGear.getVersion().equals(spec.getVersion())){
                    //Flag a match
                    match = true;

                    //Make sure that the match reflect the most up to date spec state
                    declaredGear.setGearState(Utils.specStateForSpec(declaredGear, project));

                    break;
                }
                //Check for a possible new version coming in.
                else if (declaredGear.getName().equals(spec.getName())){
                    //Flag a match
                    match = true;

                    //Set match index so you know which value to replace with updated spec
                    matchIndex = ii;
                }
            }

            //Finally, add the installed gear
            if (!match){
                register.declaredGears.add(spec);
            }
            else{
                //Set the gear with the new version
                if (matchIndex > -1){
                    register.declaredGears.set(matchIndex, spec);
                }
            }
        }
        else {
            //Create register and
            register = new GearSpecRegister();
            register.declaredGears = new ArrayList<GearSpec>() {};
            register.declaredGears.add(spec);
        }

        //Write specNames to file
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
            if (register.declaredGears != null){
                for (GearSpec installedGear : register.declaredGears){
                    if (installedGear.getName().equals(spec.getName()) && installedGear.getVersion().equals(spec.getVersion())){
                        if (register.declaredGears.remove(installedGear)){
                            //Create new Gson instance for use
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();

                            //Write register to file
                            try {
                                FileUtils.forceDelete(getRegisterPath(project));
                                FileUtils.write(getRegisterPath(project), gson.toJson(register));
                            } catch (IOException e) {
                                e.printStackTrace();
                                return false;
                            }

                            return true;
                        }
                    }
                }

            }
        }


        return false;
    }

    public static GearSpecRegister getRegister(Project project){
        //Get specregister file
        File registrationFile = new File(project.getBasePath()+Utils.pathSeparator()+"GearSpecRegister");

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
        return new File(project.getBasePath()+Utils.pathSeparator()+"GearSpecRegister");
    }
}
