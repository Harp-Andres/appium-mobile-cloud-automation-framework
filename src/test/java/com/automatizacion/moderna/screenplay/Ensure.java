package com.automatizacion.moderna.screenplay;

import com.automatizacion.moderna.actors.Actor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;

/**
 * DSL de aserciones estilo Screenplay, inspirado en Serenity Ensure.
 * Mantiene separada la obtencion de datos (Question) de la validacion.
 */
public final class Ensure {

    private Ensure() {
    }

    public static ActorEnsure that(Actor actor) {
        return new ActorEnsure(actor);
    }

    public static <T> ValueEnsure<T> that(T actual) {
        return new ValueEnsure<>(actual);
    }

    public static AssumptionEnsure assumeThat(boolean actual) {
        return new AssumptionEnsure(actual);
    }

    public static final class ActorEnsure {
        private final Actor actor;

        private ActorEnsure(Actor actor) {
            this.actor = actor;
        }

        public <T> ValueEnsure<T> asksFor(Question<T> question) {
            return Ensure.that(actor.asksFor(question));
        }
    }

    public static class ValueEnsure<T> {
        private final T actual;
        private String description;
        private Object[] args = new Object[0];

        private ValueEnsure(T actual) {
            this.actual = actual;
        }

        public ValueEnsure<T> as(String description, Object... args) {
            this.description = description;
            this.args = args == null ? new Object[0] : args;
            return this;
        }

        public void isEqualTo(Object expected) {
            Assertions.assertThat(actual).as(description, args).isEqualTo(expected);
        }

        public void isTrue() {
            Assertions.assertThat(actual).as(description, args).isEqualTo(Boolean.TRUE);
        }

        public void isNotBlank() {
            Assertions.assertThat((CharSequence) actual).as(description, args).isNotBlank();
        }

        @SafeVarargs
        public final void isIn(T... values) {
            Assertions.assertThat(actual).as(description, args).isIn((Object[]) values);
        }

        public void contains(String substring) {
            Assertions.assertThat((CharSequence) actual).as(description, args).contains(substring);
        }
    }

    public static final class AssumptionEnsure {
        private final boolean actual;
        private String description;
        private Object[] args = new Object[0];

        private AssumptionEnsure(boolean actual) {
            this.actual = actual;
        }

        public AssumptionEnsure as(String description, Object... args) {
            this.description = description;
            this.args = args == null ? new Object[0] : args;
            return this;
        }

        public void isTrue() {
            Assumptions.assumeThat(actual).as(description, args).isTrue();
        }
    }
}

