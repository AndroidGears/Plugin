package Models.GearSpec;

import java.util.ArrayList;

/**
 * Created by matthewyork on 3/31/14.
 */
public class GearSpec {
    private String name;
    private String summary;
    private String version;
    private String type;
    private String homepage;
    private String icon;
    private String license;
    private ArrayList<GearSpecAuthor> authors;
    private int minimum_api;
    private GearSpecSource source;
    private ArrayList<GearSpecDependency> dependencies;

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

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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
}
