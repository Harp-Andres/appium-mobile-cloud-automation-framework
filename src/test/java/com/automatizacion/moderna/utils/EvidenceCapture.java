package com.automatizacion.moderna.utils;

import com.automatizacion.moderna.abilities.TakeScreenshot;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Utilidades profesionales para capturar evidencias (screenshots, logs, video) en Appium bajo Screenplay.
 * Solo se permite evidencia vía habilidades Screenplay (TakeScreenshot).
 */
public class EvidenceCapture {

    private static final String SCREENSHOTS_DIR = "target/screenshots";
    private static final String VIDEOS_DIR = "target/videos";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    static {
        File screenshotsDir = new File(SCREENSHOTS_DIR);
        if (!screenshotsDir.exists() && !screenshotsDir.mkdirs()) {
            System.out.println("[EVIDENCE][WARN] No se pudo crear el directorio de screenshots: " + SCREENSHOTS_DIR);
        }
        File videosDir = new File(VIDEOS_DIR);
        if (!videosDir.exists() && !videosDir.mkdirs()) {
            System.out.println("[EVIDENCE][WARN] No se pudo crear el directorio de videos: " + VIDEOS_DIR);
        }
    }

    /**
     * Adjunta un screenshot usando la habilidad TakeScreenshot.
     * Si se provee Scenario, también lo adjunta al reporte de Cucumber.
     * @param takeScreenshot habilidad Screenplay para capturar la pantalla
     * @param attachmentName nombre de la evidencia
     * @param saveToDisk si true, guarda la imagen en disco
     * @param scenario escenario de Cucumber (puede ser null)
     */
    public static void attachScreenshot(TakeScreenshot takeScreenshot, String attachmentName, boolean saveToDisk, Scenario scenario) {
        if (takeScreenshot == null) {
            System.out.println("[EVIDENCE] No se puede capturar screenshot: habilidad TakeScreenshot es null");
            return;
        }
        try {
            byte[] screenshot = takeScreenshot.capture();
            String safeName = sanitizeName(attachmentName);

            if (saveToDisk) {
                String timestamp = LocalDateTime.now().format(FORMATTER);
                String filepath = SCREENSHOTS_DIR + "/" + safeName + "_" + timestamp + ".png";
                try (FileOutputStream fos = new FileOutputStream(filepath)) {
                    fos.write(screenshot);
                }
                System.out.println("[EVIDENCE] Screenshot guardado: " + filepath);
            }

            Allure.getLifecycle().addAttachment(
                attachmentName,
                "image/png",
                "png",
                screenshot
            );
            System.out.println("[EVIDENCE] Screenshot adjuntado a Allure: " + attachmentName);

            if (scenario != null) {
                scenario.attach(screenshot, "image/png", attachmentName);
                System.out.println("[EVIDENCE] Screenshot adjuntado a Cucumber: " + attachmentName);
            }
        } catch (Exception e) {
            System.out.println("[EVIDENCE] Error al capturar screenshot: " + e.getMessage());
        }
    }

    /**
     * Adjunta un video en base64 a Allure y opcionalmente lo guarda en disco.
     * @param base64Video video en base64
     * @param attachmentName nombre de la evidencia
     * @param saveToDisk si true, guarda el video en disco
     */
    public static void attachVideoFromBase64(String base64Video, String attachmentName, boolean saveToDisk) {
        if (base64Video == null || base64Video.isBlank()) {
            System.out.println("[EVIDENCE] Video vacio, no se adjunta");
            return;
        }
        try {
            byte[] videoBytes = Base64.getDecoder().decode(base64Video);
            String safeName = sanitizeName(attachmentName);

            if (saveToDisk) {
                String timestamp = LocalDateTime.now().format(FORMATTER);
                String filepath = VIDEOS_DIR + "/" + safeName + "_" + timestamp + ".mp4";
                try (FileOutputStream fos = new FileOutputStream(filepath)) {
                    fos.write(videoBytes);
                }
                System.out.println("[EVIDENCE] Video guardado: " + filepath);
            }

            Allure.getLifecycle().addAttachment(
                attachmentName,
                "video/mp4",
                "mp4",
                videoBytes
            );
            System.out.println("[EVIDENCE] Video adjuntado a Allure: " + attachmentName);
        } catch (Exception e) {
            System.out.println("[EVIDENCE] Error al adjuntar video: " + e.getMessage());
        }
    }

    /**
     * Adjunta un log de texto a Allure.
     * @param title título del log
     * @param content contenido del log
     */
    public static void attachLog(String title, String content) {
        try {
            Allure.addAttachment(title, "text/plain", content, "txt");
            System.out.println("[EVIDENCE] Log adjuntado: " + title);
        } catch (Exception e) {
            System.out.println("[EVIDENCE] Error al adjuntar log: " + e.getMessage());
        }
    }

    /**
     * Adjunta información relevante del driver a Allure.
     * @param driver driver de Appium
     */
    public static void attachDriverInfo(AppiumDriver driver) {
        try {
            String driverInfo = "Session ID: " + driver.getSessionId() + "\n"
                + "Platform: " + driver.getCapabilities().getCapability("platformName") + "\n"
                + "Device: " + driver.getCapabilities().getCapability("deviceName") + "\n"
                + "App: " + driver.getCapabilities().getCapability("app");
            attachLog("Driver Info", driverInfo);
        } catch (Exception e) {
            System.out.println("[EVIDENCE] Error al adjuntar driver info: " + e.getMessage());
        }
    }

    /**
     * Sanitiza el nombre de la evidencia para uso en archivos.
     */
    private static String sanitizeName(String raw) {
        return raw == null ? "evidence" : raw.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}
