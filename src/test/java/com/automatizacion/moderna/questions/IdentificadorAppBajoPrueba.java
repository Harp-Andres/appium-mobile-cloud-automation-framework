package com.automatizacion.moderna.questions;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.config.FrameworkConfig;
import com.automatizacion.moderna.screenplay.Question;

/**
 * Pregunta Screenplay que resuelve el identificador de la app bajo prueba
 * desde la configuración activa (package en Android o bundle id en iOS).
 */
public final class IdentificadorAppBajoPrueba implements Question<String> {

    private IdentificadorAppBajoPrueba() {
    }

    public static IdentificadorAppBajoPrueba ahora() {
        return new IdentificadorAppBajoPrueba();
    }

    @Override
    public String answeredBy(Actor actor) {
        FrameworkConfig config = FrameworkConfig.getInstance();
        String platform = config.getDefaultPlatform();

        return "android".equals(platform)
            ? config.getForPlatformOrDefault(platform, "app.package", "")
            : config.getForPlatformOrDefault(platform, "bundle.id", "");
    }
}

