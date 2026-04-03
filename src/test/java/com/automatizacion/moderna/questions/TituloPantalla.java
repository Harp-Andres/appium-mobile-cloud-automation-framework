package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.screenplay.Question;
import com.automatizacion.moderna.ui.ContentUI;

/**
 * Pregunta Screenplay que devuelve el texto del título de pantalla visible.
 * Encapsula la construcción de {@link ContentUI} a través de la habilidad del actor.
 *
 * <pre>
 * Ensure.that(actor())
 *     .asksFor(TituloPantalla.ahora())
 *     .as("El título debe ser '%s'", expected)
 *     .isIn(expected, expectedLeaf);
 * </pre>
 */
public final class TituloPantalla implements Question<String> {

    private TituloPantalla() {
    }

    public static TituloPantalla ahora() {
        return new TituloPantalla();
    }

    @Override
    public String answeredBy(Actor actor) {
        ContentUI ui = new ContentUI(BrowseTheMobileApp.as(actor));
        return actor.asksFor(TextOf.firstPresent(ui.PATCH_TITLE));
    }
}

