package com.automation.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration Manager for handling application properties
 */
public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Properties properties;

    private ConfigManager() {
        loadProperties();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        String configPath = "src/main/resources/config/application.properties";
        
        try (FileInputStream fis = new FileInputStream(configPath)) {
            properties.load(fis);
            logger.info("Configuration loaded successfully from: {}", configPath);
        } catch (IOException e) {
            logger.error("Failed to load configuration from: {}", configPath, e);
            throw new RuntimeException("Configuration file not found: " + configPath, e);
        }
    }

    public String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Browser Configuration
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public boolean isBrowserHeadless() {
        return getBooleanProperty("browser.headless");
    }

    public boolean isBrowserMaximize() {
        return getBooleanProperty("browser.maximize");
    }

    public boolean isBrowserIncognito() {
        return getBooleanProperty("browser.incognito");
    }

    public int getBrowserWindowWidth() {
        return getIntProperty("browser.window.width", 1920);
    }

    public int getBrowserWindowHeight() {
        return getIntProperty("browser.window.height", 1080);
    }

    // Test Configuration
    public int getThreadCount() {
        return getIntProperty("test.thread.count", 1);
    }

    public int getRetryCount() {
        return getIntProperty("test.retry.count", 1);
    }

    public int getImplicitTimeout() {
        return getIntProperty("test.timeout.implicit", 10);
    }

    public int getExplicitTimeout() {
        return getIntProperty("test.timeout.explicit", 30);
    }

    public int getPageLoadTimeout() {
        return getIntProperty("test.timeout.page.load", 60);
    }

    // Environment Configuration
    public String getBaseUrl() {
        return getProperty("base.url", "https://www.saucedemo.com/v1/index.html");
    }

    public String getEnvironment() {
        return getProperty("environment", "dev");
    }

    // Reporting Configuration
    public boolean isExtentReportsEnabled() {
        return getBooleanProperty("reports.extent.enabled");
    }
    
    public boolean isScreenshotsEnabled() {
        return getBooleanProperty("reports.screenshots.enabled");
    }

    public boolean isScreenshotOnFailure() {
        return getBooleanProperty("reports.screenshots.on.failure");
    }

    public boolean isScreenshotOnPass() {
        return getBooleanProperty("reports.screenshots.on.pass");
    }

    // Test Data Configuration
    public String getTestDataPath() {
        return getProperty("testdata.path", "src/test/resources/testdata/");
    }

    public String getTestDataFormat() {
        return getProperty("testdata.format", "json");
    }

    // Driver Management Configuration
    public boolean isDriverAutoDownload() {
        return getBooleanProperty("driver.auto.download", true);
    }

    public boolean isDriverOfflineMode() {
        return getBooleanProperty("driver.offline.mode", false);
    }

    public String getDriverCachePath() {
        return getProperty("driver.cache.path", "./drivers/.cache");
    }

    public String getDriverLocalPath() {
        return getProperty("driver.local.path", "./drivers");
    }

    public int getDriverDownloadTimeout() {
        return getIntProperty("driver.download.timeout", 30);
    }

    // Proxy Configuration
    public boolean isProxyEnabled() {
        return getBooleanProperty("proxy.enabled", false);
    }

    public String getProxyHost() {
        return getProperty("proxy.host", "");
    }

    public int getProxyPort() {
        return getIntProperty("proxy.port", 8080);
    }

    public String getProxyUsername() {
        return getProperty("proxy.username", "");
    }

    public String getProxyPassword() {
        return getProperty("proxy.password", "");
    }

    public String getNonProxyHosts() {
        return getProperty("proxy.non.proxy.hosts", "localhost|127.0.0.1");
    }

    // WebDriverManager Configuration
    public int getWebDriverManagerTimeout() {
        return getIntProperty("webdrivermanager.timeout", 30);
    }

    public boolean isWebDriverManagerCacheEnabled() {
        return getBooleanProperty("webdrivermanager.cache.enabled", true);
    }

    public boolean isWebDriverManagerOfflineEnabled() {
        return getBooleanProperty("webdrivermanager.offline.enabled", false);
    }

    public boolean isWebDriverManagerForceCache() {
        return getBooleanProperty("webdrivermanager.force.cache", false);
    }
}