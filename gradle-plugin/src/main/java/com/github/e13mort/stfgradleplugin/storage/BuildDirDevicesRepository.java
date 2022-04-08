package com.github.e13mort.stfgradleplugin.storage;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.gradle.api.file.RegularFile;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class BuildDirDevicesRepository implements ConnectedDevicesRepository {

    private final RegularFile cacheFile;
    private final Logger logger;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BuildDirDevicesRepository(RegularFile cacheFile, Logger logger) {
        this.cacheFile = cacheFile;
        this.logger = logger;
    }

    @Override
    public List<String> connectedDevices() {
        File cacheFile = cacheFile();
        try (JsonParser parser = objectMapper.createParser(cacheFile)) {
            StoredObject storedObject = parser.readValueAs(StoredObject.class);
            return storedObject.connectedDevice;
        } catch (IOException e) {
            logger.error("Failed to read connected device list from file", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void storeConnectedDevices(List<String> connectedDevice) {
        try (JsonGenerator generator = objectMapper.createGenerator(new FileWriter(cacheFile()))) {
            generator.writeObject(new StoredObject(connectedDevice));
        } catch (IOException e) {
            logger.error("Failed to save connected devices list to file", e);
        }
    }

    @Override
    public void clear() {
        if (!cacheFile().delete()) {
            logger.debug("Clear operation failed");
        };
    }

    private File cacheFile() {
        return cacheFile.getAsFile();
    }

    @SuppressWarnings("unused")
    private static class StoredObject {

        private List<String> connectedDevice;

        public StoredObject() {
            connectedDevice = Collections.emptyList();
        }

        public StoredObject(List<String> connectedDevice) {
            this.connectedDevice = connectedDevice;
        }

        public List<String> getConnectedDevice() {
            return connectedDevice;
        }

        public void setConnectedDevice(List<String> connectedDevice) {
            this.connectedDevice = connectedDevice;
        }
    }
}
