import Utilities.OSValidator;
import Workers.GitWorker;
import com.intellij.openapi.components.ApplicationComponent;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Created by matthewyork on 3/31/14.
 */
public class GitComponent implements ApplicationComponent {

    private  String REMOTE_SPECS_URL = "https://github.com/AndroidGears/Specs.git";

    public GitComponent() {
    }

    public void initComponent() {

        GitWorker worker = new GitWorker(){
            @Override
            protected void done() {
                super.done();

            }
        };
        worker.run();
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "GitComponent";
    }



}
