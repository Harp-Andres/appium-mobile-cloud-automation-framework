package com.automatizacion.moderna.steps;

import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.abilities.ManageSession;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.hooks.Hooks;
import com.automatizacion.moderna.models.MobileSmokeMemoryKeys;
import com.automatizacion.moderna.questions.DatoRecordado;
import com.automatizacion.moderna.questions.EstadoLanzamientoApp;
import com.automatizacion.moderna.questions.HabilidadAsignada;
import com.automatizacion.moderna.questions.IdentificadorAppBajoPrueba;
import com.automatizacion.moderna.questions.SesionAppiumDisponible;
import com.automatizacion.moderna.screenplay.Ensure;
import io.appium.java_client.appmanagement.ApplicationState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MobileSmokeSteps {

    private Actor actor() {
        return Hooks.currentActor();
    }

    @Given("mobile execution is enabled")
    public void ejecucionMobileHabilitada() {
        boolean enabled = Boolean.parseBoolean(System.getProperty("run.mobile.tests", "true"));
        Ensure.that(enabled)
            .as("La ejecución mobile debe estar habilitada (-Drun.mobile.tests=true)")
            .isTrue();
    }

    @When("I initialize the Appium driver")
    public void inicializoDriverAppium() {
        Ensure.that(actor())
            .asksFor(HabilidadAsignada.de(BrowseTheMobileApp.class))
            .as("El actor no tiene la habilidad BrowseTheMobileApp")
            .isTrue();

        Ensure.that(actor())
            .asksFor(HabilidadAsignada.de(ManageSession.class))
            .as("El actor no tiene la habilidad ManageSession")
            .isTrue();

        Ensure.that(actor())
            .asksFor(SesionAppiumDisponible.ahora())
            .as("El driver no fue inicializado por los Hooks")
            .isNotBlank();
    }

    @When("I query the app launch state")
    public void consultoEstadoLanzamientoApp() {
        String appId = actor().asksFor(IdentificadorAppBajoPrueba.ahora());
        Ensure.that(appId)
            .as("Configura 'app.package' (Android) o 'bundle.id' (iOS) en las propiedades")
            .isNotBlank();

        actor().remember(MobileSmokeMemoryKeys.APP_ID, appId);
        actor().remember(MobileSmokeMemoryKeys.APP_STATE, actor().asksFor(EstadoLanzamientoApp.para(appId)));
    }

    @Then("the mobile session should be available")
    public void sesionMobileDisponible() {
        Ensure.that(actor())
            .asksFor(SesionAppiumDisponible.ahora())
            .as("El sessionId de Appium no debería estar vacío")
            .isNotBlank();
    }

    @Then("the app should be running in foreground")
    public void appCorriendoEnForeground() {
        ApplicationState appState = actor().asksFor(DatoRecordado.de(MobileSmokeMemoryKeys.APP_STATE, ApplicationState.class));
        Ensure.that(appState)
            .as("Se esperaba la app en foreground, pero el estado fue: %s", appState)
            .isEqualTo(ApplicationState.RUNNING_IN_FOREGROUND);

        actor().forget(MobileSmokeMemoryKeys.APP_ID);
        actor().forget(MobileSmokeMemoryKeys.APP_STATE);
    }
}
