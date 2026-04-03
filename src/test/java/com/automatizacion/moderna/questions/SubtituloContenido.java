package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.screenplay.Question;
import com.automatizacion.moderna.ui.ContentUI;

/**
 * Pregunta Screenplay que devuelve el texto del subtítulo o contenido de la pantalla activa.
 * Encapsula la construcción de {@link ContentUI} a través de la habilidad del actor.
 *
 * <pre>
 * Ensure.that(actor())
 *     .asksFor(SubtituloContenido.ahora())
 *     .as("El subtítulo debe contener '%s'", expected)
 *     .contains(expected);
 * </pre>
 */
public final class SubtituloContenido implements Question<String> {

    private SubtituloContenido() {
    }

    public static SubtituloContenido ahora() {
        return new SubtituloContenido();
    }

    @Override
    public String answeredBy(Actor actor) {
        ContentUI ui = new ContentUI(BrowseTheMobileApp.as(actor));
        return actor.asksFor(TextOfElement.of(ui.READ_ASSET_CONTENT));
    }
}

