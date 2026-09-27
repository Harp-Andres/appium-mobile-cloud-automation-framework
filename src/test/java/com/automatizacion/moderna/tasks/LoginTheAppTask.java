package com.automatizacion.moderna.tasks;

import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.interactions.ClickOn;
import com.automatizacion.moderna.interactions.WaitUntil;
import com.automatizacion.moderna.screenplay.Task;
import com.automatizacion.moderna.ui.TheAppLoginUI;
import io.appium.java_client.AppiumDriver;

/**
 * Fills credentials and submits the TheApp login form.
 */
public final class LoginTheAppTask implements Task {

    private final String username;
    private final String password;

    private LoginTheAppTask(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static LoginTheAppTask with(String username, String password) {
        return new LoginTheAppTask(username, password);
    }

    @Override
    public void performAs(Actor actor) {
        AppiumDriver driver = BrowseTheMobileApp.as(actor);
        TheAppLoginUI login = new TheAppLoginUI(driver);

        actor.attemptsTo(WaitUntil.the(login.USERNAME).isVisible());
        login.USERNAME.clear();
        login.USERNAME.sendKeys(username);
        login.PASSWORD.clear();
        login.PASSWORD.sendKeys(password);
        actor.attemptsTo(ClickOn.element(login.LOGIN_BUTTON));
    }
}
