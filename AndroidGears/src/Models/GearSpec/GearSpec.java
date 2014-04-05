package Models.GearSpec;

import Utilities.Utils;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by matthewyork on 3/31/14.
 */
public class GearSpec {
    public static final String SPEC_TYPE_MODULE = "module";
    public static final String SPEC_TYPE_JAR = "jar";

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

    public Boolean isInstalled(Project project){
        if (this.getType().equals(SPEC_TYPE_JAR)){
            File libsDirectory = new File(project.getBasePath()+Utils.pathSeparator()+ "GearsJars"+Utils.pathSeparator()+Utils.jarFileNameForSpecSource(this.getSource()));
            if (libsDirectory.exists()){
                return true;
            }
        }
        else if(this.getType().equals(SPEC_TYPE_MODULE)){
            File specDirectory = new File(project.getBasePath()+ Utils.pathSeparator()+this.getName());

            if (specDirectory.exists()){
                return true;
            }
        }


        return false;
    }
}
