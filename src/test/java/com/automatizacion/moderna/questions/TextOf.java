package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.abilities.WaitForElements;
import com.automatizacion.moderna.screenplay.Question;
import java.time.Duration;
import org.openqa.selenium.WebElement;

public final class TextOf implements Question<String> {

    private final WebElement[] elements;
    private final Duration timeout;

    private TextOf(Duration timeout, WebElement... elements) {
        this.elements = elements;
        this.timeout = timeout;
    }

    public static TextOf firstPresent(WebElement... elements) {
        return new TextOf(null, elements);
    }

    public static TextOf firstPresent(Duration timeout, WebElement... elements) {
        return new TextOf(timeout, elements);
    }

    @Override
    public String answeredBy(Actor actor) {
        WaitForElements waits = WaitForElements.as(actor);
        for (WebElement element : elements) {
            if (element == null) {
                continue;
            }
            try {
                waits.untilVisible(element, timeout);
                String text = element.getText();
                if (text != null && !text.isBlank()) {
                    return text.trim();
                }
            } catch (RuntimeException ignored) {
                // intenta el siguiente elemento
            }
        }
        return "";
    }
}


