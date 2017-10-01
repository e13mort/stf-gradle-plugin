package com.github.e13mort.stfgradleplugin;

import com.github.e13mort.stfgradleplugin.tasks.StfConnectionTask;
import com.github.e13mort.stfgradleplugin.tasks.StfDisconnectionTask;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.LogLevel;

public class Plugin implements org.gradle.api.Plugin<Project> {

    private static final String FARM_SETTINGS = "farmSettings";
    private static final String TASK_NAME_CONNECT = "connectToSmartphoneTestFarm";
    private static final String TASK_NAME_DISCONNECT = "disconnectFromSmartphoneTestFarm";
    private static final TestTaskNameValidator VALIDATOR = new TestTaskNameValidator();

    @Override
    public void apply(Project project) {
        final Task connectionTask = project.getTasks().create(TASK_NAME_CONNECT, StfConnectionTask.class);
        final StfDisconnectionTask disconnectionTask = project.getTasks().create(TASK_NAME_DISCONNECT, StfDisconnectionTask.class);
        project.getExtensions().create(FARM_SETTINGS, PluginSettings.class);
        addTask(project, connectionTask, disconnectionTask);
    }

    private void addTask(final Project project, final Task connectionTask, final Task disconnectionTask) {
        project.getTasks().whenTaskAdded(new Action<Task>() {
            @Override
            public void execute(Task task) {
                if (VALIDATOR.isAndroidTestTaskName(task.getName())) {
                    task.dependsOn(connectionTask);
                    task.finalizedBy(disconnectionTask);
                    logTaskAttached(task, project);
                }
            }
        });
    }

    private void logTaskAttached(Task task, Project project) {
        project.getLogger().log(LogLevel.DEBUG, "STF connection task attached to the " + task.getName() + " task");
    }

}
