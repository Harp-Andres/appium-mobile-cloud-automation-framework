package com.automatizacion.moderna.steps;

import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.hooks.Hooks;
import com.automatizacion.moderna.interactions.WaitUntil;
import com.automatizacion.moderna.questions.ElementIsVisible;
import com.automatizacion.moderna.questions.SubtituloContenido;
import com.automatizacion.moderna.questions.TituloPantalla;
import com.automatizacion.moderna.screenplay.Ensure;
import com.automatizacion.moderna.tasks.NavegarContentTask;
import com.automatizacion.moderna.ui.HomePageUI;
import com.automatizacion.moderna.utils.NormalizadorTexto;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ApiDemosSteps {

    private Actor actor() {
        return Hooks.currentActor();
    }

    @Given("the user is on the application home page")
    public void userIsOnHomePage() {
        AppiumDriver driver = BrowseTheMobileApp.as(actor());
        HomePageUI home = new HomePageUI(driver);

        actor().attemptsTo(WaitUntil.the(home.HOME_TITLE).isVisible());
        Ensure.that(actor())
            .asksFor(ElementIsVisible.of(home.HOME_TITLE))
            .as("El título principal de Home debe estar visible")
            .isTrue();
    }

    @When("the user navigates to Content\\/Assets\\/Read Asset")
    public void userNavigatesTo() {
        actor().attemptsTo(NavegarContentTask.hastaReadAsset());
    }

    @Then("the screen title should be {string}")
    public void verifyScreenTitle(String expectedTitle) {
        Ensure.that(actor())
            .asksFor(TituloPantalla.ahora())
            .as("El título de pantalla debe ser '%s'", expectedTitle)
            .isEqualTo(expectedTitle);
    }

    @Then("the subtitle should be {string}")
    public void verifySubtitle(String expectedSubtitle) {
        String normalizedExpected = NormalizadorTexto.paraComparacionFlexible(expectedSubtitle);
        String normalizedActual = NormalizadorTexto.paraComparacionFlexible(actor().asksFor(SubtituloContenido.ahora()));

        Ensure.that(normalizedActual)
            .as("El subtítulo debe contener '%s'", expectedSubtitle)
            .contains(normalizedExpected);
    }
}
