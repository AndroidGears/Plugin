package Workers.Git;

import Singletons.SettingsManager;
import Utilities.OSValidator;
import Utilities.Utils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by matthewyork on 4/2/14.
 */
public class GitWorker extends SwingWorker<Void, Void> {
    private static final int DEFAULT_TIMEOUT = 5000; //10s
    private  String REMOTE_SPECS_URL = "https://github.com/AndroidGears/Specs.git";

    public boolean successful = false;

    @Override
    protected Void doInBackground() throws Exception {
        SettingsManager.getInstance().loadSettings();
        syncAndroidGears();
        return null;
    }

    private void syncAndroidGears(){
        //Setup file
        File androidGearsDirectory = Utils.androidGearsDirectory();

        //Pull changes or clone repo
        if(androidGearsDirectory.exists()){
            successful = pullChanges(androidGearsDirectory);
        }
        else {
            androidGearsDirectory.mkdir();
            successful = cloneRepository(androidGearsDirectory);
        }
    }

    private Boolean cloneRepository(File androidGearsDirectory){
        try {
           Git git = Git.cloneRepository()
                    .setURI(REMOTE_SPECS_URL)
                    .setBranch("master")
                    .setDirectory(androidGearsDirectory)
                   .setTimeout(DEFAULT_TIMEOUT)
                    .call();

            //Get repos directory
           File reposDirectory = git.getRepository().getDirectory().getParentFile();

            //Close git connection!
            git.close();

            //If everything was created successfully, return true
            if (reposDirectory != null){
                if (reposDirectory.exists()){
                    if (reposDirectory.list().length > 1){
                        return true;
                    }
                }
            }
        } catch (GitAPIException e) {
            e.printStackTrace();

        }

        return false;
    }

    private Boolean pullChanges(File androidGearsDirectory){
        try {
            Git git = Git.open(new File(androidGearsDirectory.getAbsolutePath()+ Utils.pathSeparator()+".git"));
            PullResult result = git.pull().setTimeout(DEFAULT_TIMEOUT).call();
            git.close();
            return result.isSuccessful();

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

        return false;
    }
}
