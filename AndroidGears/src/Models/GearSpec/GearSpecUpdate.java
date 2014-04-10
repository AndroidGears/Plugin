package Models.GearSpec;

import java.util.ArrayList;

/**
 * Created by matthewyork on 4/9/14.
 */
public class GearSpecUpdate extends GearSpec {

    public GearSpecUpdate(GearSpec spec) {
        name = spec.getName();
        summary = spec.getSummary();
        release_notes = spec.getRelease_notes();
        version = spec.getVersion();
        type = spec.getType();
        copyright = spec.getCopyright();
        homepage = spec.getHomepage();
        license = spec.getLicense();
        authors = spec.getAuthors();
        minimum_api = spec.getMinimum_api();
        source = spec.getSource();
        dependencies = spec.getDependencies();
        tags = spec.getTags();
        gearState = spec.gearState;
    }

    private String updateVersionNumber;

    public String getUpdateVersionNumber() {
        return updateVersionNumber;
    }

    public void setUpdateVersionNumber(String updateVersionNumber) {
        this.updateVersionNumber = updateVersionNumber;
    }
}
