package Models.GearSpec;

import java.util.ArrayList;

/**
 * Created by matthewyork on 4/9/14.
 * Updated by adfleshner  on 4/22/14.
 */
public class GearSpecUpdate extends GearSpec {

    public GearSpecUpdate(GearSpec spec) {
        setName(spec.getName());
        setSummary(spec.getSummary());
        setRelease_notes(spec.getRelease_notes());
        setVersion(spec.getVersion());
        setType(spec.getType());
        setCopyright(spec.getCopyright());
        setHomepage(spec.getHomepage());
        setLicense(spec.getLicense());
        setAuthors(spec.getAuthors());
        setMinimum_api(spec.getMinimum_api());
        setSource(spec.getSource());
        setDependencies(spec.getDependencies());
        setTags(spec.getTags());
        setGearState(spec.getGearState());
    }

    private String updateVersionNumber;

    public String getUpdateVersionNumber() {
        return updateVersionNumber;
    }

    public void setUpdateVersionNumber(String updateVersionNumber) {
        this.updateVersionNumber = updateVersionNumber;
    }
}
