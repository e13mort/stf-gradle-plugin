package com.github.e13mort.stfgradleplugin.tasks;

import com.github.e13mort.stf.client.FarmClient;

import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by pavel
 */

public class StfDisconnectionTask extends StfTask {

    @Override
    public void run() {
        final FarmClient client = FarmClient.create(getFarmInfo());
        client.disconnectFromAllDevices()
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
