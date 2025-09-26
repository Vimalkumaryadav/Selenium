package com.automation.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.automation.framework.base.BaseTest;
import com.automation.framework.pages.LoginPage;
public class saucedemo extends BaseTest {

    @Test(priority = 1, groups = {"smoke", "regression"}, 
          description = "Test validates that a user can successfully log into the SauceDemo application using valid credentials")
    public void testLoginFunctionality() {
        logStep("Starting valid login functionality test");

        LoginPage loginPage = new LoginPage();
        navigateToSwagLabsPage(loginPage);
        
        validatePageLoad(loginPage);
        
        performLogin(loginPage, "standard_user", "secret_sauce");
        
        validateLoginSuccess(loginPage);
        
        logStep("Valid login test completed successfully");
    }

    @Test(priority = 2, groups = {"negative", "regression"},
          description = "Test validates that the application properly handles invalid login attempts and displays appropriate error messages.")
    public void testLoginWithInvalidCredentials() {
        logStep("Starting invalid credentials login test");

        LoginPage loginPage = new LoginPage();
        navigateToSwagLabsPage(loginPage);
        
        validatePageLoad(loginPage);
        
        performLogin(loginPage, "invalid_user", "test_sauce");
        
        validateLoginFailure(loginPage);
        
        logStep("Invalid credentials test completed successfully");
    }

    private void navigateToSwagLabsPage(LoginPage loginPage) {
        loginPage.navigateToSwagLabs();
        logStep("Successfully navigated to SauceDemo application");
    }

    private void validatePageLoad(LoginPage loginPage) {
        Assert.assertTrue(loginPage.isPageLoaded(), "SauceDemo login page should be loaded and username field should be visible");
        logStep("Login page load validation passed - username field is visible");
    }

    private void performLogin(LoginPage loginPage, String username, String password) {
        loginPage.enterUsername(username);
        logStep("Entered username: " + username);
        
        loginPage.enterPassword(password); // Use actual password for functionality, logs will hide it
        logStep("Entered password (hidden for security)");
        
        loginPage.clickLoginButton();
        logStep("Clicked login button to submit credentials");
    }

    private void validateLoginSuccess(LoginPage loginPage) {
        Assert.assertTrue(loginPage.isLoginSuccessful(), "Login should be successful and inventory container should be visible");
        logStep("Login success validation passed - user successfully authenticated and redirected");
    }

    private void validateLoginFailure(LoginPage loginPage) {
        Assert.assertTrue(loginPage.isLoginErrorDisplayed(), "Login error message should be displayed for invalid credentials");
        logStep("Login failure validation passed - error message displayed as expected");
    }
}
