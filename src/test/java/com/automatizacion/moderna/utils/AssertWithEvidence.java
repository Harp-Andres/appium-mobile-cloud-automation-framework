package com.automatizacion.moderna.utils;

import com.automatizacion.moderna.abilities.TakeScreenshot;
import com.automatizacion.moderna.actors.Actor;
import io.cucumber.java.Scenario;
import org.junit.jupiter.api.Assertions;

/**
 * Utilidad para aserciones con captura de evidencia automática en caso de fallo.
 */
public class AssertWithEvidence {
    public static void assertTrueWithEvidence(boolean condition, String message, Actor actor, Scenario scenario) {
        try {
            Assertions.assertTrue(condition, message);
        } catch (AssertionError e) {
            attachEvidence(actor, "ASSERTION_FAILED_TRUE_" + message, scenario);
            throw e;
        }
    }

    public static void assertEqualsWithEvidence(Object expected, Object actual, String message, Actor actor, Scenario scenario) {
        try {
            Assertions.assertEquals(expected, actual, message);
        } catch (AssertionError e) {
            attachEvidence(actor, "ASSERTION_FAILED_EQUALS_" + message, scenario);
            throw e;
        }
    }

    private static void attachEvidence(Actor actor, String label, Scenario scenario) {
        TakeScreenshot takeScreenshotAbility = actor.getAbility(TakeScreenshot.class);
        if (takeScreenshotAbility != null) {
            EvidenceCapture.attachScreenshot(takeScreenshotAbility, label, true, scenario);
        }
    }
}

