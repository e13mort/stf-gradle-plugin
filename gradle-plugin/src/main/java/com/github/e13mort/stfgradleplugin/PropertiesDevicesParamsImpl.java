package com.github.e13mort.stfgradleplugin;

import com.github.e13mort.stf.adapter.filters.StringsFilterDescription;
import com.github.e13mort.stf.adapter.filters.StringsFilterParser;
import com.github.e13mort.stf.client.DevicesParams;

import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;

import java.util.Map;

/**
 * Created by pavel
 */
final class PropertiesDevicesParamsImpl implements DevicesParams {

    private static final String KEY_STF_PROVIDER = "STF_PROVIDER";
    private static final String KEY_STF_COUNT = "STF_COUNT";
    private static final String KEY_STF_ABI = "STF_ABI";
    private static final String KEY_STF_NAME = "STF_NAME";
    private static final String KEY_STF_SERIAL = "STF_SERIAL";
    private static final String KEY_STF_MIN_API = "STF_MIN_API";
    private static final String KEY_STF_MAX_API = "STF_MAX_API";
    private final Logger logger;

    private final Map<String, ?> properties;

    public PropertiesDevicesParamsImpl(Map<String, ?> properties, Logger logger) {
        this.logger = logger;
        this.properties = properties;
    }

    @Override
    public boolean isAllDevices() {
        return false;
    }

    @Override
    public String getAbi() {
        return readString(properties, KEY_STF_ABI);
    }

    @Override
    public int getApiVersion() {
        return 0;
    }

    @Override
    public int getCount() {
        return readInteger(KEY_STF_COUNT);
    }

    @Override
    public StringsFilterDescription getNameFilterDescription() {
        return readStringsFilterDescription(KEY_STF_NAME);
    }

    @Override
    public int getMinApiVersion() {
        return readInteger(KEY_STF_MIN_API);
    }

    @Override
    public int getMaxApiVersion() {
        return readInteger(KEY_STF_MAX_API);
    }

    @Override
    public StringsFilterDescription getProviderFilterDescription() {
        return readStringsFilterDescription(KEY_STF_PROVIDER);
    }

    @Override
    public StringsFilterDescription getSerialFilterDescription() {
        return readStringsFilterDescription(KEY_STF_SERIAL);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PropertiesDevicesParamsImpl{");
        sb.append("allDevices=").append(isAllDevices());
        sb.append(", abi='").append(getAbi()).append('\'');
        sb.append(", apiVersion=").append(getApiVersion());
        sb.append(", count=").append(getCount());
        sb.append(", nameFilterDescription=").append(getNameFilterDescription());
        sb.append(", minApiVersion=").append(getMinApiVersion());
        sb.append(", maxApiVersion=").append(getMaxApiVersion());
        sb.append(", providerFilterDescription=").append(getProviderFilterDescription());
        sb.append(", serialFilterDescription=").append(getSerialFilterDescription());
        sb.append('}');
        return sb.toString();
    }

    private int readInteger(String key) {
        if (properties.containsKey(key)) {
            try {
                final String value = readString(properties, key);
                if (notEmpty(value)) return Integer.parseInt(value);
            } catch (Exception e) {
                log(e);
            }
        }
        return 0;
    }

    private String readString(Map<String, ?> properties, String key) {
        final String str = (String) properties.get(key);
        if (str == null) {
            return null;
        }
        final String trim = str.trim();
        return trim.length() != 0 ? trim : null;
    }

    private StringsFilterDescription readStringsFilterDescription(String key) {
        if (properties.containsKey(key)) {
            final StringsFilterParser parser = new StringsFilterParser();
            try {
                final String template = readString(properties, key);
                if (notEmpty(template)) return (parser.parse(template));
            } catch (Exception e) {
                log(e);
            }
        }
        return null;
    }

    private boolean notEmpty(String value) {
        return value != null && value.length() != 0;
    }

    private void log(Exception e) {
        logger.log(LogLevel.ERROR, "Params parsing error", e);
    }
}
