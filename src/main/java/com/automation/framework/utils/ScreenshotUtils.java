package com.automation.framework.utils;

import com.automation.framework.driver.DriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for capturing screenshots
 */
public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "test-output/extent-reports/screenshots/";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    static {
        createScreenshotDirectory();
    }

    /**
     * Create screenshot directory if it doesn't exist
     */
    private static void createScreenshotDirectory() {
        File dir = new File(SCREENSHOT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            logger.info("Created screenshot directory: {}", SCREENSHOT_DIR);
        }
    }

    /**
     * Capture full page screenshot
     */
    public static String captureScreenshot(String testName) {
        try {
            WebDriver driver = DriverManager.getDriver();
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            String timestamp = LocalDateTime.now().format(formatter);
            String fileName = String.format("%s_%s.png", testName, timestamp);
            String filePath = SCREENSHOT_DIR + fileName;
            
            File destinationFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destinationFile);
            
            logger.info("Screenshot captured: {}", filePath);
            return filePath;
        } catch (IOException e) {
            logger.error("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }

    /**
     * Capture screenshot with custom name
     */
    public static String captureScreenshot(String testName, String description) {
        try {
            WebDriver driver = DriverManager.getDriver();
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            String timestamp = LocalDateTime.now().format(formatter);
            String fileName = String.format("%s_%s_%s.png", testName, description, timestamp);
            String filePath = SCREENSHOT_DIR + fileName;
            
            File destinationFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destinationFile);
            
            logger.info("Screenshot captured: {}", filePath);
            return filePath;
        } catch (IOException e) {
            logger.error("Failed to capture screenshot for test: {} - {}", testName, description, e);
            return null;
        }
    }

    /**
     * Capture element screenshot
     */
    public static String captureElementScreenshot(WebElement element, String testName, String elementName) {
        try {
            File sourceFile = element.getScreenshotAs(OutputType.FILE);
            
            String timestamp = LocalDateTime.now().format(formatter);
            String fileName = String.format("%s_%s_%s.png", testName, elementName, timestamp);
            String filePath = SCREENSHOT_DIR + fileName;
            
            File destinationFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destinationFile);
            
            logger.info("Element screenshot captured: {}", filePath);
            return filePath;
        } catch (IOException e) {
            logger.error("Failed to capture element screenshot for test: {} - element: {}", testName, elementName, e);
            return null;
        }
    }

    /**
     * Get screenshot as byte array for reporting
     */
    public static byte[] getScreenshotAsBytes() {
        try {
            WebDriver driver = DriverManager.getDriver();
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as bytes", e);
            return new byte[0];
        }
    }

    /**
     * Get screenshot as base64 string for reporting
     */
    public static String getScreenshotAsBase64() {
        try {
            WebDriver driver = DriverManager.getDriver();
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as base64", e);
            return "";
        }
    }
}