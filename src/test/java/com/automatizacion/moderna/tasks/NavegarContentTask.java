package com.automatizacion.moderna.tasks;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.interactions.ClickOn;
import com.automatizacion.moderna.screenplay.Task;
import com.automatizacion.moderna.ui.ContentUI;
import com.automatizacion.moderna.ui.HomePageUI;

/**
 * Tarea que navega hasta la sección "Read Asset" dentro de la aplicación.
 * <p>
 * Al implementar {@link Task}, el compilador obliga a sobreescribir
 * {@link #performAs(Actor)}, siguiendo el patrón Screenplay de Serenity BDD.
 * </p>
 */
public final class NavegarContentTask implements Task {

    private NavegarContentTask() {
    }

    /**
     * Método de fábrica estático (fluent API).
     *
     * @return nueva instancia de {@link NavegarContentTask}
     */
    public static NavegarContentTask hastaReadAsset() {
        return new NavegarContentTask();
    }

    @Override
    public void performAs(Actor actor) {
        actor.attemptsTo(
            ClickOn.element(HomePageUI.PRINCIPLA_MENU),
            ClickOn.element(ContentUI.LABEL_ASSETS),
            ClickOn.element(ContentUI.LABEL_READ_ASSET)
        );
    }
}
