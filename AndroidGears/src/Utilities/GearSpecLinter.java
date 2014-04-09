package Utilities;

import Models.GearSpec.GearSpec;
import Models.GearSpec.GearSpecAuthor;
import Models.GearSpec.GearSpecDependency;
import Models.GearSpecLinter.GearSpecLintResult;
import Workers.Lint.LintGearSpecWorker;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/3/14.
 */
public class GearSpecLinter {
    public static GearSpecLintResult lintSpec(GearSpec spec, LintGearSpecWorker worker){

        ArrayList<String> failureReasons = new ArrayList<String>();
        ArrayList<String> successReasons = new ArrayList<String>();

        //Check for spec existence
        if(spec == null){
            failureReasons.add("Spec file could not be linted either because it was not found or because of invalid syntax.\n    Consider reviewing/linting your JSON syntax. http://jsonlint.com/\n    For best results, use the \"Create Gear Spec\" utility in the Android Gears menu.");
        }
        else {
            successReasons.add("Spec correctly parsed from JSON.");

            //Check for name
            if (!existsAndNotBlank(spec.getName())){
                failureReasons.add("Spec must have a name.");
            }
            else {
                successReasons.add("Spec has valid name.");
            }
            //Check for summary
            if (!existsAndNotBlank(spec.getSummary())){
                failureReasons.add("Spec must have a summary.");
            }
            else {
                successReasons.add("Spec has valid summary.");
            }
            //Check for release notes
            if (!existsAndNotBlank(spec.getRelease_notes())){
                failureReasons.add("Spec must have release notes.");
            }
            else {
                successReasons.add("Spec has valid release notes.");
            }
            //Check for version
            if (!existsAndNotBlank(spec.getVersion())){
                failureReasons.add("Spec must have a version.");
            }
            else {
                successReasons.add("Spec has valid version number.");
            }
            //Check for type
            if (!existsAndNotBlank(spec.getType())){
                failureReasons.add("Spec must have a type. (module or jar)");
            }
            else if (!spec.getType().equals("module") && !spec.getType().equals("jar")){
                failureReasons.add("Spec must be of type \"module\" or \"jar\". The type is case sensitive.");
            }
            else {
                successReasons.add("Spec has valid type.");
            }
            //Check for copyright
            if (!existsAndNotBlank(spec.getCopyright())){
                failureReasons.add("Spec must have a copyright.");
            }
            else {
                successReasons.add("Spec has valid copyright.");
            }
            //Check for homepage
            if (!existsAndNotBlank(spec.getHomepage())){
                failureReasons.add("Spec must have a homepage. If the project is hosted on github, use the projects github page (http://github.com/username/projectname)");
            }
            else {
                successReasons.add("Spec has valid homepage.");
            }
            //Check authors
            checkAuthors(spec, failureReasons, successReasons, worker);

            //Check for minimum api level
            if (spec.getMinimum_api() > 0){
                successReasons.add("Spec has valid api level.");
            }
            else {
                failureReasons.add("Spec must have a minimum api level above 0.");
            }

            //Check source
            checkSources(spec, failureReasons, successReasons, worker);

            //Check dependencies
            checkDependencies(spec, failureReasons, successReasons, worker);
        }

        //Return lint result
        if (failureReasons.size() > 0){
            return lintFailedForReason(failureReasons);
        }
        else {
            return lintPassed(successReasons);
        }
    }

    //////////////////////
    // Lint Helpers
    //////////////////////

    private static Boolean existsAndNotBlank(String string){
        if (string == null){
            return false;
        }
        else if (string.equals("")){
            return false;
        }

        return true;
    }

    private static void checkAuthors(GearSpec spec, ArrayList<String> failureReasons, ArrayList<String> successReasons, LintGearSpecWorker worker){
        if (spec.getAuthors() == null){
            failureReasons.add("Spec must have at least one author.");
        }
        else if (spec.getAuthors().size() > 0){
            for (GearSpecAuthor author : spec.getAuthors()){
                if (!existsAndNotBlank(author.getName()) || !existsAndNotBlank(author.getEmail())){
                    failureReasons.add("Spec authors must have a name and email.");
                }
                else {
                    successReasons.add("Spec has valid author: "+author.getName());
                }
            }
        }
        else {
            failureReasons.add("Spec must have at least one author.");
        }
    }

