# STF gradle plugin

[![Build Status](https://travis-ci.org/e13mort/STF-gradle-plugin.svg?branch=master)](https://travis-ci.org/e13mort/STF-gradle-plugin)

The `stf-gradle-plugin` allows you to automate a [farm](https://openstf.io/) connection process. 

## Enabling the plugin

To use the plugin, you first need to configure `build.gradle` as follows
``` groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.github.e13mort:stf-gradle-plugin:0.2.3'
    }
}

apply plugin: 'stf-gradle-plugin'
```

After that, you have to setup the farm settings in `build.gradle`
```groovy
farmSettings {
    baseUrl = 'http://your.farm.address'
    apiKey = 'api key created in user settings'
    adbPath = '<path to adb folder>'
    timeout = '<farm connection timeout in seconds. 1 minute by default>'
}
```

The last step is to pass a connection params into test task:
 ```bash
 ./gradlew :sample-app:connectedDebugAndroidTest -PSTF_PROVIDER=~support -PSTF_COUNT=5 -PSTF_MIN_API=21
 ```
 
## Connection params
You can specify the following params in order to select devices:
- STF_COUNT - how many devices you want to target for a test launch
- STF_NAME - list of devices names. Might be a name part, like 'Nexus' for all the nexus devices. 
- STF_ABI - filter devices by platform. Might be a part, like 'arm' to include arm-v7, arm-v8, etc
- STF_MIN_API - filter out devices with API lower than specified
- STF_MAX_API - filter out devices with API greater than specified
- STF_PROVIDER - list of device providers. 'provider1,provider' - use devices only from this providers.
- STF_SERIAL - list of serial numbers divided by `,`

You can store connection params as a separate file (e.g. in VCS) and pass is's URL as 'STF_PROPERTIES_URL' parameter:
 ```bash
 ./gradlew :sample-app:connectedDebugAndroidTest -PSTF_PROPERTIES_URL=http://you-server/sample_params.json
 ```
 File format described on the [stf-console-client page](https://github.com/e13mort/stf-console-client#store-connection-parameters-in-a-file).

By default, list parameters (name, provider and serial) are work with 'inclusion' logic. 
To use this parameter with 'exclusion' logic use the `~` sign before a list. 
E.g. -PSTF_SERIAL='~AA,BB' will keep all devices except the ones with serial numbers 'AA' and 'BB'        