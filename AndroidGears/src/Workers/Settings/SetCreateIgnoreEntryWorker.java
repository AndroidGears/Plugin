package Workers.Settings;

import Singletons.SettingsManager;

import javax.swing.*;

/**
 * Created by matthewyork on 4/8/14.
 */
public class SetCreateIgnoreEntryWorker extends SwingWorker<Void, Void> {

    private boolean createIgnore;
    public boolean success;

    public SetCreateIgnoreEntryWorker(boolean createIgnore) {
        this.createIgnore = createIgnore;
    }

    @Override
    protected Void doInBackground() throws Exception {

        //Set the ignore function
        if (createIgnore){
            success = SettingsManager.getInstance().setCreateIgnore(true);
        }
        else {
            success = SettingsManager.getInstance().setCreateIgnore(false);
        }

        return null;
    }
}