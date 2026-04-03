package com.automatizacion.moderna.ui;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * Elementos UI de pantallas de contenido de ApiDemos.
 * La navegación dinámica se resuelve desde Screenplay, pero los elementos fijos viven aquí.
 */
public class ContentUI {


    @AndroidFindBy(xpath = "//android.widget.TextView[@content-desc='Assets']")
    @iOSXCUITFindBy(xpath = "//*[contains(@name,'Read Asset') or contains(@label,'Read Asset') or contains(@value,'Read Asset')]")
    public static WebElement LABEL_ASSETS;

    @AndroidFindBy(accessibility = "Read Asset")
    @iOSXCUITFindBy(xpath = "//*[contains(@name,'raw Asset') or contains(@label,'raw Asset') or contains(@value,'raw Asset')]")
    public static WebElement LABEL_READ_ASSET;

    @AndroidFindBy(uiAutomator = "new UiSelector().text('Content/Assets/Read Asset')")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeNavigationBar//XCUIElementTypeStaticText[1]")
    public WebElement PATCH_TITLE;

    @AndroidFindBy(xpath = "//android.widget.TextView[@resource-id='io.appium.android.apis:id/text']")
    @iOSXCUITFindBy(xpath = "//*[contains(@name,'raw Asset') or contains(@label,'raw Asset') or contains(@value,'raw Asset')]")
    public WebElement READ_ASSET_CONTENT;

    public ContentUI(AppiumDriver driver) {
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
}

