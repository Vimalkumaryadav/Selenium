package com.automation.framework.listeners;

import com.automation.framework.config.ConfigManager;
import com.automation.framework.reporting.ExtentReportsManager;
import com.automation.framework.utils.ScreenshotUtils;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ITestContext;
import org.testng.ISuiteListener;
import org.testng.ISuite;

import java.io.File;

/**
 * TestNG Listener for basic test execution logging, screenshot capture, and ExtentReports integration
 */
public class TestListener implements ITestListener, ISuiteListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);
    private final ConfigManager config = ConfigManager.getInstance();

    @Override
    public void onStart(ISuite suite) {
        logger.info("=== Test Suite Started: {} ===", suite.getName());
        // ExtentReports initialization handled by ExtentReportsManager
    }

    @Override
    public void onFinish(ISuite suite) {
        logger.info("=== Test Suite Finished: {} ===", suite.getName());
        // Flush ExtentReports
        if (config.isExtentReportsEnabled()) {
            ExtentReportsManager.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        String description = result.getMethod().getDescription();
        
        logger.info("Starting test: {}.{}", className, testName);
        
        // Start ExtentReports test if enabled
        if (config.isExtentReportsEnabled()) {
            String testDescription = (description != null && !description.isEmpty()) ? 
                                   description : "Test method: " + testName;
            ExtentReportsManager.startTest(testName, testDescription);
            
            // Add test metadata
            addTestMetadata(result);
            
            // Log test start in ExtentReports
            ExtentReportsManager.logStep(LogStatus.INFO, "Test started: " + testName);
            ExtentReportsManager.logStep(LogStatus.INFO, "Test class: " + className);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        logger.info("Test passed: {}.{}", className, testName);
        
        // ExtentReports logging
        if (config.isExtentReportsEnabled()) {
            ExtentReportsManager.logStep(LogStatus.PASS, "Test passed successfully");
            long duration = result.getEndMillis() - result.getStartMillis();
            ExtentReportsManager.logStep(LogStatus.INFO, "Execution time: " + duration + " ms");
        }
        
        if (config.isScreenshotOnPass()) {
            captureScreenshot(result, "PASSED");
        }
        
        // End ExtentReports test
        if (config.isExtentReportsEnabled()) {
            ExtentReportsManager.endTest();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        Throwable throwable = result.getThrowable();
        
        logger.error("Test failed: {}.{}", className, testName);
        if (throwable != null) {
            logger.error("Failure reason: {}", throwable.getMessage());
        }
        
        // ExtentReports logging
        if (config.isExtentReportsEnabled()) {
            String errorMessage = throwable != null ? throwable.getMessage() : "Test failed with unknown error";
            ExtentReportsManager.logStep(LogStatus.FAIL, "Test failed: " + errorMessage);
            
            if (throwable != null) {
                String stackTrace = getStackTrace(throwable);
                ExtentReportsManager.logStep(LogStatus.FAIL, "Stack trace: <pre>" + stackTrace + "</pre>");
            }
            
            long duration = result.getEndMillis() - result.getStartMillis();
            ExtentReportsManager.logStep(LogStatus.INFO, "Execution time: " + duration + " ms");
        }
        
        if (config.isScreenshotOnFailure()) {
            captureScreenshot(result, "FAILED");
        }
        
        // End ExtentReports test
        if (config.isExtentReportsEnabled()) {
            ExtentReportsManager.endTest();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        Throwable throwable = result.getThrowable();
        
        logger.warn("Test skipped: {}.{}", className, testName);
        
        // ExtentReports logging
        if (config.isExtentReportsEnabled()) {
            // Start test if not already started
            if (ExtentReportsManager.getTest() == null) {
                String description = result.getMethod().getDescription();
                ExtentReportsManager.startTest(testName, description != null ? description : "Test skipped");
                addTestMetadata(result);
            }
            
            String skipReason = throwable != null ? throwable.getMessage() : "Test skipped";
            ExtentReportsManager.logStep(LogStatus.SKIP, "Test skipped: " + skipReason);
            ExtentReportsManager.endTest();
        }
    }

    /**
     * Capture screenshot for test reporting
     */
    private void captureScreenshot(ITestResult result, String status) {
        try {
            String testName = result.getMethod().getMethodName();
            String screenshotPath = ScreenshotUtils.captureScreenshot(testName, status);
            
            if (screenshotPath != null) {
                logger.info("Screenshot captured for test: {}", testName);
                
                // Attach screenshot to ExtentReports if enabled
                if (config.isExtentReportsEnabled()) {
                    String relativePath = getRelativeScreenshotPath(screenshotPath);
                    ExtentReportsManager.logStepWithScreenshot(
                        LogStatus.INFO, 
                        "Screenshot captured", 
                        relativePath
                    );
                }
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for test result", e);
            if (config.isExtentReportsEnabled()) {
                ExtentReportsManager.logStep(LogStatus.WARNING, "Failed to capture screenshot: " + e.getMessage());
            }
        }
    }
    
    /**
     * Convert absolute screenshot path to relative path for ExtentReports
     * The HTML report is in test-output/extent-reports/, so screenshots should be relative to that location
     */
    private String getRelativeScreenshotPath(String absolutePath) {
        try {
            // Extract just the filename from the path
            String fileName = absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1);
            // Return relative path from report location to screenshots directory
            return "./screenshots/" + fileName;
        } catch (Exception e) {
            logger.warn("Failed to convert screenshot path to relative: {}", e.getMessage());
            return absolutePath;
        }
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Suite started: {}", context.getName());
    }

    @Override 
    public void onFinish(ITestContext context) {
        logger.info("Test Suite finished: {}", context.getName());
        logger.info("Tests run: {}, Passed: {}, Failed: {}, Skipped: {}", 
                   context.getAllTestMethods().length,
                   context.getPassedTests().size(),
                   context.getFailedTests().size(), 
                   context.getSkippedTests().size());
    }
    
    /**
     * Add test metadata like categories and author for ExtentReports
     */
    private void addTestMetadata(ITestResult result) {
        if (!config.isExtentReportsEnabled()) {
            return;
        }
        
        String className = result.getTestClass().getName();
        
        // Extract package name as category
        String packageName = className.substring(className.lastIndexOf('.') + 1);
        ExtentReportsManager.assignCategory(packageName);
        
        // Check for test groups and add as categories
        String[] groups = result.getMethod().getGroups();
        if (groups != null && groups.length > 0) {
            for (String group : groups) {
                ExtentReportsManager.assignCategory(group);
            }
        }
        
        // Add author
        ExtentReportsManager.assignAuthor("Test Team");
    }
    
    /**
     * Get formatted stack trace
     */
    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}

