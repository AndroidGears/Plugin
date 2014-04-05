package Workers;

import Utilities.OSValidator;
import Utilities.Utils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by matthewyork on 4/2/14.
 */
public class GitWorker extends SwingWorker<Void, Void> {
    private  String REMOTE_SPECS_URL = "https://github.com/AndroidGears/Specs.git";

    @Override
    protected Void doInBackground() throws Exception {
        syncAndroidGears();
        return null;
    }

    private void syncAndroidGears(){
        //Setup file
        File androidGearsDirectory = null;

        //Setup file
        if (OSValidator.isWindows()) {
            androidGearsDirectory = new File(System.getProperty("user.home")+"/AndroidGears"); //C drive
        } else if (OSValidator.isMac()) {
            androidGearsDirectory = new File(System.getProperty("user.home")+"/.androidgears"); //Home folder
        } else if (OSValidator.isUnix()) {
            androidGearsDirectory = new File("~/.androidgears"); //Home folder
        } else if (OSValidator.isSolaris()) {
            androidGearsDirectory = new File("~/AndroidGears");//Home folder
        } else {
            androidGearsDirectory = new File("~/AndroidGears");//Home folder
        }

        //Pull changes or clone repo
        if(androidGearsDirectory.exists()){
            pullChanges(androidGearsDirectory);
        }
        else {
            androidGearsDirectory.mkdir();
            cloneRepository(androidGearsDirectory);
        }
    }

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
            Git git = Git.open(new File(androidGearsDirectory.getAbsolutePath()+ Utils.pathSeparator()+".git"));
            git.pull().call();

            /*
            PullCommand pullCmd = git.pull();

            PullResult result = pullCmd.call();
            FetchResult fetchResult = result.getFetchResult();
            MergeResult mergeResult = result.getMergeResult();
            mergeResult.getMergeStatus();  // this should be interesting*/
        }
        catch (IOException exception){
            //cloneRepository(androidGearsDirectory);
        }
        catch (GitAPIException exception){
            exception.printStackTrace();
        }
    }
}
