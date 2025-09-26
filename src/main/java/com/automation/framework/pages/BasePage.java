package com.automation.framework.pages;

import com.automation.framework.config.ConfigManager;
import com.automation.framework.driver.DriverManager;
import com.automation.framework.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Base Page class containing common page operations and utilities
 */
public abstract class BasePage {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected final WebDriver driver;
    protected final WaitUtils waitUtils;
    protected final Actions actions;
    protected final JavascriptExecutor jsExecutor;
    protected final ConfigManager config;

    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.waitUtils = new WaitUtils(driver);
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
        this.config = ConfigManager.getInstance();
        PageFactory.initElements(driver, this);
    }

    /**
     * Abstract method to verify if page is loaded
     */
    public abstract boolean isPageLoaded();

    /**
     * Navigate to URL
     */
    public void navigateTo(String url) {
        logger.info("Navigating to URL: {}", url);
        driver.get(url);
        waitForPageLoad();
    }

    /**
     * Get current page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Get current page URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Click element
     */
    protected void click(WebElement element) {
        waitUtils.waitForElementToBeClickable(element);
        element.click();
        logger.debug("Clicked element: {}", element);
    }

    /**
     * Click element by locator
     */
    protected void click(By locator) {
        WebElement element = waitUtils.waitForElementToBeClickable(locator);
        element.click();
        logger.debug("Clicked element: {}", locator);
    }

    /**
     * Type text into element
     */
    protected void type(WebElement element, String text) {
        waitUtils.waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(text);
        logger.debug("Typed '{}' into element: {}", text, element);
    }

    /**
     * Type text into element by locator
     */
    protected void type(By locator, String text) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        element.clear();
        element.sendKeys(text);
        logger.debug("Typed '{}' into element: {}", text, locator);
    }

    /**
     * Get text from element
     */
    protected String getText(WebElement element) {
        waitUtils.waitForElementToBeVisible(element);
        String text = element.getText();
        logger.debug("Got text '{}' from element: {}", text, element);
        return text;
    }

    /**
     * Get text from element by locator
     */
    protected String getText(By locator) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        String text = element.getText();
        logger.debug("Got text '{}' from element: {}", text, locator);
        return text;
    }

    /**
     * Get attribute value from element
     */
    protected String getAttribute(WebElement element, String attribute) {
        waitUtils.waitForElementToBeVisible(element);
        return element.getAttribute(attribute);
    }

    /**
     * Check if element is displayed
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if element is displayed by locator
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if element is enabled
     */
    protected boolean isEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if element is selected
     */
    protected boolean isSelected(WebElement element) {
        try {
            return element.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for element to be visible
     */
    protected WebElement waitForElement(By locator) {
        return waitUtils.waitForElementToBeVisible(locator);
    }

    /**
     * Wait for elements to be visible
     */
    protected List<WebElement> waitForElements(By locator) {
        return waitUtils.waitForElementsToBeVisible(locator);
    }

    /**
     * Select dropdown option by visible text
     */
    protected void selectByText(WebElement element, String text) {
        Select select = new Select(element);
        select.selectByVisibleText(text);
        logger.debug("Selected '{}' from dropdown: {}", text, element);
    }

    /**
     * Select dropdown option by value
     */
    protected void selectByValue(WebElement element, String value) {
        Select select = new Select(element);
        select.selectByValue(value);
        logger.debug("Selected value '{}' from dropdown: {}", value, element);
    }

    /**
     * Select dropdown option by index
     */
    protected void selectByIndex(WebElement element, int index) {
        Select select = new Select(element);
        select.selectByIndex(index);
        logger.debug("Selected index '{}' from dropdown: {}", index, element);
    }

    /**
     * Hover over element
     */
    protected void hover(WebElement element) {
        actions.moveToElement(element).perform();
        logger.debug("Hovered over element: {}", element);
    }

    /**
     * Double click element
     */
    protected void doubleClick(WebElement element) {
        actions.doubleClick(element).perform();
        logger.debug("Double clicked element: {}", element);
    }

    /**
     * Right click element
     */
    protected void rightClick(WebElement element) {
        actions.contextClick(element).perform();
        logger.debug("Right clicked element: {}", element);
    }

    /**
     * Scroll to element
     */
    protected void scrollToElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
        logger.debug("Scrolled to element: {}", element);
    }

    /**
     * Scroll by pixels
     */
    protected void scrollBy(int x, int y) {
        jsExecutor.executeScript("window.scrollBy(" + x + "," + y + ");");
        logger.debug("Scrolled by x:{}, y:{}", x, y);
    }

    /**
     * Execute JavaScript
     */
    protected Object executeScript(String script, Object... args) {
        return jsExecutor.executeScript(script, args);
    }

    /**
     * Wait for page load to complete
     */
    protected void waitForPageLoad() {
        waitUtils.waitForPageLoad();
    }

    /**
     * Refresh the page
     */
    protected void refreshPage() {
        logger.info("Refreshing page");
        driver.navigate().refresh();
        waitForPageLoad();
    }

    /**
     * Navigate back
     */
    protected void goBack() {
        logger.info("Navigating back");
        driver.navigate().back();
        waitForPageLoad();
    }

    /**
     * Navigate forward
     */
    protected void goForward() {
        logger.info("Navigating forward");
        driver.navigate().forward();
        waitForPageLoad();
    }
}