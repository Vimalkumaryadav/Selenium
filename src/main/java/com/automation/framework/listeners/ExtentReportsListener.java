package com.automation.framework.listeners;

import com.automation.framework.reporting.ExtentReportsManager;
import com.automation.framework.utils.ScreenshotUtils;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

import java.io.File;

/**
 * ExtentReports 2.0 TestNG Listener
 * Integrates ExtentReports with TestNG test execution lifecycle
 */
public class ExtentReportsListener implements ITestListener, ISuiteListener {
    
    private static final Logger logger = LogManager.getLogger(ExtentReportsListener.class);
    
    @Override
    public void onStart(ISuite suite) {
        logger.info("=== Test Suite Started: {} ===", suite.getName());
        // ExtentReports is initialized lazily when first test starts
    }
    
    @Override
    public void onFinish(ISuite suite) {
        logger.info("=== Test Suite Finished: {} ===", suite.getName());
        // Flush reports when suite finishes
        ExtentReportsManager.flush();
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String testDescription = result.getMethod().getDescription();
        String className = result.getTestClass().getName();
        
        // Use method description if available, otherwise use method name
        String description = (testDescription != null && !testDescription.isEmpty()) ? 
                            testDescription : "Test method: " + testName;
        
        // Start test in ExtentReports
        ExtentReportsManager.startTest(testName, description);
        
        // Add test categories and author information
        addTestMetadata(result);
        
        // Log test start
        ExtentReportsManager.logStep(LogStatus.INFO, "Test started: " + testName);
        ExtentReportsManager.logStep(LogStatus.INFO, "Test class: " + className);
        
        logger.info("Test started: {}.{}", className, testName);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        // Log success
        ExtentReportsManager.logStep(LogStatus.PASS, "Test passed successfully");
        
        // Add execution time
        long duration = result.getEndMillis() - result.getStartMillis();
        ExtentReportsManager.logStep(LogStatus.INFO, "Execution time: " + duration + " ms");
        
        // Capture screenshot on pass if configured
        if (shouldCaptureScreenshotOnPass()) {
            captureAndAttachScreenshot(result, "PASS");
        }
        
        // End test
        ExtentReportsManager.endTest();
        
        logger.info("Test passed: {}.{}", className, testName);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        Throwable throwable = result.getThrowable();
        
        // Log failure
        String errorMessage = throwable != null ? throwable.getMessage() : "Test failed with unknown error";
        ExtentReportsManager.logStep(LogStatus.FAIL, "Test failed: " + errorMessage);
        
        // Log stack trace
        if (throwable != null) {
            String stackTrace = getStackTrace(throwable);
            ExtentReportsManager.logStep(LogStatus.FAIL, "Stack trace: <pre>" + stackTrace + "</pre>");
        }
        
        // Add execution time
        long duration = result.getEndMillis() - result.getStartMillis();
        ExtentReportsManager.logStep(LogStatus.INFO, "Execution time: " + duration + " ms");
        
        // Capture screenshot on failure
        captureAndAttachScreenshot(result, "FAIL");
        
        // End test
        ExtentReportsManager.endTest();
        
        logger.error("Test failed: {}.{} - {}", className, testName, errorMessage);
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        Throwable throwable = result.getThrowable();
        
        // Start test for skipped case (in case it wasn't started)
        if (ExtentReportsManager.getTest() == null) {
            String description = result.getMethod().getDescription();
            ExtentReportsManager.startTest(testName, description != null ? description : "Test skipped");
            addTestMetadata(result);
        }
        
        // Log skip reason
        String skipReason = throwable != null ? throwable.getMessage() : "Test skipped";
        ExtentReportsManager.logStep(LogStatus.SKIP, "Test skipped: " + skipReason);
        
        // End test
        ExtentReportsManager.endTest();
        
        logger.warn("Test skipped: {}.{} - {}", className, testName, skipReason);
    }
    
    /**
     * Add test metadata like categories and author
     */
    private void addTestMetadata(ITestResult result) {
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
        
        // Add author (you can customize this based on your needs)
        ExtentReportsManager.assignAuthor("Test Team");
    }
    
    /**
     * Capture screenshot and attach to report
     */
    private void captureAndAttachScreenshot(ITestResult result, String status) {
        try {
            String testName = result.getMethod().getMethodName();
            String screenshotPath = ScreenshotUtils.captureScreenshot(testName, status);
            
            if (screenshotPath != null) {
                // Convert absolute path to relative path for report
                String relativePath = getRelativeScreenshotPath(screenshotPath);
                ExtentReportsManager.logStepWithScreenshot(
                    LogStatus.INFO, 
                    "Screenshot captured", 
                    relativePath
                );
                logger.debug("Screenshot captured and attached: {}", screenshotPath);
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage(), e);
            ExtentReportsManager.logStep(LogStatus.WARNING, "Failed to capture screenshot: " + e.getMessage());
        }
    }
    
    /**
     * Convert absolute screenshot path to relative path for report
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
    
    /**
     * Check if screenshot should be captured on pass
     */
    private boolean shouldCaptureScreenshotOnPass() {
        String captureOnPass = System.getProperty("reports.screenshots.on.pass", "false");
        return Boolean.parseBoolean(captureOnPass);
    }
}