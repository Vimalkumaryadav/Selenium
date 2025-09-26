package com.automation.framework.listeners;

import com.automation.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry Analyzer for failed tests
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private final ConfigManager config = ConfigManager.getInstance();
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetryCount = config.getRetryCount();
        
        if (retryCount < maxRetryCount) {
            retryCount++;
            String testName = result.getMethod().getMethodName();
            logger.warn("Retrying failed test: {} (Attempt {}/{})", testName, retryCount, maxRetryCount);
            return true;
        }
        
        return false;
    }
}