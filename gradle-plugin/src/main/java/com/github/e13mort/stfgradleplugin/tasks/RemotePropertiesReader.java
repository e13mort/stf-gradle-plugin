package com.github.e13mort.stfgradleplugin.tasks;

import com.github.e13mort.stf.client.parameters.DevicesParams;
import com.github.e13mort.stf.client.parameters.JsonDeviceParametersReader;
import com.github.e13mort.stf.client.parameters.JsonDeviceParametersReader.JsonParamsReaderException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

class RemotePropertiesReader {

    private static final String KEY_STF_PROPERTIES_URL = "STF_PROPERTIES_URL";
    private final URL url;
    private final JsonDeviceParametersReader parametersReader = new JsonDeviceParametersReader();

    static RemotePropertiesReader of(Map<String, ?> properties) throws MalformedURLException {
        if (properties.containsKey(KEY_STF_PROPERTIES_URL)) {
            return createReader(properties);
        }
        return null;
    }

    private static RemotePropertiesReader createReader(Map<String, ?> properties) throws MalformedURLException {
        final String url = (String) properties.get(KEY_STF_PROPERTIES_URL);
        return new RemotePropertiesReader(new URL(url));
    }

    private RemotePropertiesReader(URL url) {
        this.url = url;
    }

    DevicesParams readProperties() throws JsonParamsReaderException {
        return parametersReader.read(url);
    }
}
