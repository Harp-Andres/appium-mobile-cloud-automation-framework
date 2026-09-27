package com.automatizacion.moderna.tasks;

import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.interactions.ClickOn;
import com.automatizacion.moderna.screenplay.Task;
import com.automatizacion.moderna.ui.TheAppHomeUI;
import io.appium.java_client.AppiumDriver;

/**
 * Opens the Login Screen from TheApp home menu.
 */
public final class OpenLoginScreenTask implements Task {

    private OpenLoginScreenTask() {
    }

    public static OpenLoginScreenTask fromHome() {
        return new OpenLoginScreenTask();
    }

    @Override
    public void performAs(Actor actor) {
        AppiumDriver driver = BrowseTheMobileApp.as(actor);
        TheAppHomeUI home = new TheAppHomeUI(driver);
        actor.attemptsTo(ClickOn.element(home.LOGIN_SCREEN_ENTRY));
    }
}
