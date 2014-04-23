package Models.GearSpec;

import Models.GearSpecRegister.GearSpecRegister;
import Utilities.GearSpecRegistrar;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;

/**
 * Created by matthewyork on 3/31/14.
 */
public class GearSpec {
    public enum GearState {
        GearStateUninstalled,
        GearStateDeclared,
        GearStateInstalled
    };

    public static final String SPEC_TYPE_MODULE = "module";
    public static final String SPEC_TYPE_JAR = "jar";

    private GearState gearState;
    private String name;
    private String summary;
    private String release_notes;
    private String version;
    private String type;
    private String copyright;
    private String homepage;
    private String license;
    private ArrayList<GearSpecAuthor> authors;
    private int minimum_api;
    private GearSpecSource source;
    private ArrayList<GearSpecDependency> dependencies;
    private ArrayList<String> tags;

    public GearState getGearState() {
        return gearState;
    }

    public void setGearState(GearState gearState) {
        this.gearState = gearState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRelease_notes() {
        return release_notes;
    }

    public void setRelease_notes(String release_notes) {
        this.release_notes = release_notes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public ArrayList<GearSpecAuthor> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<GearSpecAuthor> authors) {
        this.authors = authors;
    }

    public int getMinimum_api() {
        return minimum_api;
    }

    public void setMinimum_api(int minimum_api) {
        this.minimum_api = minimum_api;
    }

    public GearSpecSource getSource() {
        return source;
    }

    public void setSource(GearSpecSource source) {
        this.source = source;
    }

    public ArrayList<GearSpecDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(ArrayList<GearSpecDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    ///////////////////////
    // Helper Methods
    ///////////////////////

    public Boolean isRegistered(Project project){
        //Get register
        GearSpecRegister register = GearSpecRegistrar.getRegister(project);

        //Iterate over register
        if (register != null){
            if (register.declaredGears != null){
                for(GearSpec spec : register.declaredGears){
                    if (this.getName().equals(spec.getName()) && this.getVersion().equals(spec.getVersion())){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public ArrayList<GearSpec> dependentGears(Project project){
        //Get register
        GearSpecRegister register = GearSpecRegistrar.getRegister(project);

        //Setup dependents array
        ArrayList<GearSpec> dependents = new ArrayList<GearSpec>();

        //Iterate over register dependencies for potential dependents
        if (register != null) {
            if (register.declaredGears != null) {
                for (GearSpec spec : register.declaredGears) {
                    if (this.getName().equals(spec.getName()) && this.getVersion().equals(spec.getVersion())){
                        continue;
                    }
                    else { //For all not the calling spec
                        if (spec.getDependencies() != null){
                            for (GearSpecDependency dependency : spec.getDependencies()){
                                //If a dependency matches the calling spec, add it as a dependent gear
                                if (dependency.getName().equals(this.getName()) && dependency.getVersion().equals(this.getVersion())){
                                    dependents.add(spec);
                                    ArrayList<GearSpec> dependentSpecs = spec.dependentGears(project);
                                    dependents.addAll(dependentSpecs);
                                }
                            }
                        }
                    }
                }
            }
        }

        return dependents;
    }
}
