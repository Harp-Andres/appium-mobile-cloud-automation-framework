package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.abilities.WaitForElements;
import com.automatizacion.moderna.screenplay.Question;
import org.openqa.selenium.WebElement;

/**
 * Question simple: devuelve el texto visible de un unico elemento.
 * No realiza aserciones, solo responde la pregunta.
 */
public final class TextOfElement implements Question<String> {

    private final WebElement element;

    private TextOfElement(WebElement element) {
        this.element = element;
    }

    public static TextOfElement of(WebElement element) {
        return new TextOfElement(element);
    }

    @Override
    public String answeredBy(Actor actor) {
        if (element == null) {
            return "";
        }

        try {
            WaitForElements.as(actor).untilVisible(element);
            String text = element.getText();
            return text == null ? "" : text.trim();
        } catch (RuntimeException ignored) {
            return "";
        }
    }
}

