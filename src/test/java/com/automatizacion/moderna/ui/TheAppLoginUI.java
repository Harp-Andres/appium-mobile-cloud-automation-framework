package com.automatizacion.moderna.ui;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * Login screen of TheApp (accessibility ids match upstream {@code test/views/LoginView.ts}).
 */
public class TheAppLoginUI {

    @AndroidFindBy(accessibility = "username")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeTextField[@name=\"username\"]")
    public WebElement USERNAME;

    @AndroidFindBy(accessibility = "password")
    @iOSXCUITFindBy(accessibility = "password")
    public WebElement PASSWORD;

    @AndroidFindBy(accessibility = "loginBtn")
    @iOSXCUITFindBy(accessibility = "loginBtn")
    public WebElement LOGIN_BUTTON;

    public TheAppLoginUI(AppiumDriver driver) {
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
}
