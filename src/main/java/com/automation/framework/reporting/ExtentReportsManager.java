package com.automation.framework.reporting;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.NetworkMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ExtentReports 2.0 Manager Class
 * Manages ExtentReports initialization, test creation, and report finalization
 */
public class ExtentReportsManager {
    
    private static final Logger logger = LogManager.getLogger(ExtentReportsManager.class);
    private static ExtentReports extent;
    private static Map<Long, ExtentTest> testMap = new HashMap<>();
    private static String reportPath;
    private static String screenshotPath;
    
    // Singleton pattern to ensure single instance
    private ExtentReportsManager() {}
    
    /**
     * Initialize ExtentReports with configuration
     */
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }
    
    /**
     * Create ExtentReports instance with proper configuration
     */
    private static void createInstance() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String baseDir = System.getProperty("user.dir");
        reportPath = baseDir + File.separator + "test-output" + File.separator + 
                    "extent-reports" + File.separator + "ExtentReport_" + timestamp + ".html";
        screenshotPath = baseDir + File.separator + "test-output" + File.separator + 
                        "extent-reports" + File.separator + "screenshots";
        
        // Create directories if they don't exist
        createDirectories();
        
        // Initialize ExtentReports
        extent = new ExtentReports(reportPath, true, DisplayOrder.OLDEST_FIRST, NetworkMode.OFFLINE);
        
        // Load configuration from XML file
        String configPath = baseDir + File.separator + "src" + File.separator + "test" + 
                           File.separator + "resources" + File.separator + "extent-config.xml";
        File configFile = new File(configPath);
        try {
            if (configFile.exists()) {
                extent.loadConfig(configFile);
                logger.info("ExtentReports configuration loaded from: {}", configPath);
            } else {
                logger.warn("ExtentReports configuration file not found: {}", configPath);
                setDefaultConfiguration();
            }
        } catch (Exception e) {
            logger.error("Failed to load ExtentReports configuration from {}: {}", configPath, e.getMessage());
            logger.info("Using default configuration instead");
            setDefaultConfiguration();
        }
        
        // Add system information
        addSystemInformation();
        
        logger.info("ExtentReports initialized successfully");
        logger.info("Report path: {}", reportPath);
    }
    
    /**
     * Set default configuration if XML config file is not found
     */
    private static void setDefaultConfiguration() {
        try {
            // Set default configuration programmatically
            extent.addSystemInfo("Environment", System.getProperty("environment", "Test"));
            extent.addSystemInfo("User Name", System.getProperty("user.name"));
            extent.addSystemInfo("Java Version", System.getProperty("java.version"));
            extent.addSystemInfo("OS", System.getProperty("os.name"));
            extent.addSystemInfo("Browser", System.getProperty("browser", "Chrome"));
            
            // Note: Document title and report name are set through XML config or constructor
                   
            logger.info("Default ExtentReports configuration applied successfully");
        } catch (Exception e) {
            logger.error("Failed to set default configuration: {}", e.getMessage());
        }
    }
    
    /**
     * Add system information to the report
     */
    private static void addSystemInformation() {
        extent.addSystemInfo("Framework", "Selenium TestNG");
        extent.addSystemInfo("ExtentReports Version", "2.41.2");
        extent.addSystemInfo("Automation Engineer", "Test Team");
        extent.addSystemInfo("Environment", System.getProperty("environment", "Test"));
        extent.addSystemInfo("User Name", System.getProperty("user.name"));
        extent.addSystemInfo("Java Version", System.getProperty("java.version"));
        extent.addSystemInfo("OS", System.getProperty("os.name"));
        extent.addSystemInfo("Browser", System.getProperty("browser", "Chrome"));
        extent.addSystemInfo("Report Generated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
    
    /**
     * Create test directories
     */
    private static void createDirectories() {
        try {
            File reportDir = new File(reportPath).getParentFile();
            if (!reportDir.exists()) {
                reportDir.mkdirs();
                logger.info("Created report directory: {}", reportDir.getAbsolutePath());
            }
            
            File screenshotDir = new File(screenshotPath);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
                logger.info("Created screenshot directory: {}", screenshotDir.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.error("Error creating directories: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Start a new test
     * @param testName Name of the test
     * @param description Description of the test
     * @return ExtentTest instance
     */
    public static synchronized ExtentTest startTest(String testName, String description) {
        ExtentTest test = getInstance().startTest(testName, description);
        testMap.put(Thread.currentThread().getId(), test);
        logger.debug("Started test: {} - {}", testName, description);
        return test;
    }
    
    /**
     * Get current test instance for the thread
     * @return Current ExtentTest instance
     */
    public static synchronized ExtentTest getTest() {
        return testMap.get(Thread.currentThread().getId());
    }
    
    /**
     * End current test
     */
    public static synchronized void endTest() {
        ExtentTest test = testMap.get(Thread.currentThread().getId());
        if (test != null) {
            getInstance().endTest(test);
            testMap.remove(Thread.currentThread().getId());
            logger.debug("Ended test for thread: {}", Thread.currentThread().getId());
        }
    }
    
    /**
     * Log test step with status
     * @param status LogStatus (PASS, FAIL, SKIP, INFO)
     * @param stepDescription Description of the step
     */
    public static void logStep(LogStatus status, String stepDescription) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(status, stepDescription);
            logger.debug("Logged step: {} - {}", status, stepDescription);
        }
    }
    
    /**
     * Log test step with screenshot
     * @param status LogStatus
     * @param stepDescription Description of the step
     * @param screenshotPath Path to screenshot
     */
    public static void logStepWithScreenshot(LogStatus status, String stepDescription, String screenshotPath) {
        ExtentTest test = getTest();
        if (test != null) {
            try {
                // Convert absolute path to relative path for ExtentReports
                String relativePath = convertToRelativePath(screenshotPath);
                String screenshotRef = test.addScreenCapture(relativePath);
                test.log(status, stepDescription + screenshotRef);
                logger.debug("Logged step with screenshot: {} - {} (relative path: {})", status, stepDescription, relativePath);
            } catch (Exception e) {
                logger.error("Failed to attach screenshot to ExtentReports: {}", e.getMessage());
                test.log(status, stepDescription + " [Screenshot capture failed: " + e.getMessage() + "]");
            }
        }
    }
    
    /**
     * Convert screenshot path to relative path for ExtentReports
     * The HTML report is in test-output/extent-reports/, so screenshots should be relative to that location
     */
    private static String convertToRelativePath(String screenshotPath) {
        try {
            // Handle both absolute and relative paths
            String fileName;
            if (screenshotPath.contains(File.separator)) {
                // Extract filename from path (works for both absolute and relative paths)
                fileName = screenshotPath.substring(screenshotPath.lastIndexOf(File.separator) + 1);
            } else if (screenshotPath.contains("/")) {
                // Handle forward slash paths
                fileName = screenshotPath.substring(screenshotPath.lastIndexOf("/") + 1);
            } else {
                // It's already just a filename
                fileName = screenshotPath;
            }
            
            // Return relative path from report location to screenshots directory
            return "./screenshots/" + fileName;
        } catch (Exception e) {
            logger.warn("Failed to convert screenshot path to relative: {}", e.getMessage());
            return screenshotPath;
        }
    }
    
    /**
     * Assign category to current test
     * @param category Category name
     */
    public static void assignCategory(String category) {
        ExtentTest test = getTest();
        if (test != null) {
            test.assignCategory(category);
            logger.debug("Assigned category: {}", category);
        }
    }
    
    /**
     * Assign author to current test
     * @param author Author name
     */
    public static void assignAuthor(String author) {
        ExtentTest test = getTest();
        if (test != null) {
            test.assignAuthor(author);
            logger.debug("Assigned author: {}", author);
        }
    }
    
    /**
     * Flush and close the ExtentReports
     */
    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            extent.close();
            logger.info("ExtentReports flushed and closed");
            logger.info("Report available at: {}", reportPath);
        }
    }
    
    /**
     * Get report file path
     * @return Report file path
     */
    public static String getReportPath() {
        return reportPath;
    }
    
    /**
     * Get screenshot directory path
     * @return Screenshot directory path
     */
    public static String getScreenshotPath() {
        return screenshotPath;
    }
}