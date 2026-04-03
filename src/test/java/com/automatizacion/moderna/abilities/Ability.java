
package com.automatizacion.moderna.abilities;

import io.appium.java_client.AppiumDriver;

/**
 * Interfaz base para todas las habilidades Screenplay móviles.
 * Solo permite asociar AppiumDriver, nunca drivers web.
 * Esto garantiza que el framework sea 100% móvil.
 */
public interface Ability {
    /**
     * Asocia el driver Appium instanciado a la habilidad.
     * @param driver AppiumDriver ya instanciado (no acepta WebDriver web)
     */
    void bindDriver(AppiumDriver driver);
}
