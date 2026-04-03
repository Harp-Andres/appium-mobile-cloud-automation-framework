package com.automatizacion.moderna.abilities;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.config.FrameworkConfig;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Habilidad Screenplay para centralizar esperas explicitas dinamicas.
 * Permite usar timeouts por defecto del framework o por accion.
 */
public final class WaitForElements implements Ability {

    private static final long FALLBACK_WAIT_SECONDS = 20;
    private static final long FALLBACK_POLLING_MILLIS = 500;

    private final AppiumDriver driver;
    private final Duration defaultTimeout;
    private final Duration defaultPolling;

    private WaitForElements(AppiumDriver driver, Duration defaultTimeout, Duration defaultPolling) {
        if (driver == null) {
            throw new IllegalArgumentException("El driver no puede ser null");
        }
        this.driver = driver;
        this.defaultTimeout = defaultTimeout;
        this.defaultPolling = defaultPolling;
    }

    public static WaitForElements with(AppiumDriver driver) {
        FrameworkConfig config = FrameworkConfig.getInstance();
        long waitSeconds = parseLong(config.getOrDefault("explicit.wait", String.valueOf(FALLBACK_WAIT_SECONDS)), FALLBACK_WAIT_SECONDS);
        long pollingMillis = parseLong(config.getOrDefault("explicit.poll.ms", String.valueOf(FALLBACK_POLLING_MILLIS)), FALLBACK_POLLING_MILLIS);

        Duration timeout = Duration.ofSeconds(Math.max(1, waitSeconds));
        Duration polling = Duration.ofMillis(Math.max(100, pollingMillis));
        return new WaitForElements(driver, timeout, polling);
    }

    public static WaitForElements as(Actor actor) {
        WaitForElements ability = actor.getAbility(WaitForElements.class);
        if (ability == null) {
            throw new IllegalStateException("El actor no tiene la habilidad WaitForElements asignada");
        }
        return ability;
    }

    @Override
    public void bindDriver(AppiumDriver driver) {
        throw new UnsupportedOperationException("WaitForElements es inmutable. Use WaitForElements.with(driver)");
    }

    public WebElement untilVisible(WebElement element) {
        return untilVisible(element, defaultTimeout);
    }

    public WebElement untilVisible(WebElement element, Duration timeout) {
        return waitWith(timeout).until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement untilClickable(WebElement element) {
        return untilClickable(element, defaultTimeout);
    }

    public WebElement untilClickable(WebElement element, Duration timeout) {
        return waitWith(timeout).until(ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement untilVisibleAndClickable(WebElement element) {
        untilVisible(element, defaultTimeout);
        return untilClickable(element, defaultTimeout);
    }

    public WebElement untilPresent(By locator) {
        return untilPresent(locator, defaultTimeout);
    }

    public WebElement untilPresent(By locator, Duration timeout) {
        return waitWith(timeout).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public boolean untilInvisible(WebElement element) {
        return untilInvisible(element, defaultTimeout);
    }

    public boolean untilInvisible(WebElement element, Duration timeout) {
        return waitWith(timeout).until(ExpectedConditions.invisibilityOf(element));
    }

    public boolean untilTextContains(WebElement element, String expectedText) {
        return untilTextContains(element, expectedText, defaultTimeout);
    }

    public boolean untilTextContains(WebElement element, String expectedText, Duration timeout) {
        return waitWith(timeout).until(ExpectedConditions.textToBePresentInElement(element, expectedText));
    }

    public boolean untilAttributeContains(WebElement element, String attribute, String expectedValue) {
        return untilAttributeContains(element, attribute, expectedValue, defaultTimeout);
    }

    public boolean untilAttributeContains(WebElement element, String attribute, String expectedValue, Duration timeout) {
        return waitWith(timeout).until(ExpectedConditions.attributeContains(element, attribute, expectedValue));
    }

    private WebDriverWait waitWith(Duration timeout) {
        Duration effectiveTimeout = (timeout == null || timeout.isNegative() || timeout.isZero())
            ? defaultTimeout
            : timeout;

        WebDriverWait wait = new WebDriverWait(driver, effectiveTimeout);
        wait.pollingEvery(defaultPolling);
        return wait;
    }

    private static long parseLong(String value, long fallback) {
        try {
            return Long.parseLong(value);
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }
}

