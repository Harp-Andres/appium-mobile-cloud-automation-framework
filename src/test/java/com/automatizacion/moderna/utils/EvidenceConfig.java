package com.automatizacion.moderna.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utilidad para leer configuraciones de evidencia desde system properties o archivo properties.
 * Da prioridad a system properties, luego busca en evidence.properties.
 */
public class EvidenceConfig {
    private static final String PROPERTIES_FILE = "/evidence.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = EvidenceConfig.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (in != null) {
                PROPERTIES.load(in);
            }
        } catch (IOException e) {
            System.out.println("[EVIDENCE][WARN] No se pudo cargar evidence.properties: " + e.getMessage());
        }
    }

    /**
     * Obtiene el valor de una propiedad de evidencia, primero de system properties, luego del archivo.
     * @param key clave de la propiedad
     * @param defaultValue valor por defecto si no se encuentra
     * @return valor de la propiedad
     */
    public static String get(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null) return sys;
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /**
     * Obtiene un booleano de configuración de evidencia.
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, Boolean.toString(defaultValue)));
    }
}

