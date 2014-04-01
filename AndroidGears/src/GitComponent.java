import com.intellij.openapi.components.ApplicationComponent;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Created by matthewyork on 3/31/14.
 */
public class GitComponent implements ApplicationComponent {

    private String REMOTE_SPECS_URL = "https://github.com/AndroidGears/Specs.git";

    public GitComponent() {
    }

    public void initComponent() {

        //Setup file
        File androidGearsDirectory = null;

        //Setup file
        if (OSValidator.isWindows()) {
            androidGearsDirectory = new File("C:\\AndroidGears"); //C drive
        } else if (OSValidator.isMac()) {
            androidGearsDirectory = new File("~"); //Home folder
        } else if (OSValidator.isUnix()) {
            androidGearsDirectory = new File("~/AndroidGears"); //Home folder
        } else if (OSValidator.isSolaris()) {
            androidGearsDirectory = new File("~/AndroidGears");//Home folder
        } else {
            androidGearsDirectory = new File("~/AndroidGears");//Home folder
        }

        //Create path with AndroidGears folder in home directory
        String slash = (OSValidator.isWindows()) ? "\\" : "/";
        androidGearsDirectory = new File(System.getProperty("user.home")+slash+"AndroidGears");

        //Pull changes or clone repo
        if(androidGearsDirectory.exists()){
            pullChanges(androidGearsDirectory);
        }
        else {
            androidGearsDirectory.mkdir();
            cloneRepository(androidGearsDirectory);
        }
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "GitComponent";
    }


    //Git methods

    private void cloneRepository(File androidGearsDirectory){
        try {
            Git.cloneRepository()
                    .setURI(REMOTE_SPECS_URL)
                    .setBranch("master")
                    .setDirectory(androidGearsDirectory)
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();

        }
    }

    private void pullChanges(File androidGearsDirectory){
        try {
            Git git = Git.open(new File(androidGearsDirectory.getAbsolutePath()+".git"));
            git.pull();
        }
        catch (IOException exception){
            cloneRepository(androidGearsDirectory);
        }
    }
}
