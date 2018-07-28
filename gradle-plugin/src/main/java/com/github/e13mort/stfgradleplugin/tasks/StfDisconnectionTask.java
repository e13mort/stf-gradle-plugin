package com.github.e13mort.stfgradleplugin.tasks;

import com.github.e13mort.stf.client.FarmClient;

/**
 * Created by pavel
 */

public class StfDisconnectionTask extends StfTask {

    @Override
    public void run() {
        final FarmClient client = FarmClient.create(getFarmInfo());
        client.disconnectFromAllDevices()
                .subscribe(
                        notification -> logI(TAG_STF, "Disconnected from " + notification.getValue()),
                        throwable -> log("Disconnection error", throwable));
    }
}
