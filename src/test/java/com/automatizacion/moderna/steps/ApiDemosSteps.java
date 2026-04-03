package com.automatizacion.moderna.steps;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.hooks.Hooks;
import com.automatizacion.moderna.interactions.WaitUntil;
import com.automatizacion.moderna.questions.ElementIsVisible;
import com.automatizacion.moderna.questions.SubtituloContenido;
import com.automatizacion.moderna.questions.TituloPantalla;
import com.automatizacion.moderna.screenplay.Ensure;
import com.automatizacion.moderna.tasks.NavegarContentTask;
import com.automatizacion.moderna.ui.HomePageUI;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ApiDemosSteps {

    private Actor actor() {
        return Hooks.currentActor();
    }

    @Given("the user is on the application home page")
    public void userIsOnHomePage() {
        actor().attemptsTo(WaitUntil.the(HomePageUI.HOME_TITLE).isVisible());
        Ensure.that(actor())
            .asksFor(ElementIsVisible.of(HomePageUI.HOME_TITLE))
            .as("El título principal de Home debe estar visible")
            .isTrue();
    }

    @When("the user navigates to Content\\/Assets\\/Read Asset")
    public void userNavigatesTo() {
        actor().attemptsTo(NavegarContentTask.hastaReadAsset());
    }

    @Then("the screen title should be {string}")
    public void verifyScreenTitle(String expectedTitle) {
        String expectedLeaf = expectedTitle.contains("/")
            ? expectedTitle.substring(expectedTitle.lastIndexOf('/') + 1).trim()
            : expectedTitle;

        Ensure.that(actor())
            .asksFor(TituloPantalla.ahora())
            .as("Titulo esperado '%s' (o '%s')", expectedTitle, expectedLeaf)
            .isIn(expectedTitle, expectedLeaf);
    }

    @Then("the subtitle should be {string}")
    public void verifySubtitle(String expectedSubtitle) {
        Ensure.that(actor())
            .asksFor(SubtituloContenido.ahora())
            .as("El subtítulo debe contener '%s'", expectedSubtitle)
            .contains(expectedSubtitle);
    }
}
