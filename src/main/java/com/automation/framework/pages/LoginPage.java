package com.automation.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.NoSuchElementException;

/**
 * Page Object Model for Login Page
 */
public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement usernameField;

    @Override
    public boolean isPageLoaded() {
        return usernameField != null && usernameField.isDisplayed();
    }

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    public LoginPage navigateToSwagLabs() {
        navigateTo("https://www.saucedemo.com/v1/");
        waitForPageLoad();
        return this;
    }

    public void enterUsername(String username) {
        usernameField.sendKeys(username);
    }

    public void enterPassword(String password) {
        passwordField.sendKeys(password);
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    public boolean isLoginSuccessful() {
        try {
            driver.findElement(By.id("inventory_container"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public String getLoginErrorMessage() {
        try {
            WebElement errorMessage = driver.findElement(By.xpath("//button[@class='error-button']"));
            return errorMessage.getText();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
    
    public boolean isLoginErrorDisplayed() {
        try {
            WebElement errorMessage = driver.findElement(By.xpath("//h3[@data-test='error']"));
            return errorMessage.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
