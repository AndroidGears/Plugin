import Workers.Git.GitWorker;
import Workers.Git.IgnoreCheckWorker;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by matthewyork on 3/31/14.
 */
public class GitComponent implements ApplicationComponent {

    private  String REMOTE_SPECS_URL = "https://github.com/AndroidGears/Specs.git";

    public GitComponent() {
    }

    public void initComponent() {

        //Clones/Pulls on the specNames repo
        GitWorker worker = new GitWorker(){
            @Override
            protected void done() {
                super.done();

            }
        };
        worker.execute();
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "GitComponent";
    }



}
