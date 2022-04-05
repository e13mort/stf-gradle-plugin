package com.github.e13mort.stfgradleplugin;

import com.github.e13mort.stfgradleplugin.storage.BuildDirDevicesRepository;
import com.github.e13mort.stfgradleplugin.storage.ConnectedDevicesRepository;
import com.github.e13mort.stfgradleplugin.tasks.StfConnectionTask;
import com.github.e13mort.stfgradleplugin.tasks.StfDisconnectionTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.provider.Provider;

public class Plugin implements org.gradle.api.Plugin<Project> {

    private static final String FARM_SETTINGS = "farmSettings";
    private static final String TASK_NAME_CONNECT = "connectToSmartphoneTestFarm";
    private static final String TASK_NAME_DISCONNECT = "disconnectFromSmartphoneTestFarm";
    private static final String STF_FOLDER_NAME = "stf";
    private static final String STF_FILE_NAME = "connectedDevices.json";
    private static final TestTaskNameValidator VALIDATOR = new TestTaskNameValidator();
    private ConnectedDevicesRepository connectedDevicesRepository = ConnectedDevicesRepository.EMPTY;

    @Override
    public void apply(Project project) {
        final StfConnectionTask connectionTask = project.getTasks().create(TASK_NAME_CONNECT, StfConnectionTask.class);
        final StfDisconnectionTask disconnectionTask = project.getTasks().create(TASK_NAME_DISCONNECT, StfDisconnectionTask.class);
        prepareDevicesRepository(project);
        disconnectionTask.take(connectedDevicesRepository);
        connectionTask.setDevicesRepository(connectedDevicesRepository);
        PluginSettings settings = project.getExtensions().create(FARM_SETTINGS, PluginSettings.class);
        addTask(project, connectionTask, disconnectionTask, settings);
    }

    private void prepareDevicesRepository(Project project) {
        connectedDevicesRepository = new BuildDirDevicesRepository(
                getCacheFile(project),
                project.getLogger()
        );
    }

    private RegularFile getCacheFile(Project project) {
        DirectoryProperty buildDirectory = project.getLayout().getBuildDirectory();
        Provider<Directory> farmDirectory = buildDirectory.dir(STF_FOLDER_NAME);
        Directory directory = farmDirectory.get();
        directory.getAsFile().mkdirs();
        return directory.file(STF_FILE_NAME);
    }

    private void addTask(final Project project, final Task connectionTask, final Task disconnectionTask, final PluginSettings settings) {
        if (settings.isAttachToTestTaskManually()) return;
        project.afterEvaluate(project1 -> project1.getTasks().forEach(task -> {
            if (VALIDATOR.isAndroidTestTaskName(task.getName())) {
                task.dependsOn(connectionTask);
                task.finalizedBy(disconnectionTask);
                logTaskAttached(task, project1);
            }
        }));
    }

    private void logTaskAttached(Task task, Project project) {
        project.getLogger().log(LogLevel.INFO, "STF connection task attached to the " + task.getName() + " task");
    }

}
