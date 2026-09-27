package com.automatizacion.moderna.driver;

import com.automatizacion.moderna.config.FrameworkConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona el ciclo de vida del servidor Appium local.
 * <p>
 * Si {@code appium.auto.start=true} y el servidor no responde,
 * lo levanta via proceso del sistema. Al terminar la JVM,
 * un shutdown hook lo detiene automáticamente.
 * <p>
 * No actúa sobre URLs de BrowserStack ni entornos remotos.
 */
public final class AppiumServerManager {

    private static final Logger log = LoggerFactory.getLogger(AppiumServerManager.class);
    private static final int STARTUP_WAIT_SECONDS = 30;
    private static final int STATUS_CHECK_INTERVAL_MS = 1000;

    private static final AtomicBoolean STARTED_BY_FRAMEWORK = new AtomicBoolean(false);
    private static volatile Process serverProcess = null;

    private AppiumServerManager() {
    }

    /**
     * Verifica si Appium está corriendo; si no, lo levanta (solo cuando
     * {@code appium.auto.start=true} y la URL es local).
     *
     * @param serverUrl URL del servidor, ej: http://127.0.0.1:4723
     */
    public static synchronized void ensureRunning(String serverUrl) {
        if (isBrowserStack(serverUrl)) {
            logInfo("BrowserStack detectado — gestión del servidor omitida");
            return;
        }
        if (!isAutoStartEnabled()) {
            logInfo("appium.auto.start=false — gestión automática deshabilitada");
            return;
        }
        if (isReachable(serverUrl)) {
            logInfo("Servidor Appium ya disponible en " + serverUrl);
            return;
        }
        if (STARTED_BY_FRAMEWORK.compareAndSet(false, true)) {
            logInfo("Servidor Appium no disponible, iniciando proceso...");
            startServer(serverUrl);
            registerShutdownHook();
        }
    }

    /**
     * Detiene el proceso de Appium iniciado por el framework.
     * Es un no-op si el servidor fue iniciado externamente.
     */
    public static synchronized void stop() {
        if (serverProcess == null || !serverProcess.isAlive()) return;
        logInfo("Deteniendo servidor Appium (PID=" + serverProcess.pid() + ")...");
        ProcessHandle handle = serverProcess.toHandle();
        handle.descendants().forEach(ProcessHandle::destroyForcibly);
        handle.destroyForcibly();
        try {
            serverProcess.waitFor(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        serverProcess = null;
        STARTED_BY_FRAMEWORK.set(false);
        logInfo("Servidor Appium detenido");
    }

    /**
     * Retorna true cuando el endpoint /status responde correctamente.
     */
    public static boolean isRunning(String serverUrl) {
        return isReachable(serverUrl);
    }

    /**
     * Indica si el framework fue quien inició el proceso Appium actual.
     */
    public static boolean startedByFramework() {
        return STARTED_BY_FRAMEWORK.get();
    }

    // ─── Privados ─────────────────────────────────────────────────────────────

    private static boolean isAutoStartEnabled() {
        String value = FrameworkConfig.getInstance().getOrDefault("appium.auto.start", "false");
        return Boolean.parseBoolean(value);
    }

    private static boolean isReachable(String serverUrl) {
        try {
            URI statusUri = URI.create(serverUrl.replaceAll("/+$", "") + "/status");
            HttpRequest request = HttpRequest.newBuilder(statusUri)
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
            HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() < 400;
        } catch (Exception e) {
            return false;
        }
    }

    private static void startServer(String serverUrl) {
        try {
            URI uri = URI.create(serverUrl);
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 4723;

            ProcessBuilder pb = buildProcessFor(host, port);
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            serverProcess = pb.start();

            logInfo("Proceso Appium iniciado PID=" + serverProcess.pid()
                + " en " + host + ":" + port);
            waitUntilReady(serverUrl);
        } catch (IOException e) {
            STARTED_BY_FRAMEWORK.set(false);
            throw new IllegalStateException(
                "No se pudo iniciar Appium: " + e.getMessage()
                    + " — verifica que 'appium' esté instalado (npm install -g appium)", e);
        }
    }

    private static ProcessBuilder buildProcessFor(String host, int port) {
        String portStr = String.valueOf(port);
        boolean windows = System.getProperty("os.name", "").toLowerCase().contains("win");
        if (windows) {
            return new ProcessBuilder(
                "appium.cmd",
                "--address", host,
                "--port", portStr,
                "--log-level", "error"
            );
        }
        return new ProcessBuilder(
            "appium",
            "--address", host,
            "--port", portStr,
            "--log-level", "error"
        );
    }

    private static void waitUntilReady(String serverUrl) {
        logInfo("Esperando que Appium esté listo (máx " + STARTUP_WAIT_SECONDS + "s)...");
        for (int i = 1; i <= STARTUP_WAIT_SECONDS; i++) {
            if (isReachable(serverUrl)) {
                logInfo("Appium listo tras " + i + "s");
                return;
            }
            try {
                Thread.sleep(STATUS_CHECK_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        throw new IllegalStateException(
            "Appium no respondió en " + STARTUP_WAIT_SECONDS + "s. Revisa que esté instalado.");
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logInfo("JVM shutdown hook — deteniendo Appium...");
            stop();
        }, "appium-shutdown-hook"));
    }

    private static boolean isBrowserStack(String serverUrl) {
        return serverUrl != null && serverUrl.contains("browserstack");
    }

    private static void logInfo(String msg) {
        log.info(msg);
    }
}

