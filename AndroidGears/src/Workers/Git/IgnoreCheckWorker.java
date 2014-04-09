package Workers.Git;

import Singletons.SettingsManager;
import Utilities.Utils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;

/**
 * Created by matthewyork on 4/8/14.
 */
public class IgnoreCheckWorker extends SwingWorker<Void, Void>{

    @Override
    protected Void doInBackground() throws Exception {

        //Load settings from file
        SettingsManager.getInstance().loadSettings();

        //If the ignore is set, try and add it. If it already exists, the following won't do anything
        if (SettingsManager.getInstance().getAutoIgnore()){
           SettingsManager.getInstance().setCreateIgnore(true);
        }

        return null;
    }
}
