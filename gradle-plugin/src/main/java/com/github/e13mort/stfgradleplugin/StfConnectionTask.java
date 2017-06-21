package com.github.e13mort.stfgradleplugin;

import com.github.e13mort.stf.client.DevicesParams;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.FarmInfo;

import org.gradle.BuildAdapter;
import org.gradle.BuildResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class StfConnectionTask extends DefaultTask {

    private static final String TAG_STF = "STF";
    private static final String TAG_ADB = "ADB";

    @TaskAction
    public void run() {
        final PluginSettings settings = getSettings();
        final FarmInfo info = getFarmInfo(settings);
        final FarmClient farmClient = FarmClient.create(info);
        getProject().getGradle().addBuildListener(new CleanListener(farmClient));
        farmClient
                .connectToDevices(getDeviceParams())
                .subscribe(new NotificationConsumer(info), new ThrowableConsumer());
    }

    private DevicesParams getDeviceParams() {
        final ProjectPropertyReader reader = new ProjectPropertyReader(getProject(), getLogger());
        return reader.readParams();
    }

    private FarmInfo getFarmInfo(PluginSettings settings) {
        return new FarmInfo(settings.getBaseUrl(), settings.getApiKey(), settings.getAdbPath(), settings.getTimeout());
    }

    private PluginSettings getSettings() {
        final PluginSettings settings = getProject().getExtensions().findByType(PluginSettings.class);
        if (settings == null) {
            throw new NullPointerException("Settings is null");
        }
        settings.validate(getLogger());
        return settings;
    }

    private void log(String tag, String message) {
        getLogger().log(LogLevel.LIFECYCLE, tag + ": " + message);
    }

    private void log(String message, Throwable e) {
        getLogger().log(LogLevel.ERROR, message, e);
    }

    private class ThrowableConsumer implements Consumer<Throwable> {
        @Override
        public void accept(@NonNull Throwable throwable) throws Exception {
            fail(throwable.getMessage());
            log("An error occurred during connection", throwable);
        }

        private void fail(String message) {
            getState().setOutcome(new Exception(message));
        }
    }

    private class NotificationConsumer implements Consumer<Notification<String>> {
        private final FarmInfo info;

        NotificationConsumer(FarmInfo info) {
            this.info = info;
        }

        @Override
        public void accept(@NonNull Notification<String> ipNotification) throws Exception {
            if (ipNotification.isOnNext()) {
                runAdb(info.getSdkPath() + "adb connect " + ipNotification.getValue());
                runAdb(info.getSdkPath() + "adb wait-for-any-device");
            } else {
                log("Skip device", ipNotification.getError());
            }
        }

        private void runAdb(String command) throws IOException {
            log(TAG_STF, "Run command: " + command);
            final Process process = Runtime.getRuntime().exec(command);
            final InputStream stream = process.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    log(TAG_ADB, line);
                }
            } catch (IOException e) {
                log("Line reading error", e.getMessage());
            } finally {
                reader.close();
                stream.close();
            }
        }
    }

    private class CleanListener extends BuildAdapter {

        private final FarmClient farmClient;

        CleanListener(FarmClient farmClient) {
            this.farmClient = farmClient;
        }


        @Override
        public void buildFinished(BuildResult buildResult) {
            farmClient.disconnectFromAllDevices()
                    .subscribe(new Consumer<Notification<String>>() {
                                   @Override
                                   public void accept(@NonNull Notification<String> notification) throws Exception {
                                       log(TAG_STF, "Disconnected from " + notification.getValue());
                                   }
                               }
                            , new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception {
                                    log("Disconnection error", throwable);
                                }
                            });
        }

    }

}
