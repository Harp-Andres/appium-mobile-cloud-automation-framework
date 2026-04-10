package com.automatizacion.moderna.ui;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * Elementos UI de la pantalla principal de ApiDemos.
 * Mantiene mapeo multiplataforma con PageFactory para ser reutilizado desde Screenplay.
 */
public class HomePageUI {

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='API Demos']")
    @iOSXCUITFindBy(accessibility = "API Demos")
    public WebElement HOME_TITLE;

    @AndroidFindBy(accessibility = "Content")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeNavigationBar//XCUIElementTypeStaticText[1]")
    public WebElement PRINCIPAL_MENU;

    public HomePageUI(AppiumDriver driver) {
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
}
