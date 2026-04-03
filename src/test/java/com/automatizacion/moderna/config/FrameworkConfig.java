package com.automatizacion.moderna.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Configuracion centralizada del framework.
 * <p>
 * Responsabilidad unica: cargar {@code base.properties + {env}.properties}
 * y resolver valores por clave con prioridad: System property > Variable de entorno > archivo.
 * <p>
 * NO almacena estado de plataforma. La plataforma se pasa como parametro
 * via {@link #getForPlatform(String, String)} para soportar paralelismo.
 */
public final class FrameworkConfig {

    private static final String TEST_ENV_VARIABLE = "ENV";
    private static final String CONFIG_BASE_PATH = "config";
    private static final String BASE_FILE = "base.properties";
    private static final String PLATFORM_TARGET_KEY = "platform.target";
    private static final String DEFAULT_PLATFORM = "android";

    private static FrameworkConfig instance;

    private final String activeEnv;
    private final Properties properties;

    private FrameworkConfig() {
        this.activeEnv = resolveActiveEnv();
        this.properties = load(activeEnv);
    }

    public static synchronized FrameworkConfig getInstance() {
        if (instance == null) {
            instance = new FrameworkConfig();
        }
        return instance;
    }

    public static synchronized void reset() {
        instance = null;
    }

    public String getActiveEnv() {
        return activeEnv;
    }

    // ===================== Propiedades compartidas =====================

    public String get(String key) {
        String value = resolveValue(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Propiedad obligatoria no encontrada: " + key);
        }
        return value.trim();
    }

    public String getOrDefault(String key, String defaultValue) {
        String value = resolveValue(key);
        return (value == null || value.isBlank()) ? defaultValue.trim() : value.trim();
    }

    // ============== Propiedades con resolucion por plataforma ==============

    /**
     * Resuelve {@code {platform}.{key}} primero; fallback a {@code {key}} generico.
     * La plataforma se recibe como parametro — NO depende de estado interno.
     * Esto permite que hilos paralelos resuelvan Android e iOS sin conflicto.
     *
     * @param platform "android" o "ios"
     * @param key      clave sin prefijo, ej: "device.name"
     * @return valor resuelto
     * @throws IllegalArgumentException si no se encuentra ni con prefijo ni sin el
     */
    public String getForPlatform(String platform, String key) {
        String value = resolvePlatformValue(platform, key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                "Propiedad [" + platform + "." + key + "] ni generica [" + key + "] encontrada");
        }
        return value.trim();
    }

    /**
     * Igual que {@link #getForPlatform(String, String)} con fallback a defaultValue.
     */
    public String getForPlatformOrDefault(String platform, String key, String defaultValue) {
        String value = resolvePlatformValue(platform, key);
        return (value == null || value.isBlank()) ? defaultValue.trim() : value.trim();
    }

    /**
     * Devuelve la plataforma por defecto configurada en properties.
     * Util para Hooks cuando el escenario no tiene tag @android/@ios explicito.
     */
    public String getDefaultPlatform() {
        String raw = resolveValue(PLATFORM_TARGET_KEY);
        String resolved = resolveEnvToken(raw);
        if (resolved == null || resolved.isBlank()) {
            return DEFAULT_PLATFORM;
        }
        String normalized = resolved.trim().toLowerCase();
        if (!"android".equals(normalized) && !"ios".equals(normalized)) {
            throw new IllegalStateException("platform.target debe ser 'android' o 'ios', recibido: " + resolved);
        }
        return normalized;
    }

    // ===================== Internals =====================

    private String resolvePlatformValue(String platform, String key) {
        String prefixed = resolveValue(platform + "." + key);
        if (prefixed != null && !prefixed.isBlank()) {
            return prefixed;
        }
        return resolveValue(key);
    }

    private Properties load(String env) {
        Path basePath = Path.of("src", "test", "resources", CONFIG_BASE_PATH, BASE_FILE);
        Path envPath = Path.of("src", "test", "resources", CONFIG_BASE_PATH, env + ".properties");

        if (!Files.exists(envPath)) {
            throw new IllegalStateException("No existe archivo de configuracion: " + envPath.toAbsolutePath());
        }

        Properties loaded = new Properties();
        loadPropertiesFile(basePath, loaded, false);
        loadPropertiesFile(envPath, loaded, true);

        validateSharedKeys(loaded);
        return loaded;
    }

    private void validateSharedKeys(Properties loaded) {
        String serverUrl = resolveValue("appium.server.url", loaded);
        if (serverUrl == null || serverUrl.isBlank()) {
            throw new IllegalStateException("Falta propiedad obligatoria: appium.server.url");
        }
    }

    private String resolveActiveEnv() {
        String fromSysProp = System.getProperty(TEST_ENV_VARIABLE);
        if (fromSysProp != null) {
            if (!fromSysProp.isBlank()) {
                return fromSysProp.trim();
            }
            throw new IllegalStateException(
                "Debe definir ENV para seleccionar el archivo de entorno. "
                    + "Ejemplo local: -DENV=local. Ejemplo pipeline: ENV=browserstack");
        }

        String fromEnvVar = System.getenv(TEST_ENV_VARIABLE);
        if (fromEnvVar != null && !fromEnvVar.isBlank()) return fromEnvVar.trim();

        throw new IllegalStateException(
            "Debe definir ENV para seleccionar el archivo de entorno. "
                + "Ejemplo local: -DENV=local. Ejemplo pipeline: ENV=browserstack");
    }


    private String resolveEnvToken(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) return null;

        String candidate = rawValue.trim();
        if (!candidate.startsWith("${") || !candidate.endsWith("}")) return candidate;

        String token = candidate.substring(2, candidate.length() - 1);
        String[] parts = token.split(":", 2);
        String varName = parts[0].trim();
        String fallback = parts.length > 1 ? parts[1].trim() : null;

        if (!varName.isBlank()) {
            String varValue = System.getenv(varName);
            if (varValue != null && !varValue.isBlank()) return varValue.trim();
        }
        return (fallback == null || fallback.isBlank()) ? null : fallback;
    }

    private void loadPropertiesFile(Path path, Properties target, boolean required) {
        if (!Files.exists(path)) {
            if (required) throw new IllegalStateException("No existe: " + path.toAbsolutePath());
            return;
        }
        try (InputStream is = Files.newInputStream(path)) {
            target.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Error cargando: " + path.toAbsolutePath(), e);
        }
    }

    private String resolveValue(String key) {
        return resolveValue(key, properties);
    }

    private String resolveValue(String key, Properties source) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) return sysProp;

        String envKey = key.toUpperCase().replace('.', '_').replace('-', '_');
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isBlank()) return envVal;

        return source.getProperty(key);
    }
}
