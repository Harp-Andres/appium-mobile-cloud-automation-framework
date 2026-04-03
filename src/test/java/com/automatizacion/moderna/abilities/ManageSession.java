package com.automatizacion.moderna.abilities;

import io.appium.java_client.AppiumDriver;

/**
 * Habilidad Screenplay para gestionar la sesión Appium (abrir/cerrar app, reset, etc.).
 * Permite polimorfismo y extensión para cualquier gestión de sesión.
 */
public final class ManageSession implements Ability {
    private final AppiumDriver driver;

    private ManageSession(AppiumDriver driver) {
        if (driver == null) throw new IllegalArgumentException("El driver no puede ser null");
        this.driver = driver;
    }

    @Override
    public void bindDriver(AppiumDriver driver) {
        throw new UnsupportedOperationException("ManageSession es inmutable. Use ManageSession.with(driver) para crear una nueva instancia.");
    }

    /**
     * Factory estático para crear la habilidad.
     */
    public static ManageSession with(AppiumDriver driver) {
        return new ManageSession(driver);
    }

    /**
     * Cierra la aplicación bajo prueba.
     */
    public void closeApp() {
        driver.executeScript("mobile: closeApp");
    }

    /**
     * Lanza la aplicación bajo prueba.
     */
    public void launchApp() {
        driver.executeScript("mobile: launchApp");
    }

    /**
     * Resetea la aplicación bajo prueba.
     */
    public void resetApp() {
        driver.executeScript("mobile: reset");
    }

    public AppiumDriver getDriver() {
        return driver;
    }
}
