import Singletons.SettingsManager;
import Workers.Git.IgnoreCheckWorker;
import Workers.Sync.SyncGears;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
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

        //Auto-sync
        syncGears();
    }

    public void projectClosed() {
        // called when project is being closed
    }

    private void syncGears(){
        //Load settings
        SettingsManager.getInstance().loadSettings();

        //Get all projects
        ProjectManager pm = ProjectManager.getInstance();
        Project[] targetProjects = pm.getOpenProjects();
        Project p = targetProjects[0];

        //Load project settings
        SettingsManager.getInstance().loadProjectSettings(p);

        //Get all modules
        ModuleManager mm = ModuleManager.getInstance(p);
        Module[] targetModules = mm.getModules();

        //Get main module
        String mainModule = SettingsManager.getInstance().getMainModule();

        if (!mainModule.equals("")){
            //Find the module object from module name
            for (int ii = 0; ii < targetModules.length; ii++){
                //If module name matches target module, sync
                if (targetModules[ii].getName().equals(mainModule)){

                    //Sync gears
                    if (SettingsManager.getInstance().getAutoSync()){
                        SyncGears syncWorker = new SyncGears(p, targetModules[ii]){
                            @Override
                            protected void done() {
                                super.done();

                                //Possibly show toast here...
                            }
                        };
                        syncWorker.execute();
                    }

                    break;
                }
            }
        }
    }
}
