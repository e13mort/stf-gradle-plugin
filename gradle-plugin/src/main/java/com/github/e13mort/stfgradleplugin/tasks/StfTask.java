package com.github.e13mort.stfgradleplugin.tasks;

import com.github.e13mort.stf.client.FarmInfo;
import com.github.e13mort.stfgradleplugin.PluginSettings;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.TaskAction;

/**
 * Created by pavel
 */

public abstract class StfTask extends DefaultTask {

    public static final String TAG_STF = "STF";
    public static final String TAG_ADB = "ADB";

    @TaskAction
    public abstract void run();

    protected FarmInfo getFarmInfo() {
        final PluginSettings settings = getSettings();
        return new FarmInfo(settings.getBaseUrl(), settings.getApiKey(), settings.getAdbPath(), settings.getTimeout());
    }

    protected void log(String tag, String message) {
        getLogger().log(LogLevel.LIFECYCLE, tag + ": " + message);
    }

    private PluginSettings getSettings() {
        final PluginSettings settings = getProject().getExtensions().findByType(PluginSettings.class);
        if (settings == null) {
            throw new NullPointerException("Settings is null");
        }
        settings.validate(getLogger());
        return settings;
    }

    protected void log(String message, Throwable e) {
        getLogger().log(LogLevel.ERROR, message, e);
    }

}
