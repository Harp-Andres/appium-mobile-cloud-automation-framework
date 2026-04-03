
package com.automatizacion.moderna.abilities;

import io.appium.java_client.AppiumDriver;

/**
 * Habilidad Screenplay que permite al actor interactuar con la app móvil mediante un AppiumDriver.
 * Implementa el patrón Ability para ser gestionada por el actor.
 */
public record BrowseTheMobileApp(AppiumDriver driver) implements Ability {

    public BrowseTheMobileApp {
        if (driver == null) {
            throw new IllegalArgumentException("El driver no puede ser null");
        }
    }

    @Override
    public void bindDriver(AppiumDriver driver) {
        throw new UnsupportedOperationException("BrowseTheMobileApp es inmutable. Use BrowseTheMobileApp.with(driver) para crear una nueva instancia.");
    }

    /**
     * Instancia la habilidad con el driver dado.
     * @param driver AppiumDriver
     * @return Habilidad BrowseTheMobileApp
     */
    public static BrowseTheMobileApp with(AppiumDriver driver) {
        return new BrowseTheMobileApp(driver);
    }

    /**
     * Obtiene el driver de la habilidad asignada al actor.
     * Lanza excepción si el actor no tiene la habilidad.
     * @param actor Actor de Screenplay
     * @return AppiumDriver
     */
    public static AppiumDriver as(com.automatizacion.moderna.actors.Actor actor) {
        BrowseTheMobileApp ability = actor.getAbility(BrowseTheMobileApp.class);
        if (ability == null) {
            throw new IllegalStateException("El actor no tiene la habilidad BrowseTheMobileApp asignada");
        }
        return ability.driver();
    }
}
