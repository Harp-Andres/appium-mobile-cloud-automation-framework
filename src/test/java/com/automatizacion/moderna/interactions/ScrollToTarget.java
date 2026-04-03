package com.automatizacion.moderna.interactions;

import com.automatizacion.moderna.actors.Actor;
import com.automatizacion.moderna.abilities.BrowseTheMobileApp;
import com.automatizacion.moderna.screenplay.Performable;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

public final class ScrollToTarget implements Performable {

    private static final int DEFAULT_MAX_SWIPES = 6;

    private enum Direction {
        DOWN,
        UP
    }

    private enum ScrollMode {
        NATIVE_GESTURE,
        COORDINATES
    }

    private final WebElement element;
    private final Direction direction;
    private final ScrollMode mode;

    private ScrollToTarget(WebElement element, Direction direction, ScrollMode mode) {
        this.element = element;
        this.direction = direction;
        this.mode = mode;
    }

    public static ScrollToTarget downTo(WebElement element) {
        return new ScrollToTarget(element, Direction.DOWN, ScrollMode.NATIVE_GESTURE);
    }

    public static ScrollToTarget upTo(WebElement element) {
        return new ScrollToTarget(element, Direction.UP, ScrollMode.NATIVE_GESTURE);
    }

    // Excepcion solicitada: scroll por coordenadas.
    public static ScrollToTarget byCoordinatesTo(WebElement element) {
        return new ScrollToTarget(element, Direction.DOWN, ScrollMode.COORDINATES);
    }

    @Override
    public void performAs(Actor actor) {
        AppiumDriver driver = BrowseTheMobileApp.as(actor);
        if (!(driver instanceof AndroidDriver androidDriver)) {
            return;
        }

        if (element == null) {
            throw new IllegalArgumentException("El elemento para hacer scroll no puede ser null");
        }

        if (isVisible(element)) {
            return;
        }

        RuntimeException last = null;
        for (int attempt = 0; attempt < DEFAULT_MAX_SWIPES; attempt++) {
            if (isVisible(element)) {
                return;
            }
            try {
                if (mode == ScrollMode.COORDINATES) {
                    scrollByCoordinates(androidDriver);
                } else {
                    scrollByNativeGesture(androidDriver);
                }
            } catch (RuntimeException e) {
                last = e;
                break;
            }
        }

        if (isVisible(element)) {
            return;
        }

        throw new IllegalStateException(
            "No fue posible hacer scroll al elemento tras " + DEFAULT_MAX_SWIPES + " intentos",
            last
        );
    }

    private void scrollByNativeGesture(AndroidDriver androidDriver) {
        Dimension size = androidDriver.manage().window().getSize();

        int left = Math.max(1, (int) (size.width * 0.10));
        int top = Math.max(1, (int) (size.height * 0.10));
        int width = Math.max(1, (int) (size.width * 0.80));
        int height = Math.max(1, (int) (size.height * 0.80));

        Map<String, Object> params = new HashMap<>();
        params.put("left", left);
        params.put("top", top);
        params.put("width", width);
        params.put("height", height);
        params.put("direction", direction == Direction.UP ? "up" : "down");
        params.put("percent", 0.75);

        androidDriver.executeScript("mobile: scrollGesture", params);
    }

    private void scrollByCoordinates(AndroidDriver androidDriver) {
        Dimension size = androidDriver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = direction == Direction.UP ? (int) (size.height * 0.30) : (int) (size.height * 0.70);
        int endY = direction == Direction.UP ? (int) (size.height * 0.75) : (int) (size.height * 0.25);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(450), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        androidDriver.perform(List.of(swipe));
    }

    private static boolean isVisible(WebElement element) {
        try {
            return element != null && element.isDisplayed();
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}

