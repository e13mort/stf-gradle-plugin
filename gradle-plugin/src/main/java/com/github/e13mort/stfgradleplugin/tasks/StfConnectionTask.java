package com.github.e13mort.stfgradleplugin.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.FarmInfo;
import com.github.e13mort.stf.client.parameters.DevicesParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class StfConnectionTask extends StfTask {

    @Override
    public void run() {
        final FarmInfo info = getFarmInfo();
        final FarmClient farmClient = FarmClient.create(info);
        final DevicesParams deviceParams = getDeviceParams();
        final NotificationsHandler handler = new NotificationsHandler(info);
        logI(TAG_STF, convertParamsToString(deviceParams));
        farmClient
                .connectToDevices(deviceParams)
                .subscribe(handler, new ThrowableConsumer(), handler);
    }

    private DevicesParams getDeviceParams() {
        return new PropertiesDevicesParamsImpl(getProject().getProperties(), getLogger());
    }

    private String convertParamsToString(final DevicesParams deviceParams) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(deviceParams);
        } catch (JsonProcessingException e) {
            return deviceParams.toString();
        }
    }

    private class ThrowableConsumer implements Consumer<Throwable> {
        @Override
        public void accept(@NonNull Throwable throwable) throws Exception {
            getState().setOutcome(new Exception(throwable.getMessage()));
            log("An error occurred during connection", throwable);
        }
    }

    private class NotificationsHandler implements Consumer<Notification<String>>, Action {
        private final FarmInfo info;

        NotificationsHandler(FarmInfo info) {
            this.info = info;
        }

        @Override
        public void accept(@NonNull Notification<String> ipNotification) throws Exception {
            if (ipNotification.isOnNext()) {
                runAdb(info.getSdkPath() + "adb connect " + ipNotification.getValue());
            } else {
                log("Skip device", ipNotification.getError());
            }
        }

        @Override
        public void run() throws Exception {
            runAdb(info.getSdkPath() + "adb wait-for-any-device");
        }

        private void runAdb(String command) throws IOException {
            logL(StfTask.TAG_STF, "Run command: " + command);
            final Process process = Runtime.getRuntime().exec(command);
            final InputStream stream = process.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    logL(StfTask.TAG_ADB, line);
                }
            } catch (IOException e) {
                log("Line reading error", e);
            } finally {
                reader.close();
                stream.close();
            }
        }
    }

}
