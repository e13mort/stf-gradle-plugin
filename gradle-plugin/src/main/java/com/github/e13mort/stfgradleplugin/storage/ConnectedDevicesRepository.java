package com.github.e13mort.stfgradleplugin.storage;

import java.util.Collections;
import java.util.List;

public interface ConnectedDevicesRepository {
    ConnectedDevicesRepository EMPTY = new ConnectedDevicesRepository() {
        @Override
        public List<String> connectedDevices() {
            return Collections.emptyList();
        }

        @Override
        public void storeConnectedDevices(List<String> connectedDevice) {

        }

        @Override
        public void clear() {

        }
    };

    List<String> connectedDevices();

    void storeConnectedDevices(List<String> connectedDevice);

    void clear();
}
