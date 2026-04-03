package com.automatizacion.moderna.actors;

import com.automatizacion.moderna.abilities.Ability;
import com.automatizacion.moderna.screenplay.Performable;
import com.automatizacion.moderna.screenplay.Question;
import java.util.HashMap;
import java.util.Map;

public class Actor {
    private final String name;
    private final Map<Class<?>, Ability> abilities = new HashMap<>();
    private final Map<String, Object> memory = new HashMap<>();

    public Actor(String name) {
        this.name = name;
    }

    /**
     * Devuelve el nombre del actor.
     */
    public String getName() {
        return name;
    }

    /**
     * Asigna una habilidad al actor.
     * @param ability Habilidad a asignar
     * @param <T> Tipo de habilidad
     */
    public <T extends Ability> void can(T ability) {
        abilities.put(ability.getClass(), ability);
    }

    /**
     * Obtiene la habilidad asignada al actor del tipo solicitado.
     * Lanza excepción si la habilidad no está asignada.
     * @param abilityClass Clase de la habilidad
     * @param <T> Tipo de habilidad
     * @return Instancia de la habilidad
     */
    public <T extends Ability> T getAbility(Class<T> abilityClass) {
        Ability ability = abilities.get(abilityClass);
        if (ability == null) {
            throw new IllegalStateException("El actor '" + name + "' no tiene la habilidad " + abilityClass.getSimpleName() + " asignada");
        }
        return abilityClass.cast(ability);
    }

    /**
     * Verifica si el actor tiene una habilidad asignada.
     * @param abilityClass Clase de la habilidad
     * @return true si la tiene, false si no
     */
    public boolean hasAbility(Class<? extends Ability> abilityClass) {
        return abilities.containsKey(abilityClass);
    }

    /** Guarda un dato en memoria del actor para el escenario actual. */
    public void remember(String key, Object value) {
        memory.put(key, value);
    }

    /** Recupera un dato tipado desde la memoria del actor. */
    public <T> T recall(String key, Class<T> type) {
        Object value = memory.get(key);
        if (value == null) {
            throw new IllegalStateException("No existe valor en memoria para la clave: " + key);
        }
        if (!type.isInstance(value)) {
            throw new IllegalStateException("La clave '" + key + "' no contiene un valor del tipo " + type.getSimpleName());
        }
        return type.cast(value);
    }

    /** Elimina un dato de memoria para evitar arrastre entre pasos. */
    public void forget(String key) {
        memory.remove(key);
    }

    public void attemptsTo(Performable... actions) {
        for (Performable action : actions) {
            action.performAs(this);
        }
    }

    public <T> T asksFor(Question<T> question) {
        return question.answeredBy(this);
    }
}
