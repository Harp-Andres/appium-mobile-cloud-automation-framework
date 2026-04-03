package com.automatizacion.moderna.screenplay;

import com.automatizacion.moderna.actors.Actor;

@FunctionalInterface
public interface Performable {
    void performAs(Actor actor);
}

