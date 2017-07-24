package com.github.e13mort.stfgradleplugin;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.LogLevel;

public class Plugin implements org.gradle.api.Plugin<Project> {

    private static final String FARM_SETTINGS = "farmSettings";
    private static final String TASK_NAME = "connectToSmartphoneTestFarm";
    private static final TestTaskNameValidator VALIDATOR = new TestTaskNameValidator();

    @Override
    public void apply(Project project) {
        final StfConnectionTask connectionTask = project.getTasks().create(TASK_NAME, StfConnectionTask.class);
        project.getExtensions().create(FARM_SETTINGS, PluginSettings.class);
        addTask(project, connectionTask);
    }

    private void addTask(final Project project, final StfConnectionTask connectionTask) {
        project.getTasks().whenTaskAdded(new Action<Task>() {
            @Override
            public void execute(Task task) {
                if (VALIDATOR.isAndroidTestTaskName(task.getName())) {
                    task.dependsOn(connectionTask);
                    logTaskAttached(task, project);
                }

            }
        });
    }

    private void logTaskAttached(Task task, Project project) {
        project.getLogger().log(LogLevel.LIFECYCLE, "attached to the " + task.getName() + " task");
    }

}
