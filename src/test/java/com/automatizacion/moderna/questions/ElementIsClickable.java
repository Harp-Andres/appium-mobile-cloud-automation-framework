package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.abilities.WaitForElements;
import com.automatizacion.moderna.screenplay.Question;
import java.time.Duration;
import org.openqa.selenium.WebElement;

public final class ElementIsClickable implements Question<Boolean> {

    private final WebElement element;
    private final Duration timeout;

    private ElementIsClickable(WebElement element, Duration timeout) {
        this.element = element;
        this.timeout = timeout;
    }

    public static ElementIsClickable of(WebElement element) {
        return new ElementIsClickable(element, null);
    }

    public static ElementIsClickable of(WebElement element, Duration timeout) {
        return new ElementIsClickable(element, timeout);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        try {
            WaitForElements.as(actor).untilClickable(element, timeout);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}

