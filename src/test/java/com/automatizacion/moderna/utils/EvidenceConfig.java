package com.automatizacion.moderna.utils;

import com.automatizacion.moderna.config.FrameworkConfig;

/**
 * Utilidad para leer configuraciones de evidencia del framework.
 * <p>
 * Las propiedades {@code evidence.*} viven en {@code config/base.properties}
 * y pueden sobreescribirse por entorno en {@code config/<env>.properties}
 * o en tiempo de ejecución con {@code -Devidence.clave=valor}.
 * <p>
 * Prioridad de resolución:
 * <ol>
 *   <li>System property ({@code -D…})</li>
 *   <li>{@link FrameworkConfig} (base + env properties)</li>
 *   <li>defaultValue proporcionado por el llamador</li>
 * </ol>
 */
public final class EvidenceConfig {

    private EvidenceConfig() {}

    /**
     * Obtiene el valor de una propiedad de evidencia.
     *
     * @param key          clave, p.ej. {@code "evidence.video.enabled"}
     * @param defaultValue valor de respaldo si no se encuentra
     * @return valor resuelto
     */
    public static String get(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys.trim();
        try {
            return FrameworkConfig.getInstance().getOrDefault(key, defaultValue);
        } catch (Exception ignored) {
            // FrameworkConfig no inicializado aún (p.ej. tests unitarios sin ENV).
            return defaultValue;
        }
    }

    /**
     * Obtiene un booleano de configuración de evidencia.
     *
     * @param key          clave, p.ej. {@code "evidence.screenshot.on.pass"}
     * @param defaultValue valor de respaldo
     * @return valor booleano resuelto
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, Boolean.toString(defaultValue)));
    }
}
