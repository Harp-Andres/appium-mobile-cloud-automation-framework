package com.automatizacion.moderna.interactions;

import com.automatizacion.moderna.abilities.WaitForElements;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.screenplay.Performable;
import java.time.Duration;
import org.openqa.selenium.WebElement;

/**
 * Esperas explicitas con sintaxis Screenplay similar a Serenity.
 * Uso: actor.attemptsTo(WaitUntil.the(element).isVisibleAndClickable());
 */
public final class WaitUntil {

    private WaitUntil() {
    }

    public static ConditionBuilder the(WebElement element) {
        return new ConditionBuilder(element, null);
    }

    public static ConditionBuilder the(WebElement element, Duration timeout) {
        return new ConditionBuilder(element, timeout);
    }

    public static final class ConditionBuilder {

        private final WebElement element;
        private final Duration timeout;

        private ConditionBuilder(WebElement element, Duration timeout) {
            if (element == null) {
                throw new IllegalArgumentException("El elemento no puede ser null");
            }
            this.element = element;
            this.timeout = timeout;
        }

        public Performable isVisible() {
            return actor -> waits(actor).untilVisible(element, timeout);
        }

        public Performable isClickable() {
            return actor -> waits(actor).untilClickable(element, timeout);
        }

        public Performable isVisibleAndClickable() {
            return actor -> {
                waits(actor).untilVisible(element, timeout);
                waits(actor).untilClickable(element, timeout);
            };
        }

        public Performable isInvisible() {
            return actor -> waits(actor).untilInvisible(element, timeout);
        }

        public Performable textContains(String expectedText) {
            return actor -> waits(actor).untilTextContains(element, expectedText, timeout);
        }

        public Performable attributeContains(String attribute, String expectedValue) {
            return actor -> waits(actor).untilAttributeContains(element, attribute, expectedValue, timeout);
        }

        private WaitForElements waits(Actor actor) {
            return WaitForElements.as(actor);
        }
    }
}

