package com.automatizacion.moderna.utils;

import com.automatizacion.moderna.config.FrameworkConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prepara el directorio de resultados de Allure antes de que corran los tests.
 * <p>
 * Responsabilidades (una sola clase, SRP):
 * <ol>
 *   <li>Genera {@code environment.properties} con datos dinámicos del entorno
 *       (ENV, plataforma, dispositivo, URL del servidor, etc.) para que Allure
 *       los muestre en la sección "Environment" del reporte HTML.</li>
 *   <li>Copia {@code categories.json} al directorio de resultados para que
 *       Allure pueda agrupar fallos por categoría.</li>
 * </ol>
 * <p>
 * Uso: llamar {@link #writeOnce(String)} con la plataforma activa al inicio
 * de la ejecución (p.ej. desde el primer {@code @Before} de Hooks).
 * La operación es idempotente: se ejecuta como máximo una vez por JVM.
 *
 * <h3>Por qué NO en allure.properties</h3>
 * {@code allure.properties} solo soporta un conjunto fijo de claves que el
 * listener de allure-java lee en tiempo de compilación. Propiedades dinámicas
 * como ENV o Device.Name deben escribirse en {@code environment.properties}
 * dentro del results-dir en tiempo de ejecución.
 */
public final class AllureEnvironmentWriter {

    private static final Logger log = LoggerFactory.getLogger(AllureEnvironmentWriter.class);
    private static final AtomicBoolean WRITTEN = new AtomicBoolean(false);

    /**
     * Clave de system property / allure.properties para el results dir.
     * Debe coincidir con {@code allure.results.directory} del archivo.
     */
    private static final String RESULTS_DIR_PROP = "allure.results.directory";
    private static final String DEFAULT_RESULTS_DIR = "target/allure-results";

    /** Ruta en classpath al archivo categories.json del proyecto. */
    private static final String CATEGORIES_CLASSPATH = "/categories.json";

    private AllureEnvironmentWriter() {}

    // ─────────────────────────────────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Escribe {@code environment.properties} y copia {@code categories.json}
     * al directorio de resultados de Allure.
     * <p>
     * Thread-safe: garantizado que la escritura ocurre una sola vez aunque
     * múltiples hilos llamen este método en paralelo (escenarios Android + iOS).
     *
     * @param platform plataforma activa detectada por el Hook: {@code "android"} o {@code "ios"}
     */
    public static void writeOnce(String platform) {
        if (!WRITTEN.compareAndSet(false, true)) return;

        Path dir = resolveResultsDir();
        try {
            Files.createDirectories(dir);
            writeEnvironmentProperties(dir, platform);
            copyCategories(dir);
        } catch (IOException e) {
            log.warn("Fallo preparando directorio de resultados: {}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internals
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Resuelve la ruta del results dir con la misma prioridad que allure-java:
     * system property {@code -Dallure.results.directory} > valor en allure.properties > default.
     */
    private static Path resolveResultsDir() {
        String sysProp = System.getProperty(RESULTS_DIR_PROP);
        if (sysProp != null && !sysProp.isBlank()) return Path.of(sysProp.trim());
        return Path.of(DEFAULT_RESULTS_DIR);
    }

    /**
     * Genera environment.properties con todos los metadatos relevantes del entorno.
     * Allure lee este archivo para poblar la sección "Environment" del reporte.
     */
    private static void writeEnvironmentProperties(Path dir, String platform) throws IOException {
        FrameworkConfig config = FrameworkConfig.getInstance();
        String env = config.getActiveEnv();

        // Sobreescribe el nombre del reporte dinámicamente.
        // allure-java da prioridad a system properties sobre allure.properties,
        // por lo que este valor aparecerá en el header del reporte HTML.
        //   local        → "Automatización Mobile – ANDROID [LOCAL]"
        //   browserstack → "Automatización Mobile Cloud – ANDROID [BROWSERSTACK]"
        String context = isCloudEnv(env) ? "Mobile Cloud" : "Mobile";
        String reportName = "Automatización " + context + " – "
                + platform.toUpperCase() + " [" + env.toUpperCase() + "]";
        System.setProperty("allure.report.name", reportName);
        log.info("allure.report.name → {}", reportName);

        Properties props = new Properties();

        // ── Contexto de ejecución ──────────────────────────────────────────
        props.setProperty("Environment", env.toUpperCase());
        props.setProperty("Platform",    platform.toUpperCase());

        // ── Servidor Appium ────────────────────────────────────────────────
        props.setProperty("Appium.Server.URL",
                config.getOrDefault("appium.server.url", "n/a"));

        // ── Dispositivo / plataforma (resuelve prefijo android.* o ios.*) ─
        props.setProperty("Platform.Version",
                config.getForPlatformOrDefault(platform, "platform.version", "n/a"));
        props.setProperty("Device.Name",
                config.getForPlatformOrDefault(platform, "device.name", "n/a"));
        props.setProperty("Automation.Engine",
                config.getForPlatformOrDefault(platform, "automation.name", "n/a"));

        // ── Metadatos BrowserStack (solo si env = browserstack) ────────────
        if ("browserstack".equalsIgnoreCase(env)) {
            props.setProperty("BS.Project",
                    config.getOrDefault("browserstack.project.name", "n/a"));
            props.setProperty("BS.Build",
                    config.getOrDefault("browserstack.build.name", "n/a"));
            props.setProperty("BS.Session",
                    config.getOrDefault("browserstack.session.name", "n/a"));
        }

        Path envFile = dir.resolve("environment.properties");
        try (OutputStream os = Files.newOutputStream(envFile)) {
            props.store(os, "Auto-generated by AllureEnvironmentWriter – do not edit manually");
        }
        log.info("environment.properties escrito → {}", envFile.toAbsolutePath());
    }

    /**
     * Copia categories.json del classpath al directorio de resultados.
     * Allure lo usa para agrupar fallos por categoría en el reporte.
     * Si ya existe (ejecución incremental) no sobreescribe.
     */
    private static void copyCategories(Path dir) {
        Path target = dir.resolve("categories.json");
        if (Files.exists(target)) return;

        try (InputStream is = AllureEnvironmentWriter.class.getResourceAsStream(CATEGORIES_CLASSPATH)) {
            if (is == null) {
                log.warn("categories.json no encontrado en classpath; se omite.");
                return;
            }
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("categories.json copiado → {}", target.toAbsolutePath());
        } catch (IOException e) {
            log.warn("No se pudo copiar categories.json: {}", e.getMessage());
        }
    }

    /**
     * Determina si el entorno activo es un proveedor cloud (BrowserStack, Sauce Labs, etc.).
     * Centralizado aquí para que el criterio sea consistente en toda la clase.
     * Para agregar un nuevo proveedor, basta añadirlo a esta lista.
     */
    private static boolean isCloudEnv(String env) {
        return env != null && (
                env.equalsIgnoreCase("browserstack") ||
                env.equalsIgnoreCase("saucelabs")    ||
                env.equalsIgnoreCase("lambdatest")
        );
    }
}

