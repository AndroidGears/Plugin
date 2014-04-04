package Models.GearSpecLinter;

/**
 * Created by matthewyork on 4/3/14.
 */
public class GearSpecLintResult {
    private Boolean passed;
    private String responseMessage;

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
