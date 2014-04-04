package Models.GearSpec;

/**
 * Created by matthewyork on 3/31/14.
 */
public class GearSpecAuthor {
    private String name;
    private String email;

    public GearSpecAuthor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
