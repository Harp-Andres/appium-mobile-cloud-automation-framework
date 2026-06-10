package com.automatizacion.moderna.tests.unit;

import com.automatizacion.moderna.config.FrameworkConfig;
import com.automatizacion.moderna.driver.AppiumServerManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

class AppiumServerManagerUnitTest {

    @AfterEach
    void cleanUp() {
        AppiumServerManager.stop();
        System.clearProperty("ENV");
        System.clearProperty("appium.server.url");
        System.clearProperty("appium.auto.start");
        FrameworkConfig.reset();
    }

    @Test
    void shouldStartAndStopLocalAppiumServerWhenAutoStartEnabled() throws IOException {
        String executionEnv = resolveExecutionEnv();
        Assumptions.assumeTrue("local".equalsIgnoreCase(executionEnv),
            "Test de infraestructura local omitido porque ENV no es local. ENV actual: " + executionEnv);
        Assumptions.assumeTrue(isAppiumBinaryAvailable(),
            "Test de infraestructura omitido: el binario 'appium' no está disponible en PATH — instala con 'npm install -g appium'");

        int port = findFreePort();
        String serverUrl = "http://127.0.0.1:" + port;

        System.setProperty("ENV", "local");
        System.setProperty("appium.server.url", serverUrl);
        System.setProperty("appium.auto.start", "true");
        FrameworkConfig.reset();

        AppiumServerManager.ensureRunning(FrameworkConfig.getInstance().get("appium.server.url"));

        Assertions.assertTrue(AppiumServerManager.startedByFramework(),
            "El framework debería iniciar Appium cuando appium.auto.start=true y la URL es local");
        Assertions.assertTrue(AppiumServerManager.isRunning(serverUrl),
            "Appium debería responder en /status tras el arranque automático");

        AppiumServerManager.stop();

        Assertions.assertFalse(AppiumServerManager.startedByFramework(),
            "El estado interno debe volver a false después de detener Appium");
        Assertions.assertFalse(stillRespondsAfterShutdownGracePeriod(serverUrl),
            "Appium ya no debería responder una vez detenido por el framework");
    }

    @Test
    void shouldNotStartLocalProcessWhenServerIsBrowserStack() {
        System.setProperty("ENV", "browserstack");
        FrameworkConfig.reset();

        String serverUrl = FrameworkConfig.getInstance().get("appium.server.url");
        AppiumServerManager.ensureRunning(serverUrl);

        Assertions.assertFalse(AppiumServerManager.startedByFramework(),
            "Con BrowserStack no se debe levantar un proceso Appium local");
    }

    private int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        }
    }

    private boolean stillRespondsAfterShutdownGracePeriod(String serverUrl) {
        for (int attempt = 0; attempt < 30; attempt++) {
            if (!AppiumServerManager.isRunning(serverUrl)) {
                return false;
            }
            sleep(1000);
        }
        return true;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La espera del test fue interrumpida", e);
        }
    }

    private boolean isAppiumBinaryAvailable() {
        try {
            Process p = new ProcessBuilder("appium", "--version").start();
            boolean exited = p.waitFor(3, TimeUnit.SECONDS);
            p.destroyForcibly();
            return exited;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private String resolveExecutionEnv() {
        String fromProperty = System.getProperty("ENV");
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty.trim();
        }
        String fromEnvironment = System.getenv("ENV");
        if (fromEnvironment != null && !fromEnvironment.isBlank()) {
            return fromEnvironment.trim();
        }
        return "";
    }
}

