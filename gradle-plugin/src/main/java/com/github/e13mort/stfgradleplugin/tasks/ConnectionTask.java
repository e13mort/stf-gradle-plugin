package com.github.e13mort.stfgradleplugin.tasks;

import java.util.Collections;
import java.util.List;

public interface ConnectionTask {
    ConnectionTask EMPTY = Collections::emptyList;

    List<String> connectedDevices();
}
