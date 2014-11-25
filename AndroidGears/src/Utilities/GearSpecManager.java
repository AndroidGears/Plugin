package Utilities;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecDependency;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.runtime.directive.Foreach;
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
        return GearSpecManager.installGear(spec, project, module, null);
    }

    public static Boolean installGear(GearSpec spec, Project project, Module module, GearSpec parentSpec){
        if (spec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
            return GearSpecManager.installModule(spec, project, module, parentSpec);
        }
        else if (spec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
            return GearSpecManager.installJar(spec, project, module, parentSpec);
        }
        else {
            return false;
        }
    }

    public static Boolean installModule(GearSpec spec, Project project, Module module, GearSpec parentSpec){
        //Create local path separator for speed
        String pathSeparator = Utils.pathSeparator();

        //Install dependency and sub-dependencies
        File specDirectory = Utils.fileInstallPathForSpec(spec, project);
        File specTopLevelDirectory = specDirectory.getParentFile();

        //Delete the directory. This is for other versions installed
        if (specTopLevelDirectory.exists()) {
            try {
                FileUtils.forceDelete(specTopLevelDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        //Clone down repo
        try {
            Git git = Git.cloneRepository()
                    .setURI(spec.getSource().getUrl())
                    .setBranch(spec.getSource().getTag())
                    .setDirectory(specDirectory)
                    .call();
            git.close();
        } catch (GitAPIException e) {
            e.printStackTrace();

            //Clean up a possible bad clone (i.e. no internet)
            if (specDirectory.getParentFile().exists()){
                try {
                    FileUtils.forceDelete(specDirectory.getParentFile());
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    return false;
                }
            }

            return false;
        }

        //Check out appropriate branch
        /*
        File gitDirectory = new File(specDirectory.getAbsolutePath() + pathSeparator + ".git");
        try {
            Git git = Git.open(gitDirectory);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }*/

        //Move specified folder to root, if paremeter exists
        if (spec.getSource().getSource_files() != null){
            if (!spec.getSource().getSource_files().equals("")){
                //Replaces path seperators with system dependent ones (windows, mac, etc.)
                String systemSpecificInnerPath = spec.getSource().getSource_files().replace("/", pathSeparator);

                //Get module directory
                File moduleDirectory = new File(specDirectory.getAbsolutePath()+ pathSeparator +systemSpecificInnerPath);

                //Delete other folders, including source control
                for(File file : specDirectory.listFiles()){
                    //Delete all folders that aren't the lib folder
                    if (!file.getAbsolutePath().equals(moduleDirectory.getAbsolutePath())){
                        try {
                            if (file.isDirectory()){
                                FileUtils.deleteQuietly(file);
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
                                installJar(dependencySpec, project, module, spec);
                            }
                            else if (dependencySpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                                installModule(dependencySpec, project, module, spec);
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

        //Update internal dependencies
        if (!installInternalDependencies(spec, parentSpec, project)){
            return false;
        }

        //Register spec
        if (GearSpecRegistrar.registerGear(spec, project, GearSpec.GearState.GearStateInstalled)){
            return true;
        }
        else {
            return false;
        }
    }

    public static Boolean installJar(GearSpec spec, Project project, Module module, GearSpec parentSpec){
        //Create local path separator for speed
        String pathSeparator = Utils.pathSeparator();

        //Create GearsJars directory if not already there
        //Install dependency and sub-dependencies
        File specDirectory = Utils.fileInstallPathForSpec(spec, project);
        File specTopLevelDirectory = specDirectory.getParentFile();

        //Delete the directory. This is for other versions installed
        if (specTopLevelDirectory.exists()) {
            try {
                FileUtils.forceDelete(specTopLevelDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        //Build jar file
        File jarFile = new File(specDirectory.getAbsolutePath() + pathSeparator + Utils.jarFileNameForSpecSource(spec.getSource()));

        //Build url for gear
        String jarUrl;

        if(spec.getSource().getUrl().toLowerCase().contains(".jar")){
            jarUrl = spec.getSource().getUrl();
        }
        else {
            jarUrl = spec.getSource().getUrl()+"/raw/"+spec.getSource().getTag()+"/"+spec.getSource().getSource_files();
            jarUrl = jarUrl.replace(".git", "");
        }

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
                                installJar(dependencySpec, project, module, spec);
                            }
                            else if (dependencySpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                                installModule(dependencySpec, project, module, spec);
                            }
                        }
                    }
                }
            }
        }

        //Update project settings
        if (!updateInstallProjectSettingsForJar(spec, module)){
            return false;
        }

        //Update internal dependencies
        if (!installInternalDependencies(spec, parentSpec, project)){
            return false;
        }

        //Register spec
        if (GearSpecRegistrar.registerGear(spec, project, GearSpec.GearState.GearStateInstalled)){
            return true;
        }
        else {
            return false;
        }
    }

    private static Boolean updateInstallProjectSettingsForModule(GearSpec spec, Project project, Module module){
        //Create local path separator for speed
        String pathSeparator = Utils.pathSeparator();
        
        //Install dependency and sub-dependencies
        File settingsFile = new File(project.getBasePath() + pathSeparator + "settings.gradle");
        File buildFile = new File(new File(module.getModuleFilePath()).getParentFile().getAbsolutePath() + pathSeparator + "build.gradle");
        File gearBuildFile = new File(Utils.fileInstallPathForSpec(spec, project).getParentFile().getAbsolutePath() + pathSeparator + spec.getVersion() + pathSeparator + "build.gradle");

        //Create comment string
        String commentString = "\n/////////////////////\n" +
                "// Gears Dependencies\n" +
                "/////////////////////";

        //Read settings file
        try {
            String settingsFileString = FileUtils.readFileToString(settingsFile);

            if (!settingsFileString.contains("include ':Gears:Modules:"+spec.getName()+":"+spec.getVersion()+"'")){

                //Make changes to settings.gradle
                String newSettingString = "\n"+"include ':Gears:Modules:"+spec.getName()+":"+spec.getVersion()+"'";

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
            String newDependencyString = "\ndependencies{compile project (':Gears:Modules:"+spec.getName()+":"+spec.getVersion()+"')}";

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

            //Get compileSDK version and buildTools version
            String[] appLines = buildFileString.split("\n");
            String compileSDKVersion = null;
            String buildToolsVersion = null;
            for(String line : appLines){
                if (line.contains("compileSdkVersion")){
                    compileSDKVersion = line.replace("compileSdkVersion", "").replace(" ", "");
                }
                else if (line.contains("buildToolsVersion")){
                    buildToolsVersion = line.replace("buildToolsVersion", "").replace(" ", "");
                }
            }

            //Apply collected compile version and build tools version
            if (compileSDKVersion != null || buildToolsVersion != null) {
                String gearBuildFileString = FileUtils.readFileToString(gearBuildFile);

                //Apply compiled sdk and build tools sdk to new module
                String[] moduleLines = gearBuildFileString.split("\n");
                String modifiedModuleBuildFileString = "";
                for(String line : moduleLines) {
                    if (line.contains("'android-library'")){
                        modifiedModuleBuildFileString = modifiedModuleBuildFileString + "apply plugin: 'com.android.library'" + "\n";
                    }
                    else if (line.contains("compileSdkVersion")){
                        modifiedModuleBuildFileString = modifiedModuleBuildFileString+ "compileSdkVersion "+compileSDKVersion+"\n";
                    }
                    else if (line.contains("buildToolsVersion")){
                        modifiedModuleBuildFileString = modifiedModuleBuildFileString + "buildToolsVersion "+buildToolsVersion+"\n";
                    }
                    else {
                        modifiedModuleBuildFileString = modifiedModuleBuildFileString + line+"\n";
                    }
                }

                //Write changes to build.gradle
                FileUtils.forceDelete(gearBuildFile);
                FileUtils.write(gearBuildFile, modifiedModuleBuildFileString);

            }


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }




        return true;
    }

    private static Boolean updateInstallProjectSettingsForJar(GearSpec selectedSpec, Module module){
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
                String dependencyString = "\ndependencies{compile fileTree(dir: '../Gears/Jars/"+selectedSpec.getName()+"/"+selectedSpec.getVersion()+"', include: ['*.jar'])}";

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

    /**
     * Adds internal dependency listings to modules that require it. JARs are unaffected.
     * @param spec
     * @param parentSpec
     * @return
     */
    private static Boolean installInternalDependencies(GearSpec spec, GearSpec parentSpec, Project project){
        if (parentSpec != null){
            if (parentSpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){

                //Get parent gradle build file. Remember this is already cloned down, so we should be good!
                File parentModuleBuildFile = new File(Utils.fileInstallPathForSpec(parentSpec, project).getAbsolutePath()+Utils.pathSeparator()+"build.gradle");

                //Check to see if it exists
                if (parentModuleBuildFile.exists()){
                    //Create comment string
                    String commentString = "\n/////////////////////\n" +
                            "// Gears Dependencies\n" +
                            "/////////////////////";

                    //Read build file
                    try {
                        String buildFileString = FileUtils.readFileToString(parentModuleBuildFile);

                        //Create new addition
                        String newDependencyString = "";

                        //Build new dependency string based on type
                        if (spec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
                            newDependencyString = "\ndependencies{compile project (':Gears:Modules:"+spec.getName()+":"+spec.getVersion()+"')}";
                        }
                        else if (spec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
                            newDependencyString = "\ndependencies{compile fileTree(dir: '../../../../Gears/Jars/"+spec.getName()+"/"+spec.getVersion()+"', include: ['*.jar'])}";
                        }

                        //Check for and insert dependency string
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
                            FileUtils.forceDelete(parentModuleBuildFile);
                            FileUtils.write(parentModuleBuildFile, buildFileString);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
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

        //Update settings files
        //This MUST be run before removing the physical module on disk. Otherwise, the directory structure for the module may be retained after syncing
        if (!updateProjectSettingsForModule(spec, project, module)){
            return false;
        }

        //Make local path separator for speed
        String pathSeparator = Utils.pathSeparator();

        File libsDirectory = new File(project.getBasePath()+ pathSeparator+ "Gears"+ pathSeparator + "Modules"+pathSeparator+spec.getName());
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
        File moduleDirectory = new File(libsDirectory.getAbsolutePath()+pathSeparator+spec.getVersion());

        //Delete the jar
        if (moduleDirectory.exists()){
            try {
                FileUtils.forceDelete(libsDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
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

        //Make local path separator for speed
        String pathSeparator = Utils.pathSeparator();

        //Get the gears jar directory. If it doesn't exist, then we will count that as a win
        File libsDirectory = new File(project.getBasePath()+ pathSeparator + "Gears"+ pathSeparator + "Jars"+ pathSeparator + spec.getName());
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
        File jarFile = new File(libsDirectory.getAbsolutePath()+pathSeparator+spec.getVersion()+pathSeparator+Utils.jarFileNameForSpecSource(spec.getSource()));

        //Delete the jar
        if (jarFile.exists()){
            try {
                FileUtils.forceDelete(libsDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        //Update settings files
        if (!updateProjectSettingsForJar(spec, project, module)){
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
        //Make local path separator for speed
        String pathSeparator = Utils.pathSeparator();

        //Install dependency and sub-dependencies
        File modulesFile = new File(project.getBasePath() + pathSeparator + ".idea"+ pathSeparator +"modules.xml");
        File settingsFile = new File(project.getBasePath() + pathSeparator + "settings.gradle");
        File buildFile = new File(new File(module.getModuleFilePath()).getParentFile().getAbsolutePath() +pathSeparator + "build.gradle");

        //Recreate version entry from the modules.xml
        String versionModuleEntry = "<module fileurl=\"file://$PROJECT_DIR$/Gears/Modules/"+spec.getName()+"/"+spec.getVersion()+"/"+spec.getVersion()+".iml\" filepath=\"$PROJECT_DIR$/Gears/Modules/"+spec.getName()+"/"+spec.getVersion()+"/"+spec.getVersion()+".iml\" />";
        //Recreate gear entry from modles.xml
        String parentModuleEntry = "<module fileurl=\"file://$PROJECT_DIR$/Gears/Modules/"+spec.getName()+"/"+spec.getName()+".iml\" filepath=\"$PROJECT_DIR$/Gears/Modules/"+spec.getName()+"/"+spec.getName()+".iml\" />";

        if (modulesFile.exists()){
            //Read the build file
            try {
                String modulesFileString = FileUtils.readFileToString(modulesFile);

                if (modulesFileString.contains(versionModuleEntry)){
                    modulesFileString = modulesFileString.replace(versionModuleEntry, "");
                }
                if (modulesFileString.contains(parentModuleEntry)){
                    modulesFileString = modulesFileString.replace(parentModuleEntry, "");
                }

                //Write changes to settings.gradle
                FileUtils.forceDelete(modulesFile);
                FileUtils.write(modulesFile, modulesFileString);

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        //Modify settings file
        if (settingsFile.exists()){
            try {
                //Read in settings file
                String settingsFileString = FileUtils.readFileToString(settingsFile);

                //Make comparator strings
                String fullLineInclude = "include ':Gears:Modules:"+spec.getName()+":"+spec.getVersion()+"'";
                String partialInclude = "':Gears:Modules:"+spec.getName()+":"+spec.getVersion()+"'";

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
                String dependencyString = "dependencies{compile project (':Gears:Modules:"+spec.getName()+":"+spec.getVersion()+"')}";

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

        return true;
    }

    private static Boolean updateProjectSettingsForJar(GearSpec spec, Project project, Module module){
        //Make local path separator for speed
        String pathSeparator = Utils.pathSeparator();

        File buildFile = new File(new File(module.getModuleFilePath()).getParentFile().getAbsolutePath() + pathSeparator + "build.gradle");

        //Modify build file
        if (buildFile.exists()){
            try {
                File libsDirectory = new File(project.getBasePath() + pathSeparator + "Gears"+ pathSeparator + "Jars");

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
                        String dependencyString = "dependencies{compile fileTree(dir: '../Gears/Jars/"+spec.getName()+"/"+spec.getVersion()+"', include: ['*.jar'])}";

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

    /**
     * Removes internal dependency listings to modules that require them. JARs are unaffected.
     * @param spec
     * @param parentSpec
     * @param project
     * @return
     */
    private static Boolean uninstallInternalDependencies(GearSpec spec, GearSpec parentSpec, Project project){

        if (parentSpec != null){
            if (parentSpec.getType().equals(GearSpec.SPEC_TYPE_MODULE)) {
                
            }
        }

        return true;
    }
}
