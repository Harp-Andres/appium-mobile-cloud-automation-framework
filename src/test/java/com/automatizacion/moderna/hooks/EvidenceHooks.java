package com.automatizacion.moderna.hooks;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.abilities.TakeScreenshot;
import com.automatizacion.moderna.utils.EvidenceCapture;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Hooks profesionales para la gestión de evidencias en Cucumber con Screenplay.
 * - Captura video y screenshots de forma automática y robusta.
 * - Adjunta evidencia a Allure y Cucumber.
 * - Cumple con SOLID y POO, sin lógica legacy ni redundante.
 */
public class EvidenceHooks {

    /**
     * Obtiene el actor actual de forma robusta.
     */
    private Actor currentActor() {
        return Hooks.currentActor();
    }

    /**
     * Obtiene la habilidad TakeScreenshot del actor actual, si existe.
     */
    private TakeScreenshot currentTakeScreenshotAbility() {
        Actor actor = currentActor();
        return (actor != null && actor.hasAbility(TakeScreenshot.class)) ? actor.getAbility(TakeScreenshot.class) : null;
    }

    /**
     * Obtiene el driver de Appium desde la habilidad BrowseTheMobileApp del actor.
     */
    private AppiumDriver currentDriver() {
        Actor actor = currentActor();
        if (actor != null && actor.hasAbility(BrowseTheMobileApp.class)) {
            return (AppiumDriver) BrowseTheMobileApp.as(actor);
        }
        return null;
    }

    /**
     * Inicia la grabación de video antes del escenario si está habilitado.
     */
    @Before(order = 10, value = "@mobile")
    public void beforeMobileScenario(Scenario scenario) {
        System.out.println("[EVIDENCE] Iniciando escenario: " + scenario.getName());
        boolean videoEnabled = com.automatizacion.moderna.utils.EvidenceConfig.getBoolean("evidence.video.enabled", false);
        if (!videoEnabled) return;
        AppiumDriver driver = currentDriver();
        if (driver instanceof CanRecordScreen recorder) {
            try {
                recorder.startRecordingScreen();
                System.out.println("[EVIDENCE] Grabación de video iniciada");
            } catch (Exception e) {
                System.out.println("[EVIDENCE] No se pudo iniciar grabación: " + e.getMessage());
            }
        }
    }

    /**
     * Captura evidencia tras cada step usando la habilidad TakeScreenshot del actor.
     * Siempre adjunta a Allure y Cucumber. Si el step falla, la evidencia queda marcada.
     */
    @AfterStep(value = "@mobile")
    public void afterStep(Scenario scenario) {
        TakeScreenshot takeScreenshot = currentTakeScreenshotAbility();
        if (takeScreenshot != null) {
            String label = (scenario.isFailed() ? "FAILED_STEP_" : "STEP_") + scenario.getName();
            EvidenceCapture.attachScreenshot(takeScreenshot, label, scenario.isFailed(), scenario);
        }
    }

    /**
     * Al finalizar el escenario, toma evidencia final (screenshot y video) y adjunta info del driver.
     */
    @After(order = 100, value = "@mobile")
    public void afterMobileScenario(Scenario scenario) {
        TakeScreenshot takeScreenshot = currentTakeScreenshotAbility();
        AppiumDriver driver = currentDriver();
        boolean screenshotOnPass = Boolean.parseBoolean(System.getProperty("evidence.screenshot.on.pass", "true"));
        boolean takeFinalScreenshot = scenario.isFailed() || screenshotOnPass;
        if (takeFinalScreenshot && takeScreenshot != null) {
            String label = scenario.isFailed() ? "FAILED_FINAL_SCREEN" : "PASSED_FINAL_SCREEN";
            EvidenceCapture.attachScreenshot(takeScreenshot, label + "_" + scenario.getName(), scenario.isFailed(), scenario);
        }
        if (scenario.isFailed()) {
            System.out.println("[EVIDENCE] Escenario fallido: " + scenario.getName());
        } else {
            System.out.println("[EVIDENCE] Escenario exitoso: " + scenario.getName());
        }
        boolean videoEnabled = com.automatizacion.moderna.utils.EvidenceConfig.getBoolean("evidence.video.enabled", false);
        if (videoEnabled && driver instanceof CanRecordScreen recorder) {
            try {
                String base64Video = recorder.stopRecordingScreen();
                String videoName = (scenario.isFailed() ? "FAILED_VIDEO_" : "PASSED_VIDEO_") + scenario.getName();
                EvidenceCapture.attachVideoFromBase64(base64Video, videoName, scenario.isFailed());
            } catch (Exception e) {
                System.out.println("[EVIDENCE] No se pudo detener/adjuntar video: " + e.getMessage());
            }
        }
        if (driver != null) {
            EvidenceCapture.attachDriverInfo(driver);
        }
    }
}
