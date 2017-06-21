# STF gradle plugin

The `stf-gradle-plugin` allows you to automate a [farm](https://openstf.io/) connection process. 

## Enabling the plugin

To use the plugin, you first need to configure `build.gradle` as follows
``` groovy
buildscript {
    repositories {
        maven { url 'http://dl.bintray.com/e13mort/maven' }
    }
    dependencies {
        classpath 'com.github.e13mort:stf-gradle-plugin:0.2.0'
    }
}

apply plugin: 'stf-gradle-plugin'
```

After that, you have to attach the plugin's farm connection task to your test task
```groovy
project.tasks.whenTaskAdded {
    Task task ->
        if (task.getName() == 'connectedDebugAndroidTest') {
            task.dependsOn(connectToSmartphoneTestFarm)
        }
        
}
```

Also you have to setup the farm settings in `build.gradle`
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
- STF_NAME - filter devices by name. Might be a name part, like 'Nexus' for all the nexus devices on your farm
- STF_ABI - filter devices by platform. Might be a part, like 'arm' to include arm-v7, arm-v8, etc
- STF_MIN_API - filter out devices with API lower than specified
- STF_MAX_API - filter out devices with API greater than specified
- STF_PROVIDER - list of device providers. 'provider1,provider' - use devices only from this providers. '~support,support2' - don't use a supports devices.