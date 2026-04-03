package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.abilities.Ability;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.screenplay.Question;

/**
 * Pregunta Screenplay que responde si el actor tiene una habilidad específica asignada.
 *
 * <pre>
 * // Uso en steps:
 * assertThat(actor.asksFor(HabilidadAsignada.de(BrowseTheMobileApp.class)))
 *     .as("El actor no tiene la habilidad BrowseTheMobileApp")
 *     .isTrue();
 * </pre>
 *
 * @param <T> Tipo de habilidad a verificar
 */
public final class HabilidadAsignada<T extends Ability> implements Question<Boolean> {

    private final Class<T> habilidad;

    private HabilidadAsignada(Class<T> habilidad) {
        this.habilidad = habilidad;
    }

    /**
     * Método de fábrica estático (fluent API).
     *
     * @param habilidad clase de la habilidad a verificar
     * @param <T>       tipo de habilidad
     * @return nueva instancia de {@link HabilidadAsignada}
     */
    public static <T extends Ability> HabilidadAsignada<T> de(Class<T> habilidad) {
        return new HabilidadAsignada<>(habilidad);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        return actor.hasAbility(habilidad);
    }
}

