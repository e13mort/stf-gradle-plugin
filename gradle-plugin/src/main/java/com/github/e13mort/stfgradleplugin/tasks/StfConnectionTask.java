package com.github.e13mort.stfgradleplugin.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.e13mort.stf.adapter.ConnectedFarmDevice;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.FarmInfo;
import com.github.e13mort.stf.client.parameters.DevicesParams;
import com.github.e13mort.stf.client.parameters.JsonDeviceParametersReader.JsonParamsReaderException;
import com.github.e13mort.stfgradleplugin.storage.ConnectedDevicesRepository;

import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StfConnectionTask extends StfTask {

    private ConnectedDevicesRepository devicesRepository = ConnectedDevicesRepository.EMPTY;

    @Override
    public void run() {
        final FarmInfo info = getFarmInfo();
        final FarmClient farmClient = FarmClient.create(info);
        final DevicesParams deviceParams = getDeviceParams();
        final NotificationsHandler handler = new NotificationsHandler(info, devicesRepository);
        logI(TAG_STF, convertParamsToString(deviceParams));
        //noinspection ResultOfMethodCallIgnored
        farmClient
                .connectToDevicesByParams(deviceParams)
                .subscribe(handler, new ThrowableConsumer(), handler);
        List<String> connectedDevices = handler.connectedDevices;
        logI(TAG_STF, "Connected devices: " + connectedDevices);
    }

    public void setDevicesRepository(ConnectedDevicesRepository devicesRepository) {
        this.devicesRepository = devicesRepository;
    }

    private DevicesParams getDeviceParams() {
        final Map<String, ?> properties = getProject().getProperties();
        final DevicesParams params = readRemoteParams(properties);
        if (params != null) return params;
        return readPropertiesParams(properties);
    }

    private PropertiesDevicesParamsImpl readPropertiesParams(Map<String, ?> properties) {
        return new PropertiesDevicesParamsImpl(properties, getLogger());
    }

    private DevicesParams readRemoteParams(Map<String, ?> properties) {
        try {
            final RemotePropertiesReader reader = RemotePropertiesReader.of(properties);
            if (reader != null) return reader.readProperties();
        } catch (MalformedURLException | JsonParamsReaderException e) {
            getLogger().info("Remote parameters reading error", e);
        }
        return null;
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
        public void accept(@NonNull Throwable throwable) {
            getState().setOutcome(new RuntimeException(throwable.getMessage()));
            logL(TAG_STF, "An error occurred during connection: " + throwable.getMessage());
        }
    }

    private class NotificationsHandler implements Consumer<Notification<ConnectedFarmDevice>>, Action {
        private final FarmInfo info;
        private final ConnectedDevicesRepository devicesRepository;
        boolean needToWaitForDevice;
        private final List<String> connectedDevices = new ArrayList<>();

        NotificationsHandler(FarmInfo info, ConnectedDevicesRepository devicesRepository) {
            this.info = info;
            this.devicesRepository = devicesRepository;
        }

        @Override
        public void accept(@NonNull Notification<ConnectedFarmDevice> ipNotification) throws Exception {
            if (ipNotification.isOnNext()) {
                final ConnectedFarmDevice response = ipNotification.getValue();
                if (response != null) {
                    runAdb(info.getSdkPath() + "adb connect " + response.getRemoteConnectUrl());
                    connectedDevices.add(response.getSerial());
                }
            } else {
                log("Skip device", ipNotification.getError());
            }
            needToWaitForDevice = true;
        }

        @Override
        public void run() throws Exception {
            devicesRepository.storeConnectedDevices(connectedDevices);
            if (needToWaitForDevice) {
                runAdb(info.getSdkPath() + "adb wait-for-any-device");
            } else {
                logL(TAG_STF, "There are no devices");
            }
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
