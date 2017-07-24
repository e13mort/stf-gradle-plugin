package com.github.e13mort.stfgradleplugin;

class TestTaskNameValidator {
    boolean isAndroidTestTaskName(String name) {
        if (name == null) {
            return false;
        }
        name = name.toLowerCase();
        return name.startsWith("connected") && name.endsWith("androidtest");
    }
}
