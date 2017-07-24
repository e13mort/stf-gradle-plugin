package com.github.e13mort.stfgradleplugin;

import com.github.e13mort.stf.adapter.filters.StringsFilterParser;
import com.github.e13mort.stf.client.DevicesParams;

import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;

import java.util.Map;

class ProjectPropertyReader {

    private static final String KEY_STF_PROVIDER = "STF_PROVIDER";
    private static final String KEY_STF_COUNT = "STF_COUNT";
    private static final String KEY_STF_ABI = "STF_ABI";
    private static final String KEY_STF_NAME = "STF_NAME";
    private static final String KEY_STF_SERIAL = "STF_SERIAL";
    private static final String KEY_STF_MIN_API = "STF_MIN_API";
    private static final String KEY_STF_MAX_API = "STF_MAX_API";

    private final Project project;
    private final Logger logger;

    public ProjectPropertyReader(Project project, Logger logger) {
        this.project = project;
        this.logger = logger;
    }

    public DevicesParams readParams() {
        final DevicesParams params = new DevicesParams();
        final Map<String, ?> properties = project.getProperties();
        setupProvider(params, properties);
        setupCount(params, properties);
        setupAbi(params, properties);
        setupName(params, properties);
        setupSerial(params, properties);
        setupMinApi(params, properties);
        setupMaxApi(params, properties);
        return params;
    }

    private void setupMaxApi(DevicesParams params, Map<String, ?> properties) {
        if (properties.containsKey(KEY_STF_MAX_API)) {
            try {
                params.setMaxApiVersion(Integer.parseInt((String) properties.get(KEY_STF_MAX_API)));
            } catch (Exception e) {
                log(e);
            }
        }
    }

    private void setupMinApi(DevicesParams params, Map<String, ?> properties) {
        if (properties.containsKey(KEY_STF_MIN_API)) {
            try {
                params.setMinApiVersion(Integer.parseInt((String) properties.get(KEY_STF_MIN_API)));
            } catch (Exception e) {
                log(e);
            }
        }
    }

    private void setupName(DevicesParams params, Map<String, ?> properties) {
        if (properties.containsKey(KEY_STF_NAME)) {
            final StringsFilterParser parser = new StringsFilterParser();
            try {
                params.setNameFilterDescription(parser.parse((String) properties.get(KEY_STF_NAME)));
            } catch (Exception e) {
                log(e);
            }
        }
    }

    private void setupSerial(DevicesParams params, Map<String, ?> properties) {
        if (properties.containsKey(KEY_STF_SERIAL)) {
            final StringsFilterParser parser = new StringsFilterParser();
            try {
                params.setSerialFilterDescription(parser.parse((String) properties.get(KEY_STF_SERIAL)));
            } catch (Exception e) {
                log(e);
            }
        }
    }

    private void setupAbi(DevicesParams params, Map<String, ?> properties) {
        try {
            params.setAbi((String) properties.get(KEY_STF_ABI));
        } catch (Exception e) {
            log(e);
        }
    }

    private void setupProvider(DevicesParams params, Map<String, ?> properties) {
        if (properties.containsKey(KEY_STF_PROVIDER)) {
            final StringsFilterParser parser = new StringsFilterParser();
            try {
                params.setProviderFilterDescription(parser.parse((String) properties.get(KEY_STF_PROVIDER)));
            } catch (Exception e) {
                log(e);
            }
        }
    }

    private void setupCount(DevicesParams params, Map<String, ?> properties) {
        if (properties.containsKey(KEY_STF_COUNT)) {
            try {
                params.setCount(Integer.parseInt((String) properties.get(KEY_STF_COUNT)));
            } catch (Exception e) {
                log(e);
            }
        }
    }

    private void log(Exception e) {
        logger.log(LogLevel.ERROR, "Params parsing error", e);
    }
}
