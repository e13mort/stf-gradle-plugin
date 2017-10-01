package com.github.e13mort.stfgradleplugin.tasks;

import com.github.e13mort.stf.client.DevicesParams;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.FarmInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class StfConnectionTask extends StfTask {

    @Override
    public void run() {
        final FarmInfo info = getFarmInfo();
        final FarmClient farmClient = FarmClient.create(info);
        farmClient
                .connectToDevices(getDeviceParams())
                .subscribe(new NotificationConsumer(info), new ThrowableConsumer());
    }

    private DevicesParams getDeviceParams() {
        return new PropertiesDevicesParamsImpl(getProject().getProperties(), getLogger());
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
            log(StfTask.TAG_STF, "Run command: " + command);
            final Process process = Runtime.getRuntime().exec(command);
            final InputStream stream = process.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    log(StfTask.TAG_ADB, line);
                }
            } catch (IOException e) {
                log("Line reading error", e.getMessage());
            } finally {
                reader.close();
                stream.close();
            }
        }
    }

}
