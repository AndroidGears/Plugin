package Utilities;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecDependency;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;

/**
 * Created by matthewyork on 4/9/14.
 */
public class GearSpecManager {

    ///////////////////////
    // Install
    ///////////////////////

    public static Boolean installGear(GearSpec spec, Project project, Module module){
        if (spec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
            return GearSpecManager.installModule(spec, project, module);
        }
        else if (spec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
            return GearSpecManager.installJar(spec, project, module);
        }
        else {
            return false;
        }
    }

    public static Boolean installModule(GearSpec spec, Project project, Module module){
        //Install dependency and sub-dependencies
        File specDirectory = new File(project.getBasePath() + Utils.pathSeparator() + "Gears" + Utils.pathSeparator() + "Modules" + Utils.pathSeparator() + spec.getName());

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

        //Download dependencies
        if (spec.getDependencies() != null){
            if (spec.getDependencies().size() > 0){
                for(GearSpecDependency dependency : spec.getDependencies()){
                    //Get spec from dependency
                    GearSpec dependencySpec = Utils.specForInfo(dependency.getName(), dependency.getVersion());

                    //If we get a valid spec from the dependency, go ahead and download the dependency
                    if (dependencySpec != null){
                        //See if it is installed already, before we try
                        if (!dependencySpec.isRegistered(project)){

                            //Install dependency
                            if (dependencySpec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
                                GearSpecManager.installJar(dependencySpec, project, module);
                            }
                            else if (dependencySpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                                GearSpecManager.installModule(dependencySpec, project, module);
                            }
                        }
                    }
                }
            }
        }

        //Update project settings
        if (!updateInstallProjectSettingsForModule(spec, project, module)){
            return false;
        }

        //Register spec
        if (GearSpecRegistrar.registerGear(spec, project)){
            return true;
        }
        else {
            return false;
        }
    }

