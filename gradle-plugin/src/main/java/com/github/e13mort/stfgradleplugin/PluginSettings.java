package com.github.e13mort.stfgradleplugin;

import org.gradle.api.logging.Logger;

@SuppressWarnings("unused")
public class PluginSettings {
    private String baseUrl;
    private String apiKey;
    private String adbPath;
    private int timeout;
    private boolean disconnectFromActive;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setAdbPath(String adbPath) {
        this.adbPath = adbPath;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getAdbPath() {
        return adbPath;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isDisconnectFromActive() {
        return disconnectFromActive;
    }

    public void setDisconnectFromActive(boolean disconnectFromActive) {
        this.disconnectFromActive = disconnectFromActive;
    }

    public void validate(Logger logger) {
        doValidation(logger, baseUrl, apiKey);
    }

    private void doValidation(Logger logger, Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                logger.error(toString());
                throw new IllegalArgumentException("Invalid settings");
            }
        }
    }

    @Override
    public String toString() {
        return "PluginSettings{" +
                "baseUrl='" + baseUrl + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", adbPath='" + adbPath + '\'' +
                ", timeout=" + timeout +
                ", disconnectFromActive=" + disconnectFromActive +
                '}';
    }
}
