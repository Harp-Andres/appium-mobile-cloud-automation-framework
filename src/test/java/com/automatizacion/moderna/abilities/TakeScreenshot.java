package com.automatizacion.moderna.abilities;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import io.appium.java_client.AppiumDriver;

/**
 * Habilidad Screenplay para tomar capturas de pantalla desde el actor.
 */
public final class TakeScreenshot implements Ability {
    private final AppiumDriver driver;

    private TakeScreenshot(AppiumDriver driver) {
        if (driver == null) throw new IllegalArgumentException("El driver no puede ser null");
        this.driver = driver;
    }

    @Override
    public void bindDriver(AppiumDriver driver) {
        throw new UnsupportedOperationException("TakeScreenshot es inmutable. Use TakeScreenshot.with(driver) para crear una nueva instancia.");
    }

    /**
     * Factory estático para crear la habilidad.
     */
    public static TakeScreenshot with(AppiumDriver driver) {
        return new TakeScreenshot(driver);
    }

    /**
     * Toma una captura de pantalla y la retorna como arreglo de bytes.
     */
    public byte[] capture() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public AppiumDriver getDriver() {
        return driver;
    }
}
