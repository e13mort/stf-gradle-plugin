package com.github.e13mort.stfgradleplugin;

import org.gradle.api.Project;

public class Plugin implements org.gradle.api.Plugin<Project> {

    private static final String FARM_SETTINGS = "farmSettings";
    private static final String TASK_NAME = "connectToSmartphoneTestFarm";

    @Override
    public void apply(Project project) {
        project.getTasks().create(TASK_NAME, StfConnectionTask.class);
        project.getExtensions().create(FARM_SETTINGS, PluginSettings.class);
    }

}
