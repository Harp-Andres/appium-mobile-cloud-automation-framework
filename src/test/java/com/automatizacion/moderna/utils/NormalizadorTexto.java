package com.automatizacion.moderna.utils;

/**
 * Utilidad para normalizar textos antes de comparar aserciones UI.
 * - Convierte escapes literales (\\n, \\r\\n, \\t)
 * - Unifica saltos de línea a \n
 * - Elimina saltos de línea finales
 * - Opcionalmente colapsa espacios en blanco para comparación flexible
 */
public final class NormalizadorTexto {

    private NormalizadorTexto() {
    }

    public static String paraComparacion(String texto) {
        if (texto == null) {
            return "";
        }

        String conEscapesResueltos = texto
            .replace("\\r\\n", "\n")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t");

        String conSaltosUnificados = conEscapesResueltos
            .replace("\r\n", "\n")
            .replace('\r', '\n');

        return removerSaltosFinales(conSaltosUnificados);
    }

    public static String paraComparacionFlexible(String texto) {
        return paraComparacion(texto)
            .replaceAll("\\s+", " ")
            .trim();
    }

    private static String removerSaltosFinales(String texto) {
        int fin = texto.length();
        while (fin > 0) {
            char c = texto.charAt(fin - 1);
            if (c == '\n' || c == '\r') {
                fin--;
                continue;
            }
            break;
        }
        return texto.substring(0, fin);
    }
}