    private static void checkSources(GearSpec spec, ArrayList<String> failureReasons, ArrayList<String> successReasons, LintGearSpecWorker worker){
        if (spec.getSource() == null){
            failureReasons.add("Spec must have a source.");
        }
        else {
            if (!existsAndNotBlank(spec.getSource().getUrl())){
                failureReasons.add("Spec source must have a url.");
            }
            //Do specific checks based on type
            if (spec.getType() != null){
                //If a module
                if (spec.getType().equals("module")){
                    if (!existsAndNotBlank(spec.getSource().getTag())) {
                        failureReasons.add("Spec of type module must provide a tag in the source.");
                        return;
                    }
                    if (!spec.getSource().getUrl().contains(".git")){
                        failureReasons.add("Spec of type module must have a url leading to a .git repository.");
                        return;
                    }

                    successReasons.add("Spec has valid api level.");
                }
                //If jar
                else if (spec.getType().equals("jar")){
                    if (!spec.getSource().getUrl().contains(".jar")){
                        failureReasons.add("Spec of type jar must have a url leading to a .jar file.");
                        return;
                    }

                    successReasons.add("Spec has valid api level.");
                }
            }
        }
    }

    private static void checkDependencies(GearSpec spec, ArrayList<String> failureReasons, ArrayList<String> successReasons, LintGearSpecWorker worker){
        if (spec.getDependencies() != null){
            if (spec.getDependencies().size() > 0){
                //Iterate over all dependencies, checking for valid dependencies
                for (GearSpecDependency dependency : spec.getDependencies()){
                    if (!existsAndNotBlank(dependency.getName())){
                        failureReasons.add("Spec dependencies must have a name.");
                    }
                    else if(!versionExistsForDependency(dependency)){
                        failureReasons.add("Could not find dependency "+dependency.getName()+" for version "+dependency.getVersion()+"Consider syncing Android Gears to pull any new changes from the specs repository.");
                    }
                    else {
                        successReasons.add("Spec has valid dependency: "+dependency.getName());
                    }
                }
            }
        }
    }

    private static Boolean versionExistsForDependency(GearSpecDependency dependency){

        File projectDirectory = new File(Utils.androidGearsDirectory()+Utils.pathSeparator()+dependency.getName());
        if(projectDirectory.isDirectory() && projectDirectory.exists()){
            //Get project versions
            String[] projectVersions = versionsForProject(dependency.getName());

            //If no version is specified in the dependency, and a version exists in the system, return true
            if (dependency.getVersion() == null && projectVersions.length > 0){
                return true;
            }
            else if (dependency.getVersion() == null && projectVersions.length == 0){ //Problem getting versions...
                return false;
            }
            else {
                //Look for specified version. If you find it, return true!
                for (String version : projectVersions){
                    if (version.equals(dependency.getVersion())){
                        return true;
                    }
                }
            }

        }

        return false;
    }

    private static String[] versionsForProject(String project){
        File versionsDirectory = new File(Utils.androidGearsDirectory().getAbsolutePath()+Utils.pathSeparator()+project);
        return versionsDirectory.list();
    }

    //////////////////////
    // Lint Results
    //////////////////////

    private static GearSpecLintResult lintFailedForReason(ArrayList<String> reasons){

        GearSpecLintResult result = new GearSpecLintResult();
        result.setPassed(false);
        result.setResponseMessage("Gear Spec lint failed:\n");

        //Show all error reasons
        for (String reason : reasons){
            result.setResponseMessage(result.getResponseMessage()+"\n - "+reason);
        }

        return  result;
    }

    private static GearSpecLintResult lintPassed(ArrayList<String> reasons){

        GearSpecLintResult result = new GearSpecLintResult();
        result.setPassed(true);
        result.setResponseMessage("Gear Spec lint passed:\n");

        //Show all error reasons
        for (String reason : reasons){
            result.setResponseMessage(result.getResponseMessage()+"\n - "+reason);
        }

        return  result;
    }

}
