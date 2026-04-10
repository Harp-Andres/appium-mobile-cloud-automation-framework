package com.automatizacion.moderna.tasks;

import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.interactions.ClickOn;
import com.automatizacion.moderna.screenplay.Task;
import com.automatizacion.moderna.ui.ContentUI;
import com.automatizacion.moderna.ui.HomePageUI;
import io.appium.java_client.AppiumDriver;

/**
 * Tarea que navega hasta la sección "Read Asset" dentro de la aplicación.
 * Los UI objects se construyen en performAs con el driver del actor,
 * garantizando que PageFactory inicialice los campos de instancia correctamente.
 */
public final class NavegarContentTask implements Task {

    private NavegarContentTask() {
    }

    /**
     * Método de fábrica estático (fluent API).
     *
     * @return nueva instancia de {@link NavegarContentTask}
     */
    public static NavegarContentTask hastaReadAsset() {
        return new NavegarContentTask();
    }

    @Override
    public void performAs(Actor actor) {
        AppiumDriver driver = BrowseTheMobileApp.as(actor);
        HomePageUI home = new HomePageUI(driver);
        ContentUI content = new ContentUI(driver);

        actor.attemptsTo(
            ClickOn.element(home.PRINCIPAL_MENU),
            ClickOn.element(content.LABEL_ASSETS),
            ClickOn.element(content.LABEL_READ_ASSET)
        );
    }
}
