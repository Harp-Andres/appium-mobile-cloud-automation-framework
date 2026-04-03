package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.abilities.WaitForElements;
import com.automatizacion.moderna.screenplay.Question;
import java.time.Duration;
import org.openqa.selenium.WebElement;

public final class ElementIsVisible implements Question<Boolean> {

    private final WebElement element;
    private final Duration timeout;

    private ElementIsVisible(WebElement element, Duration timeout) {
        this.element = element;
        this.timeout = timeout;
    }

    public static ElementIsVisible of(WebElement element) {
        return new ElementIsVisible(element, null);
    }

    public static ElementIsVisible of(WebElement element, Duration timeout) {
        return new ElementIsVisible(element, timeout);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        try {
            WaitForElements.as(actor).untilVisible(element, timeout);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}

