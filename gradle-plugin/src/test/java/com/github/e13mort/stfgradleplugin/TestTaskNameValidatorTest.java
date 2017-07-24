package com.github.e13mort.stfgradleplugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTaskNameValidatorTest {

    private TestTaskNameValidator validator;

    @BeforeEach
    void init() {
        validator = new TestTaskNameValidator();
    }

    @Test
    @DisplayName("null should be invalid")
    void validateNull() throws Exception {
        assertFalse(validator.isAndroidTestTaskName(null));
    }

    @Test
    @DisplayName("default connectedAndroidTest task name should be valid")
    void validateRegularTask() throws Exception {
        assertTrue(validator.isAndroidTestTaskName("connectedAndroidTest"));
    }

    @Test
    @DisplayName("default connectedAndroidTest task name should be valid")
    void validateRegularDebugTask() throws Exception {
        assertTrue(validator.isAndroidTestTaskName("connectedDebugAndroidTest"));
    }

    @Test
    @DisplayName("default connectedAndroidTest task name should be valid")
    void validateAssembleTask() throws Exception {
        assertFalse(validator.isAndroidTestTaskName("assemble"));
    }
}