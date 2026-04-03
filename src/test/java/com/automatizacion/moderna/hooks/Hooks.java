package com.automatizacion.moderna.hooks;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.abilities.ManageSession;
import com.automatizacion.moderna.abilities.TakeScreenshot;
import com.automatizacion.moderna.abilities.WaitForElements;
import com.automatizacion.moderna.config.FrameworkConfig;
import com.automatizacion.moderna.driver.AppiumServerManager;
import com.automatizacion.moderna.driver.DriverFactory;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.util.Collection;

/**
 * Hooks de ciclo de vida para pruebas mobile con Screenplay.
 * <p>
 * Detecta la plataforma desde los tags del escenario (@android / @ios).
 * Si no hay tag explicito, usa la plataforma por defecto de properties.
 * Cada hilo/escenario tiene su propio actor aislado via ThreadLocal,
 * habilitando ejecucion paralela de Android e iOS.
 */
public class Hooks {

    private static final ThreadLocal<Actor> ACTOR = new ThreadLocal<>();

    public static Actor currentActor() {
        return ACTOR.get();
    }

    @Before(order = 1, value = "@mobile")
    public void beforeMobileScenario(Scenario scenario) {
        boolean runMobile = Boolean.parseBoolean(System.getProperty("run.mobile.tests", "true"));
        if (!runMobile) {
            throw new IllegalStateException("Pruebas mobile deshabilitadas (-Drun.mobile.tests=false)");
        }

        String platform = detectPlatform(scenario.getSourceTagNames());
        String env = FrameworkConfig.getInstance().getActiveEnv();

        logInfo("beforeScenario platform=" + platform + " env=" + env
            + " scenario=" + scenario.getName() + " thread=" + Thread.currentThread().getName());

        try {
            AppiumServerManager.ensureRunning(FrameworkConfig.getInstance().get("appium.server.url"));
            AppiumDriver driver = DriverFactory.createDriver(platform);
            Actor actor = new Actor("MobileUser");
            actor.can(BrowseTheMobileApp.with(driver));
            actor.can(WaitForElements.with(driver));
            actor.can(ManageSession.with(driver));
            actor.can(TakeScreenshot.with(driver));
            ACTOR.set(actor);
            logInfo("Actor inicializado sessionId=" + driver.getSessionId());
        } catch (RuntimeException e) {
            logError("Fallo creando driver: " + e.getMessage());
            throw e;
        }
    }

    @Before(order = 2, value = "not @mobile")
    public void beforeNonMobileScenario(Scenario scenario) {
        Actor actor = new Actor("FrameworkUser");
        ACTOR.set(actor);
        logInfo("Actor no-mobile inicializado scenario=" + scenario.getName());
    }

    @After(order = 1)
    public void attachFailureDetails(Scenario scenario) {
        if (!scenario.isFailed()) return;
        logWarn("Escenario fallido: " + scenario.getName());
        Allure.addAttachment("scenario-name", "text/plain", scenario.getName(), ".txt");
        Allure.addAttachment("scenario-tags", "text/plain",
            String.join(",", scenario.getSourceTagNames()), ".txt");
    }

    @After(order = 0, value = "@mobile")
    public void afterMobileScenario() {
        Actor actor = ACTOR.get();
        if (actor != null && actor.hasAbility(BrowseTheMobileApp.class)) {
            AppiumDriver driver = BrowseTheMobileApp.as(actor);
            logInfo("Cerrando sesion sessionId=" + driver.getSessionId());
            driver.quit();
        }
        ACTOR.remove();
    }

    @After(order = 0, value = "not @mobile")
    public void afterNonMobileScenario() {
        ACTOR.remove();
    }

    @After(order = -1, value = "@framework-health")
    public void stopAppiumAfterFrameworkHealth() {
        if (AppiumServerManager.startedByFramework()) {
            AppiumServerManager.stop();
        }
    }

    /**
     * Detecta la plataforma desde los tags del escenario.
     * Prioridad: @android o @ios en el feature > platform.target en properties.
     * Esto habilita paralelismo: un feature con @android corre Android,
     * otro con @ios corre iOS, cada uno con su driver independiente.
     */
    private static String detectPlatform(Collection<String> tags) {
        for (String tag : tags) {
            if ("@android".equalsIgnoreCase(tag)) return "android";
            if ("@ios".equalsIgnoreCase(tag)) return "ios";
        }
        return FrameworkConfig.getInstance().getDefaultPlatform();
    }

    private static void logInfo(String msg) {
        System.out.println("[HOOKS][INFO] " + msg);
    }

    private static void logWarn(String msg) {
        System.out.println("[HOOKS][WARN] " + msg);
    }

    private static void logError(String msg) {
        System.out.println("[HOOKS][ERROR] " + msg);
    }
}
