package com.automation.framework.utils;

import com.automation.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Utility class for WebDriver waits and conditions
 */
public class WaitUtils {
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);
    @SuppressWarnings("unused") // Used indirectly in lambda expressions
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor jsExecutor;
    private final ConfigManager config;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigManager.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitTimeout()));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    /**
     * Wait for element to be visible
     */
    public WebElement waitForElementToBeVisible(By locator) {
        logger.debug("Waiting for element to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be visible
     */
    public WebElement waitForElementToBeVisible(WebElement element) {
        logger.debug("Waiting for element to be visible: {}", element);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for elements to be visible
     */
    public List<WebElement> waitForElementsToBeVisible(By locator) {
        logger.debug("Waiting for elements to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /**
     * Wait for element to be clickable
     */
    public WebElement waitForElementToBeClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for element to be clickable
     */
    public WebElement waitForElementToBeClickable(WebElement element) {
        logger.debug("Waiting for element to be clickable: {}", element);
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait for element to be present in DOM
     */
    public WebElement waitForElementToBePresent(By locator) {
        logger.debug("Waiting for element to be present: {}", locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Wait for elements to be present in DOM
     */
    public List<WebElement> waitForElementsToBePresent(By locator) {
        logger.debug("Waiting for elements to be present: {}", locator);
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    /**
     * Wait for element to be invisible
     */
    public boolean waitForElementToBeInvisible(By locator) {
        logger.debug("Waiting for element to be invisible: {}", locator);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be invisible
     */
    public boolean waitForElementToBeInvisible(WebElement element) {
        logger.debug("Waiting for element to be invisible: {}", element);
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    /**
     * Wait for text to be present in element
     */
    public boolean waitForTextToBePresentInElement(By locator, String text) {
        logger.debug("Waiting for text '{}' to be present in element: {}", text, locator);
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Wait for text to be present in element
     */
    public boolean waitForTextToBePresentInElement(WebElement element, String text) {
        logger.debug("Waiting for text '{}' to be present in element: {}", text, element);
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    /**
     * Wait for attribute to contain value
     */
    public boolean waitForAttributeContains(By locator, String attribute, String value) {
        logger.debug("Waiting for attribute '{}' to contain '{}' in element: {}", attribute, value, locator);
        return wait.until(ExpectedConditions.attributeContains(locator, attribute, value));
    }

    /**
     * Wait for attribute to be
     */
    public boolean waitForAttributeToBe(By locator, String attribute, String value) {
        logger.debug("Waiting for attribute '{}' to be '{}' in element: {}", attribute, value, locator);
        return wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
    }

    /**
     * Wait for element to be selected
     */
    public boolean waitForElementToBeSelected(By locator) {
        logger.debug("Waiting for element to be selected: {}", locator);
        return wait.until(ExpectedConditions.elementToBeSelected(locator));
    }

    /**
     * Wait for element to be selected
     */
    public boolean waitForElementToBeSelected(WebElement element) {
        logger.debug("Waiting for element to be selected: {}", element);
        return wait.until(ExpectedConditions.elementToBeSelected(element));
    }

    /**
     * Wait for frame to be available and switch to it
     */
    public WebDriver waitForFrameAndSwitchToIt(By locator) {
        logger.debug("Waiting for frame and switching to it: {}", locator);
        return wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    /**
     * Wait for number of windows to be
     */
    public boolean waitForNumberOfWindowsToBe(int expectedNumberOfWindows) {
        logger.debug("Waiting for number of windows to be: {}", expectedNumberOfWindows);
        return wait.until(ExpectedConditions.numberOfWindowsToBe(expectedNumberOfWindows));
    }

    /**
     * Wait for URL to contain
     */
    public boolean waitForUrlContains(String fraction) {
        logger.debug("Waiting for URL to contain: {}", fraction);
        return wait.until(ExpectedConditions.urlContains(fraction));
    }

    /**
     * Wait for URL to be
     */
    public boolean waitForUrlToBe(String url) {
        logger.debug("Waiting for URL to be: {}", url);
        return wait.until(ExpectedConditions.urlToBe(url));
    }

    /**
     * Wait for title to contain
     */
    public boolean waitForTitleContains(String title) {
        logger.debug("Waiting for title to contain: {}", title);
        return wait.until(ExpectedConditions.titleContains(title));
    }

    /**
     * Wait for title to be
     */
    public boolean waitForTitleIs(String title) {
        logger.debug("Waiting for title to be: {}", title);
        return wait.until(ExpectedConditions.titleIs(title));
    }

    /**
     * Wait for page to load completely
     */
    public void waitForPageLoad() {
        logger.debug("Waiting for page to load completely");
        wait.until(webDriver -> jsExecutor.executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Wait for jQuery to load (if present)
     */
    public void waitForJQuery() {
        logger.debug("Waiting for jQuery to load");
        wait.until(webDriver -> {
            try {
                return (Boolean) jsExecutor.executeScript("return jQuery.active == 0");
            } catch (Exception e) {
                return true; // jQuery not present
            }
        });
    }

    /**
     * Wait for Angular to load (if present)
     */
    public void waitForAngular() {
        logger.debug("Waiting for Angular to load");
        wait.until(webDriver -> {
            try {
                return (Boolean) jsExecutor.executeScript(
                    "return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1"
                );
            } catch (Exception e) {
                return true; // Angular not present
            }
        });
    }

    /**
     * Custom wait with condition
     */
    public <T> T waitForCondition(java.util.function.Function<WebDriver, T> condition) {
        logger.debug("Waiting for custom condition");
        return wait.until(condition);
    }

    /**
     * Sleep for specified milliseconds
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
    }
}