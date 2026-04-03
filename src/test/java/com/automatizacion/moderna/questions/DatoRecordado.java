package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.screenplay.Question;

public final class DatoRecordado<T> implements Question<T> {

    private final String key;
    private final Class<T> type;

    private DatoRecordado(String key, Class<T> type) {
        this.key = key;
        this.type = type;
    }

    public static <T> DatoRecordado<T> de(String key, Class<T> type) {
        return new DatoRecordado<>(key, type);
    }

    @Override
    public T answeredBy(Actor actor) {
        return actor.recall(key, type);
    }
}

