package com.automatizacion.moderna.driver;

import com.automatizacion.moderna.config.FrameworkConfig;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsabilidad unica: crear un {@link AppiumDriver} para la plataforma indicada.
 * <p>
 * Lee la configuracion directamente de {@link FrameworkConfig} usando el prefijo
 * de plataforma recibido como parametro. Esto permite que hilos paralelos
 * creen drivers de Android e iOS simultaneamente sin conflicto de estado.
 */
public final class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final int SESSION_RETRY_COUNT = 2;
    private static final long SESSION_RETRY_DELAY_MS = 1200;

    private DriverFactory() {
    }

    /**
     * Crea un AppiumDriver para la plataforma dada.
     *
     * @param platform "android" o "ios"
     * @return AppiumDriver listo para usar
     */
    public static AppiumDriver createDriver(String platform) {
        FrameworkConfig config = FrameworkConfig.getInstance();
        String serverUrlRaw = config.get("appium.server.url");
        URL serverUrl = buildServerUrl(serverUrlRaw);
        boolean browserStack = isBrowserStack(serverUrlRaw);
        long startTime = System.currentTimeMillis();

        logInfo("createDriver platform=" + platform + " serverUrl=" + sanitizeUrl(serverUrl)
            + " browserStack=" + browserStack);

        validateBrowserStackCredentials(serverUrl, browserStack);
        verifyServerReachability(serverUrlRaw);

        AppiumDriver driver = createSessionWithRetry(platform, serverUrl, config, browserStack);

        int timeout = Integer.parseInt(config.getOrDefault("new.command.timeout", "120"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        long elapsed = System.currentTimeMillis() - startTime;
        logInfo("createDriver OK sessionId=" + driver.getSessionId() + " elapsed=" + elapsed + "ms");
        return driver;
    }

    private static AppiumDriver createSessionWithRetry(String platform, URL serverUrl, FrameworkConfig config, boolean browserStack) {
        RuntimeException last = null;
        for (int attempt = 1; true; attempt++) {
            try {
                return switch (platform.toLowerCase()) {
                    case "android" -> new AndroidDriver(serverUrl, buildAndroidOptions(platform, config, browserStack));
                    case "ios" -> new IOSDriver(serverUrl, buildIosOptions(platform, config, browserStack));
                    default -> throw new IllegalArgumentException("Plataforma no soportada: " + platform);
                };
            } catch (RuntimeException e) {
                last = e;
                if (!isConnectionIssue(e) || attempt == SESSION_RETRY_COUNT) {
                    logError("Error creando driver: " + e.getMessage());
                    if (browserStack) {
                        logError("BrowserStack — Verifica: app, credenciales en URL, device.name, platform.version");
                    }
                    throw e;
                }
                logInfo("Reintentando creacion de sesion Appium (intento " + (attempt + 1) + "/" + SESSION_RETRY_COUNT + ")");
                sleepQuietly();
            }
        }
    }

    private static boolean isConnectionIssue(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof java.net.ConnectException || current instanceof java.io.UncheckedIOException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static void verifyServerReachability(String serverUrlRaw) {
        if (isBrowserStack(serverUrlRaw)) {
            return;
        }
        try {
            URI base = URI.create(serverUrlRaw);
            URI statusUri = URI.create(base.toString().replaceAll("/+$", "") + "/status");
            HttpRequest request = HttpRequest.newBuilder(statusUri)
                .timeout(Duration.ofSeconds(6))
                .GET()
                .build();
            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(4))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Appium /status respondio HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                "No se pudo alcanzar Appium en " + serverUrlRaw
                    + ". Verifica servidor activo y URL. Ejemplo: appium --address 127.0.0.1 --port 4723",
                e
            );
        }
    }

    private static void sleepQuietly() {
        try {
            Thread.sleep(DriverFactory.SESSION_RETRY_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ===================== Android =====================

    private static UiAutomator2Options buildAndroidOptions(String platform, FrameworkConfig config, boolean browserStack) {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");

        String version = config.getForPlatformOrDefault(platform, "platform.version", "");
        if (!version.isBlank()) options.setPlatformVersion(version);

        options.setDeviceName(config.getForPlatform(platform, "device.name"));
        options.setAutomationName(config.getForPlatform(platform, "automation.name"));

        String launchTimeoutMs = config.getForPlatformOrDefault(platform, "uiautomator2.server.launch.timeout", "60000");
        options.setCapability("appium:uiautomator2ServerLaunchTimeout", Integer.parseInt(launchTimeoutMs));

        String adbExecTimeoutMs = config.getForPlatformOrDefault(platform, "adb.exec.timeout", "60000");
        options.setCapability("appium:adbExecTimeout", Integer.parseInt(adbExecTimeoutMs));

        resolveAppStrategy(options, platform, config);

        if (browserStack) applyBrowserStackOptions(options, config, platform);
        return options;
    }

    private static void resolveAppStrategy(UiAutomator2Options options, String platform, FrameworkConfig config) {
        String app = config.getForPlatformOrDefault(platform, "app", "");
        String appPath = config.getForPlatformOrDefault(platform, "app.path", "");
        String appPackage = config.getForPlatformOrDefault(platform, "app.package", "");
        String appActivity = config.getForPlatformOrDefault(platform, "app.activity", "");

        if (!app.isBlank()) {
            options.setApp(app);
            logInfo("Android strategy=remoteApp app=" + app);
        } else if (!appPath.isBlank()) {
            Path path = Path.of(appPath);
            if (!Files.exists(path)) throw new IllegalStateException("app.path no existe: " + path.toAbsolutePath());
            options.setApp(path.toAbsolutePath().toString());
            logInfo("Android strategy=localPath path=" + path.toAbsolutePath());
        } else if (!appPackage.isBlank() && !appActivity.isBlank()) {
            options.setAppPackage(appPackage);
            options.setAppActivity(appActivity);
            logInfo("Android strategy=packageActivity pkg=" + appPackage);
        } else {
            throw new IllegalStateException("Android: define app, app.path, o app.package + app.activity");
        }
    }

    // ===================== iOS =====================

    private static XCUITestOptions buildIosOptions(String platform, FrameworkConfig config, boolean browserStack) {
        XCUITestOptions options = new XCUITestOptions();
        options.setPlatformName("iOS");

        String version = config.getForPlatformOrDefault(platform, "platform.version", "");
        if (!version.isBlank()) options.setPlatformVersion(version);

        options.setDeviceName(config.getForPlatform(platform, "device.name"));
        options.setAutomationName(config.getForPlatform(platform, "automation.name"));

        resolveAppStrategy(options, platform, config);

        if (browserStack) applyBrowserStackOptions(options, config, platform);
        return options;
    }

    private static void resolveAppStrategy(XCUITestOptions options, String platform, FrameworkConfig config) {
        String app = config.getForPlatformOrDefault(platform, "app", "");
        String appPath = config.getForPlatformOrDefault(platform, "app.path", "");
        String bundleId = config.getForPlatformOrDefault(platform, "bundle.id", "");

        if (!app.isBlank()) {
            options.setApp(app);
            logInfo("iOS strategy=remoteApp app=" + app);
        } else if (!appPath.isBlank()) {
            Path path = Path.of(appPath);
            if (!Files.exists(path)) throw new IllegalStateException("app.path no existe: " + path.toAbsolutePath());
            options.setApp(path.toAbsolutePath().toString());
            logInfo("iOS strategy=localPath path=" + path.toAbsolutePath());
        } else if (!bundleId.isBlank()) {
            options.setBundleId(bundleId);
            logInfo("iOS strategy=bundleId id=" + bundleId);
        } else {
            throw new IllegalStateException("iOS: define app, app.path, o bundle.id");
        }
    }

    // ===================== BrowserStack =====================

    private static void applyBrowserStackOptions(Object options, FrameworkConfig config, String platform) {
        Map<String, Object> bsOptions = new LinkedHashMap<>();
        bsOptions.put("projectName", config.getOrDefault("browserstack.project.name", "appium-automation"));
        bsOptions.put("buildName", config.getOrDefault("browserstack.build.name", "build-1"));
        bsOptions.put("sessionName", config.getOrDefault("browserstack.session.name", "session-" + platform));
        putBoolean(bsOptions, "debug", config.getOrDefault("browserstack.debug", "true"));
        putBoolean(bsOptions, "networkLogs", config.getOrDefault("browserstack.networkLogs", "true"));
        putBoolean(bsOptions, "appiumLogs", config.getOrDefault("browserstack.appiumLogs", "true"));
        putBoolean(bsOptions, "video", config.getOrDefault("browserstack.video", "true"));
        putBoolean(bsOptions, "deviceLogs", config.getOrDefault("browserstack.deviceLogs", "true"));
        bsOptions.put("idleTimeout", Integer.parseInt(config.getOrDefault("browserstack.idleTimeout", "300")));

        if (options instanceof UiAutomator2Options android) {
            android.setCapability("bstack:options", bsOptions);
        } else if (options instanceof XCUITestOptions ios) {
            ios.setCapability("bstack:options", bsOptions);
        }

        logInfo("BrowserStack capabilities applied for " + platform);
    }

    // ===================== Utilidades =====================

    private static void validateBrowserStackCredentials(URL serverUrl, boolean browserStack) {
        if (!browserStack) return;
        String userInfo = serverUrl.getUserInfo();
        if (userInfo == null || userInfo.isBlank()) {
            throw new IllegalStateException("BrowserStack URL requiere credenciales (user:key) en appium.server.url");
        }
    }

    private static boolean isBrowserStack(String serverUrl) {
        return serverUrl != null && serverUrl.contains("browserstack");
    }

    private static void putBoolean(Map<String, Object> map, String key, String value) {
        if (value != null && !value.isBlank()) map.put(key, Boolean.parseBoolean(value));
    }

    private static URL buildServerUrl(String rawUrl) {
        try {
            return URI.create(rawUrl).toURL();
        } catch (Exception e) {
            throw new IllegalArgumentException("appium.server.url invalido: " + rawUrl, e);
        }
    }

    private static String sanitizeUrl(URL url) {
        String userInfo = url.getUserInfo();
        if (userInfo == null || userInfo.isBlank()) return url.toString();
        return url.toString().replace(userInfo + "@", "***:***@");
    }

    private static void logInfo(String msg) {
        log.info(msg);
    }

    private static void logError(String msg) {
        log.error(msg);
    }
}
