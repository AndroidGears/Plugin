import Singletons.SettingsManager;
import Workers.Git.IgnoreCheckWorker;
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
        //Puts an entry in the ignore file, if you have such
        IgnoreCheckWorker ignoreCheckWorker = new IgnoreCheckWorker(){

        };
        ignoreCheckWorker.execute();
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
