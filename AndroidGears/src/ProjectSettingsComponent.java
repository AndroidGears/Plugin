import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by matthewyork on 4/8/14.
 */
public class ProjectSettingsComponent implements ProjectComponent {
    public ProjectSettingsComponent(Project project) {
    }

    public void initComponent() {


    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "ProjectSettingsComponent";
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