    public static Boolean installJar(GearSpec spec, Project project, Module module){
        //Create GearsJars directory if not already there
        File libsDirectory = new File(project.getBasePath()+Utils.pathSeparator()+ "Gears"+ Utils.pathSeparator() + "Jars");
        if (!libsDirectory.exists()){
            try {
                FileUtils.forceMkdir(libsDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Build jar file
        File jarFile = new File(libsDirectory.getAbsolutePath()+Utils.pathSeparator()+Utils.jarFileNameForSpecSource(spec.getSource()));

        //Build url for gear
        String jarUrl = spec.getSource().getUrl()+"/raw/"+spec.getSource().getTag()+"/"+spec.getSource().getSource_files();
        jarUrl = jarUrl.replace(".git", "");

        //Download file
        try {
            FileUtils.copyURLToFile(new URL(jarUrl), jarFile, 2000, 30000);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //Download dependencies
        if (spec.getDependencies() != null){
            if (spec.getDependencies().size() > 0){
                for(GearSpecDependency dependency : spec.getDependencies()){
                    //Get spec from dependency
                    GearSpec dependencySpec = Utils.specForInfo(dependency.getName(), dependency.getVersion());

                    //If we get a valid spec from the dependency, go ahead and download the dependency
                    if (dependencySpec != null){
                        //See if it is installed already, before we try
                        if (!dependencySpec.isRegistered(project)){

                            //Install dependency
                            if (dependencySpec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
                                GearSpecManager.installJar(dependencySpec, project, module);
                            }
                            else if (dependencySpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                                GearSpecManager.installModule(dependencySpec, project, module);
                            }
                        }
                    }
                }
            }
        }

        //Update project settings
        if (!GearSpecManager.updateInstallProjectSettingsForJar(module)){
            return false;
        }

        //Register spec
        if (GearSpecRegistrar.registerGear(spec, project)){
            return true;
        }
        else {
            return false;
        }
    }

    private static Boolean updateInstallProjectSettingsForModule(GearSpec spec, Project project, Module module){

        //Install dependency and sub-dependencies
        File settingsFile = new File(project.getBasePath() + Utils.pathSeparator() + "settings.gradle");
        File buildFile = new File(new File(module.getModuleFilePath()).getParentFile().getAbsolutePath() + Utils.pathSeparator() + "build.gradle");

        //Create comment string
        String commentString = "\n/////////////////////\n" +
                "// Gears Dependencies\n" +
                "/////////////////////";

        //Read settings file
        try {
            String settingsFileString = FileUtils.readFileToString(settingsFile);

            if (!settingsFileString.contains("include ':Gears:Modules:"+spec.getName()+"'")){

                //Make changes to settings.gradle
                String newSettingString = "\n"+"include ':Gears:Modules:"+spec.getName()+"'";

                int commentIndex = settingsFileString.lastIndexOf(commentString);

                //If the comment exists...
                if (commentIndex != -1){
                    settingsFileString = settingsFileString.concat(newSettingString);
                }
                else {
                    settingsFileString = settingsFileString.concat(commentString+newSettingString);
                }

                //Write changes to settings.gradle
                FileUtils.forceDelete(settingsFile);
                FileUtils.write(settingsFile, settingsFileString);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //Read build file
        try {
            String buildFileString = FileUtils.readFileToString(buildFile);

            //Create new addition
            String newDependencyString = "\ndependencies{compile project (':Gears:Modules:"+spec.getName()+"')}";


            if (!buildFileString.contains(newDependencyString)){
                int commentIndex = buildFileString.lastIndexOf(commentString);

                //If the comment exists...
                if (commentIndex != -1){
                    buildFileString = buildFileString.concat(newDependencyString);
                }
                else {
                    buildFileString = buildFileString.concat(commentString+newDependencyString);
                }

                //Write changes to settings.gradle
                FileUtils.forceDelete(buildFile);
                FileUtils.write(buildFile, buildFileString);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static Boolean updateInstallProjectSettingsForJar(Module module){
        //Get build file
        File buildFile = new File(new File(module.getModuleFilePath()).getParentFile().getAbsolutePath() + Utils.pathSeparator() + "build.gradle");

        if (buildFile.exists()){
            //Create comment string
            String commentString = "\n/////////////////////\n" +
                    "// Gears Dependencies\n" +
                    "/////////////////////";

            try {
                //Read the build file
                String buildFileString = FileUtils.readFileToString(buildFile);

                //Create new addition
                String dependencyString = "\ndependencies{compile fileTree(dir: '../Gears/Jars', include: ['*.jar'])}";

                //If the build file doesn't contain the jar dependency, go ahead and add it
                if (!buildFileString.contains(dependencyString)){
                    int commentIndex = buildFileString.lastIndexOf(commentString);

                    //If the comment exists...
                    if (commentIndex != -1){
                        buildFileString = buildFileString.concat(dependencyString);
                    }
                    else {
                        buildFileString = buildFileString.concat(commentString+dependencyString);
                    }

                    //Write changes to settings.gradle
                    FileUtils.forceDelete(buildFile);
                    FileUtils.write(buildFile, buildFileString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        else {
            return false;
        }



        return true;
    }

    ///////////////////////
    // Uninstall
    ///////////////////////

    public static Boolean uninstallGear(GearSpec spec, Project project, Module module){
        if (spec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
            return GearSpecManager.uninstallModule(spec, project, module);
        }
        else if (spec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
            return GearSpecManager.uninstallJar(spec, project, module);
        }
        else {
            return false;
        }
    }

    public static Boolean uninstallModule(GearSpec spec, Project project, Module module){

        File libsDirectory = new File(project.getBasePath()+ Utils.pathSeparator()+ "Gears"+ Utils.pathSeparator() + "Modules");
        if (!libsDirectory.exists()){
            //Unregister just in case
            if (GearSpecRegistrar.unregisterGear(spec, project)){
                return true;
            }
            else {
                return false;
            }
        }

        //Get the jar file
        File moduleDirectory = new File(libsDirectory.getAbsolutePath()+Utils.pathSeparator()+spec.getName());

        //Delete the jar
        if (moduleDirectory.exists()){
            try {
                FileUtils.deleteDirectory(moduleDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        //Update settings files
        if (!updateProjectSettingsForModule(spec, project, module)){
            return false;
        }

        //Finally, unregister gear
        if (GearSpecRegistrar.unregisterGear(spec, project)){
            return true;
        }
        else {
            return false;
        }
    }

    public static Boolean uninstallJar(GearSpec spec, Project project, Module module){

        //Get the gears jar directory. If it doesn't exist, then we will count that as a win
        File libsDirectory = new File(project.getBasePath()+ Utils.pathSeparator()+ "Gears"+ Utils.pathSeparator() + "Jars");
        if (!libsDirectory.exists()){
            //Unregister just in case
            if (GearSpecRegistrar.unregisterGear(spec, project)){
                return true;
            }
            else {
                return false;
            }
        }

        //Get the jar file
        File jarFile = new File(libsDirectory.getAbsolutePath()+Utils.pathSeparator()+Utils.jarFileNameForSpecSource(spec.getSource()));

        //Delete the jar
        if (jarFile.exists()){
            try {
                FileUtils.forceDelete(jarFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        //Update settings files
        if (!updateProjectSettingsForJar(project, module)){
            return false;
        }

        //Finally, unregister gear
        if (GearSpecRegistrar.unregisterGear(spec, project)){
            return true;
        }
        else {
            return false;
        }
    }

    private static Boolean updateProjectSettingsForModule(GearSpec spec, Project project, Module module){
        //Install dependency and sub-dependencies
        File settingsFile = new File(project.getBasePath() + Utils.pathSeparator() + "settings.gradle");
        File buildFile = new File(new File(module.getModuleFilePath()).getParentFile().getAbsolutePath() + Utils.pathSeparator() + "build.gradle");
        File modulesFile = new File(project.getBasePath() + Utils.pathSeparator() + ".idea"+Utils.pathSeparator()+"modules.xml");

        //Modify settings file
        if (settingsFile.exists()){
            try {
                //Read in settings file
                String settingsFileString = FileUtils.readFileToString(settingsFile);

                //Make comparator strings
                String fullLineInclude = "include ':Gears:Modules:"+spec.getName()+"'";
                String partialInclude = "':Gears:Modules:"+spec.getName()+"'";

                //Look for full line inclusion
                if (settingsFileString.contains(fullLineInclude)){
                    settingsFileString = settingsFileString.replace(fullLineInclude, "");
                }
                //Look for partial line inclusions
                else if (settingsFileString.contains(partialInclude+",")){ //Comma after
                    settingsFileString = settingsFileString.replace(partialInclude+",", "");
                }
                else if (settingsFileString.contains(","+partialInclude)){ //Comma before
                    settingsFileString = settingsFileString.replace(","+partialInclude, "");
                }
                else if (settingsFileString.contains(", "+partialInclude)){ //Comma before w/ space
                    settingsFileString = settingsFileString.replace(", "+partialInclude, "");
                }

                //Write changes to settings.gradle
                FileUtils.forceDelete(settingsFile);
                FileUtils.write(settingsFile, settingsFileString);

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }

        //Modify build file
        if (buildFile.exists()){
            try {
                //Read the build file
                String buildFileString = FileUtils.readFileToString(buildFile);

                //Create new addition
                String dependencyString = "dependencies{compile project (':Gears:Modules:"+spec.getName()+"')}";

                if (buildFileString.contains(dependencyString)){
                    buildFileString = buildFileString.replace(dependencyString, "");
                }

                //Write changes to settings.gradle
                FileUtils.forceDelete(buildFile);
                FileUtils.write(buildFile, buildFileString);

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }

        //Remove entry from the modules.xml
        String moduleEntry = "<module fileurl=\"file://$PROJECT_DIR$/Gears/Modules/"+spec.getName()+"/"+spec.getName()+".iml\" filepath=\"$PROJECT_DIR$/Gears/Modules/"+spec.getName()+"/"+spec.getName()+".iml\" />";
        if (modulesFile.exists()){
            //Read the build file
            try {
                String modulesFileString = FileUtils.readFileToString(modulesFile);

                if (modulesFileString.contains(moduleEntry)){
                    modulesFileString = modulesFileString.replace(moduleEntry, "");
                }

                //Write changes to settings.gradle
                FileUtils.forceDelete(modulesFile);
                FileUtils.write(modulesFile, modulesFileString);

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        return true;
    }

    private static Boolean updateProjectSettingsForJar(Project project, Module module){
        File buildFile = new File(new File(module.getModuleFilePath()).getParentFile().getAbsolutePath() + Utils.pathSeparator() + "build.gradle");

        //Modify build file
        if (buildFile.exists()){
            try {
                File libsDirectory = new File(project.getBasePath()+ Utils.pathSeparator()+ "Gears"+ Utils.pathSeparator() + "Jars");

                if(libsDirectory.exists()){
                    //Check to see if all jars are gone. If so, remove the gears jar folder dependency
                    File[] jars = libsDirectory.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String fileName) {
                            if(fileName.contains(".jar")){
                                return true;
                            }
                            else {
                                return false;
                            }
                        }
                    });

                    //No jars, so remove it!
                    if (jars.length == 0){
                        //Read the build file
                        String buildFileString = FileUtils.readFileToString(buildFile);

                        //Create new addition
                        String dependencyString = "dependencies{compile fileTree(dir: '../Gears/Jars', include: ['*.jar'])}";

                        if (buildFileString.contains(dependencyString)){
                            buildFileString = buildFileString.replace(dependencyString, "");
                        }

                        //Write changes to settings.gradle
                        FileUtils.forceDelete(buildFile);
                        FileUtils.write(buildFile, buildFileString);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }

        return true;
    }

}
