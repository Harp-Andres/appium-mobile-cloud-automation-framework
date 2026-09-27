package com.automatizacion.moderna.steps;

import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.hooks.Hooks;
import com.automatizacion.moderna.interactions.WaitUntil;
import com.automatizacion.moderna.questions.ElementIsVisible;
import com.automatizacion.moderna.questions.TextOfElement;
import com.automatizacion.moderna.screenplay.Ensure;
import com.automatizacion.moderna.tasks.LoginTheAppTask;
import com.automatizacion.moderna.tasks.OpenLoginScreenTask;
import com.automatizacion.moderna.ui.TheAppHomeUI;
import com.automatizacion.moderna.ui.TheAppLoginUI;
import com.automatizacion.moderna.ui.TheAppSecretUI;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TheAppSteps {

    private Actor actor() {
        return Hooks.currentActor();
    }

    @Given("the user is on TheApp home")
    public void userIsOnTheAppHome() {
        AppiumDriver driver = BrowseTheMobileApp.as(actor());
        TheAppHomeUI home = new TheAppHomeUI(driver);

        actor().attemptsTo(WaitUntil.the(home.LOGIN_SCREEN_ENTRY).isVisible());
        Ensure.that(actor())
            .asksFor(ElementIsVisible.of(home.LOGIN_SCREEN_ENTRY))
            .as("TheApp home should list the Login Screen entry")
            .isTrue();
    }

    @When("the user opens the Login Screen")
    public void userOpensLoginScreen() {
        actor().attemptsTo(OpenLoginScreenTask.fromHome());
    }

    @Then("the login form should be visible")
    public void loginFormVisible() {
        AppiumDriver driver = BrowseTheMobileApp.as(actor());
        TheAppLoginUI login = new TheAppLoginUI(driver);

        Ensure.that(actor())
            .asksFor(ElementIsVisible.of(login.USERNAME))
            .as("Username field should be visible on the login screen")
            .isTrue();
        Ensure.that(actor())
            .asksFor(ElementIsVisible.of(login.LOGIN_BUTTON))
            .as("Login button should be visible on the login screen")
            .isTrue();
    }

    @When("the user logs in with username {string} and password {string}")
    public void userLogsIn(String username, String password) {
        actor().attemptsTo(LoginTheAppTask.with(username, password));
    }

    @Then("the secret area should show user {string}")
    public void secretAreaShowsUser(String expectedUser) {
        AppiumDriver driver = BrowseTheMobileApp.as(actor());
        TheAppSecretUI secret = new TheAppSecretUI(driver);

        actor().attemptsTo(WaitUntil.the(secret.LOGGED_IN_MESSAGE).isVisible());
        String message = actor().asksFor(TextOfElement.of(secret.LOGGED_IN_MESSAGE));
        Ensure.that(message)
            .as("Secret screen should confirm login for %s", expectedUser)
            .contains("You are logged in as " + expectedUser);
    }
}
