package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.abilities.ManageSession;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.screenplay.Question;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.appmanagement.ApplicationState;

/**
 * Pregunta Screenplay que consulta el estado de lanzamiento de una app,
 * dado su identificador (package/bundle id).
 */
public final class EstadoLanzamientoApp implements Question<ApplicationState> {

    private final String appId;

    private EstadoLanzamientoApp(String appId) {
        this.appId = appId;
    }

    public static EstadoLanzamientoApp para(String appId) {
        return new EstadoLanzamientoApp(appId);
    }

    @Override
    public ApplicationState answeredBy(Actor actor) {
        AppiumDriver driver = actor.getAbility(ManageSession.class).getDriver();
        return ((InteractsWithApps) driver).queryAppState(appId);
    }
}
