package com.automatizacion.moderna.abilities;

import io.appium.java_client.AppiumDriver;

/**
 * Habilidad Screenplay para obtener información del dispositivo móvil.
 */
public class ReadDeviceInfo implements Ability {
    private AppiumDriver driver;

    public ReadDeviceInfo(AppiumDriver driver) {
        if (driver == null) throw new IllegalArgumentException("El driver no puede ser null");
        this.driver = driver;
    }

    @Override
    public void bindDriver(AppiumDriver driver) {
        this.driver = driver;
    }

    /**
     * Retorna el nombre del dispositivo.
     */
    public String getDeviceName() {
        return (String) driver.getCapabilities().getCapability("deviceName");
    }

    /**
     * Retorna la versión del sistema operativo.
     */
    public String getPlatformVersion() {
        return (String) driver.getCapabilities().getCapability("platformVersion");
    }

    /**
     * Retorna el nombre del sistema operativo.
     */
    public String getPlatformName() {
        return (String) driver.getCapabilities().getCapability("platformName");
    }

    public AppiumDriver getDriver() {
        return driver;
    }
}

