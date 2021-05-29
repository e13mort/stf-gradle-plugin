package com.github.e13mort.stfgradleplugin.tasks;

import com.github.e13mort.stf.client.FarmClient;

import java.util.List;

/**
 * Created by pavel
 */

public class StfDisconnectionTask extends StfTask {

    private ConnectionTask connectionTask = ConnectionTask.EMPTY;

    @Override
    public void run() {
        final FarmClient client = FarmClient.create(getFarmInfo());
        if (shouldDisconnectFromActiveDevices()) {
            disconnectFromActiveDevice(client);
        } else {
            disconnectFromAllDevice(client);
        }
    }

    private void disconnectFromAllDevice(FarmClient client) {
        //noinspection ResultOfMethodCallIgnored
        client.disconnectFromAllDevices()
                .subscribe(
                        notification -> logL(TAG_STF, "Disconnected from " + notification.getValue()),
                        throwable -> log("Disconnection error", throwable));
    }

    private void disconnectFromActiveDevice(FarmClient client) {
        final List<String> serialNumbers = connectionTask.connectedDevices();
        logI(StfTask.TAG_STF, "Disconnect from: " + serialNumbers.toString());
        //noinspection ResultOfMethodCallIgnored
        client.disconnectFromDevices(serialNumbers)
                .subscribe(
                        notification -> logL(TAG_STF, "Disconnected from " + notification.getValue()),
                        throwable -> log("Disconnection error", throwable));
    }

    private boolean shouldDisconnectFromActiveDevices() {
        return getSettings().isDisconnectFromActive();
    }

    public void take(ConnectionTask connectionTask) {
        this.connectionTask = connectionTask;
    }
}
