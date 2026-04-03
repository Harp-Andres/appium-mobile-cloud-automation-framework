package com.automatizacion.moderna.interactions;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.screenplay.Performable;
import org.openqa.selenium.WebElement;

public final class ClickOn implements Performable {

    private final WebElement element;

    private ClickOn(WebElement element) {
        this.element = element;
    }

    public static ClickOn element(WebElement element) {
        return new ClickOn(element);
    }

    @Override
    public void performAs(Actor actor) {
        try {
            actor.attemptsTo(WaitUntil.the(element).isVisibleAndClickable());
            element.click();
        } catch (RuntimeException failure) {
            actor.attemptsTo(
                ScrollToTarget.downTo(element),
                WaitUntil.the(element).isVisibleAndClickable()
            );
            element.click();
        }
    }
}
