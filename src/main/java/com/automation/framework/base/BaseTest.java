package com.automation.framework.base;

import com.automation.framework.config.ConfigManager;
import com.automation.framework.driver.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.lang.reflect.Method;

/**
 * Base Test class containing common test setup and teardown
 */
public abstract class BaseTest {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected final ConfigManager config = ConfigManager.getInstance();

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional String browser, Method method) {
        String testName = method.getName();
        String className = this.getClass().getSimpleName();
        
        logger.info("Setting up test: {}.{}", className, testName);
        
        // Override browser from parameter if provided
        if (browser != null && !browser.isEmpty()) {
            System.setProperty("browser", browser);
        }
        
        try {
            DriverManager.initializeDriver();
            logger.info("Test setup completed for: {}.{}", className, testName);
        } catch (Exception e) {
            logger.error("Failed to setup test: {}.{}", className, testName, e);
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(Method method) {
        String testName = method.getName();
        String className = this.getClass().getSimpleName();
        
        logger.info("Tearing down test: {}.{}", className, testName);
        
        try {
            DriverManager.quitDriver();
            logger.info("Test teardown completed for: {}.{}", className, testName);
        } catch (Exception e) {
            logger.error("Failed to teardown test: {}.{}", className, testName, e);
        }
    }

    /**
     * Log test step for better reporting
     */
    protected void logStep(String stepDescription) {
        logger.info("STEP: {}", stepDescription);
    }

    /**
     * Navigate to base URL
     */
    protected void navigateToBaseUrl() {
        String baseUrl = config.getBaseUrl();
        logger.info("Navigating to base URL: {}", baseUrl);
        DriverManager.getDriver().get(baseUrl);
    }

    /**
     * Get test data file path
     */
    protected String getTestDataPath(String fileName) {
        return config.getTestDataPath() + fileName;
    }
}