package Models.GearSpec;

/**
 * Created by matthewyork on 3/31/14.
 */
public class GearSpecSource {
    private String url;
    private String tag;
    private String source_files;

    public GearSpecSource(String source_files, String url, String tag) {
        this.source_files = source_files;
        this.url = url;
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSource_files() {
        return source_files;
    }

    public void setSource_files(String source_files) {
        this.source_files = source_files;
    }
}
