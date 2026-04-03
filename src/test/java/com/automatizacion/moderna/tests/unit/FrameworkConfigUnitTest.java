package com.automatizacion.moderna.tests.unit;

import com.automatizacion.moderna.config.FrameworkConfig;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FrameworkConfigUnitTest {

    @AfterEach
    void cleanUp() {
        System.clearProperty("ENV");
        System.clearProperty("platform.target");
        System.clearProperty("ios.platform.name");
        System.clearProperty("ios.device.name");
        System.clearProperty("appium.server.url");
        FrameworkConfig.reset();
    }

    @Test
    void shouldFailWhenTestEnvIsMissing() {
        System.setProperty("ENV", "   ");
        FrameworkConfig.reset();

        IllegalStateException exception = Assertions.assertThrows(
            IllegalStateException.class,
            FrameworkConfig::getInstance
        );

        Assertions.assertTrue(exception.getMessage().contains("ENV"));
    }

    @Test
    void shouldLoadLocalEnvironmentWhenTestEnvIsProvided() {
        System.setProperty("ENV", "local");
        System.setProperty("platform.target", "ios");
        System.setProperty("ios.platform.name", "iOS");
        System.setProperty("ios.device.name", "iPhone 16 Pro");
        FrameworkConfig.reset();

        try {
            FrameworkConfig config = FrameworkConfig.getInstance();

            Assertions.assertEquals("local", config.getActiveEnv());
            Assertions.assertEquals("ios", config.getDefaultPlatform());
            Assertions.assertEquals("iOS", config.getForPlatform("ios", "platform.name"));
            Assertions.assertEquals("iPhone 16 Pro", config.getForPlatform("ios", "device.name"));
        } finally {
            FrameworkConfig.reset();
        }
    }

    @Test
    void shouldLoadBrowserstackEnvironmentFile() {
        System.setProperty("ENV", "browserstack");
        FrameworkConfig.reset();

        try {
            FrameworkConfig config = FrameworkConfig.getInstance();

            Assertions.assertEquals("browserstack", config.getActiveEnv());
            Assertions.assertFalse(config.get("appium.server.url").isBlank());
            Assertions.assertFalse(config.getForPlatform("android", "device.name").isBlank());
            Assertions.assertEquals("UiAutomator2", config.getForPlatform("android", "automation.name"));
        } finally {
            FrameworkConfig.reset();
        }
    }
}



