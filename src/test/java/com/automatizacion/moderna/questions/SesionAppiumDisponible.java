package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.screenplay.Question;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.SessionNotCreatedException;

/**
 * Pregunta Screenplay que devuelve el ID de la sesión Appium activa.
 * <p>
 * Encapsula la verificación de que el actor posee la habilidad
 * {@link BrowseTheMobileApp} y que el driver tiene una sesión válida,
 * dejando las aserciones a cargo del step de Cucumber.
 * </p>
 *
 * <pre>
 * // Uso en steps:
 * String sessionId = actor.asksFor(SesionAppiumDisponible.ahora());
 * assertThat(sessionId).isNotBlank();
 * </pre>
 */
public final class SesionAppiumDisponible implements Question<String> {

    private SesionAppiumDisponible() {
    }

    /** Método de fábrica estático (fluent API). */
    public static SesionAppiumDisponible ahora() {
        return new SesionAppiumDisponible();
    }

    @Override
    public String answeredBy(Actor actor) {
        if (!actor.hasAbility(BrowseTheMobileApp.class)) {
            throw new IllegalStateException(
                "El actor '" + actor.getName() + "' no tiene la habilidad BrowseTheMobileApp asignada"
            );
        }
        AppiumDriver driver = BrowseTheMobileApp.as(actor);
        if (driver == null) {
            throw new SessionNotCreatedException("El driver Appium no fue inicializado por los Hooks");
        }
        return driver.getSessionId() != null ? driver.getSessionId().toString() : null;
    }
}

