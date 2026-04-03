package com.automatizacion.moderna.screenplay;
import com.automatizacion.moderna.actors.Actor;
@FunctionalInterface
public interface Question<T> {
    T answeredBy(Actor actor);
}
