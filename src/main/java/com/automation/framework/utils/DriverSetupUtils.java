package com.automation.framework.utils;

import com.automation.framework.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for setting up drivers offline and managing driver downloads
 */
public class DriverSetupUtils {
    private static final Logger logger = LogManager.getLogger(DriverSetupUtils.class);
    private static final ConfigManager config = ConfigManager.getInstance();

    private DriverSetupUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Setup all drivers for offline use
     */
    public static void setupAllDriversOffline() {
        logger.info("Setting up all drivers for offline use...");
        
        createDriversDirectory();
        configureWebDriverManagerGlobally();
        
        // Try to download each driver
        downloadChromeDriver();
        downloadFirefoxDriver();
        downloadEdgeDriver();
        
        logger.info("Driver setup completed");
    }

    /**
     * Create drivers directory if it doesn't exist
     */
    private static void createDriversDirectory() {
        try {
            String driversPath = config.getDriverLocalPath();
            Path driverDir = Paths.get(driversPath);
            if (!Files.exists(driverDir)) {
                Files.createDirectories(driverDir);
                logger.info("Created drivers directory: {}", driversPath);
            }

            String cachePath = config.getDriverCachePath();
            Path cacheDir = Paths.get(cachePath);
            if (!Files.exists(cacheDir)) {
                Files.createDirectories(cacheDir);
                logger.info("Created cache directory: {}", cachePath);
            }
        } catch (IOException e) {
            logger.error("Failed to create drivers directory", e);
        }
    }

    /**
     * Configure WebDriverManager globally for offline operation
     */
    public static void configureWebDriverManagerGlobally() {
        // Set system properties for WebDriverManager
        System.setProperty("wdm.cachePath", config.getDriverCachePath());
        System.setProperty("wdm.timeout", String.valueOf(config.getWebDriverManagerTimeout()));
        System.setProperty("wdm.avoidBrowserDetection", "true");
        
        if (config.isWebDriverManagerOfflineEnabled()) {
            System.setProperty("wdm.avoidDownload", "true");
            System.setProperty("wdm.avoidFallback", "false");
        }

        if (config.isWebDriverManagerForceCache()) {
            System.setProperty("wdm.forceCache", "true");
        }

        // Configure proxy if enabled
        if (config.isProxyEnabled()) {
            String proxyHost = config.getProxyHost();
            int proxyPort = config.getProxyPort();
            
            if (!proxyHost.isEmpty()) {
                System.setProperty("wdm.proxy", proxyHost + ":" + proxyPort);
                System.setProperty("http.proxyHost", proxyHost);
                System.setProperty("http.proxyPort", String.valueOf(proxyPort));
                System.setProperty("https.proxyHost", proxyHost);
                System.setProperty("https.proxyPort", String.valueOf(proxyPort));
                
                String nonProxyHosts = config.getNonProxyHosts();
                if (!nonProxyHosts.isEmpty()) {
                    System.setProperty("http.nonProxyHosts", nonProxyHosts);
                    System.setProperty("https.nonProxyHosts", nonProxyHosts);
                }
                
                logger.info("Configured proxy: {}:{}", proxyHost, proxyPort);
            }
        }

        logger.info("WebDriverManager configured globally");
    }

    /**
     * Download ChromeDriver
     */
    private static void downloadChromeDriver() {
        try {
            if (!isInternetAvailable()) {
                logger.warn("No internet connection, skipping ChromeDriver download");
                return;
            }

            WebDriverManager chromeManager = WebDriverManager.chromedriver();
            chromeManager.cachePath(config.getDriverCachePath());
            chromeManager.timeout(config.getDriverDownloadTimeout());
            chromeManager.setup();
            
            logger.info("ChromeDriver downloaded successfully");
        } catch (Exception e) {
            logger.warn("Failed to download ChromeDriver: {}", e.getMessage());
        }
    }

    /**
     * Download FirefoxDriver
     */
    private static void downloadFirefoxDriver() {
        try {
            if (!isInternetAvailable()) {
                logger.warn("No internet connection, skipping FirefoxDriver download");
                return;
            }

            WebDriverManager firefoxManager = WebDriverManager.firefoxdriver();
            firefoxManager.cachePath(config.getDriverCachePath());
            firefoxManager.timeout(config.getDriverDownloadTimeout());
            firefoxManager.setup();
            
            logger.info("FirefoxDriver downloaded successfully");
        } catch (Exception e) {
            logger.warn("Failed to download FirefoxDriver: {}", e.getMessage());
        }
    }

    /**
     * Download EdgeDriver
     */
    private static void downloadEdgeDriver() {
        try {
            if (!isInternetAvailable()) {
                logger.warn("No internet connection, skipping EdgeDriver download");
                return;
            }

            WebDriverManager edgeManager = WebDriverManager.edgedriver();
            edgeManager.cachePath(config.getDriverCachePath());
            edgeManager.timeout(config.getDriverDownloadTimeout());
            edgeManager.setup();
            
            logger.info("EdgeDriver downloaded successfully");
        } catch (Exception e) {
            logger.warn("Failed to download EdgeDriver: {}", e.getMessage());
        }
    }

    /**
     * Check if internet connection is available
     */
    public static boolean isInternetAvailable() {
        try {
            URL url = new URL("https://www.saucedemo.com/v1/index.html");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            logger.debug("Internet connection check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if driver exists locally
     */
    public static boolean isDriverAvailableLocally(String driverName) {
        String localPath = config.getDriverLocalPath() + "/" + driverName;
        File driverFile = new File(localPath);
        
        if (driverFile.exists() && driverFile.canExecute()) {
            logger.info("Found local driver: {}", localPath);
            return true;
        }

        // Check in cache directory
        String cachePath = config.getDriverCachePath() + "/" + driverName;
        File cachedDriver = new File(cachePath);
        
        if (cachedDriver.exists() && cachedDriver.canExecute()) {
            logger.info("Found cached driver: {}", cachePath);
            return true;
        }

        return false;
    }

    /**
     * Get driver path (local or cached)
     */
    public static String getDriverPath(String driverName) {
        String localPath = config.getDriverLocalPath() + "/" + driverName;
        if (new File(localPath).exists()) {
            return localPath;
        }

        String cachePath = config.getDriverCachePath() + "/" + driverName;
        if (new File(cachePath).exists()) {
            return cachePath;
        }

        return null;
    }

    /**
     * Force offline mode for WebDriverManager
     */
    public static void enableOfflineMode() {
        System.setProperty("wdm.avoidDownload", "true");
        System.setProperty("wdm.avoidFallback", "false");
        System.setProperty("wdm.forceCache", "true");
        logger.info("Offline mode enabled for WebDriverManager");
    }

    /**
     * Disable offline mode for WebDriverManager
     */
    public static void disableOfflineMode() {
        System.clearProperty("wdm.avoidDownload");
        System.clearProperty("wdm.avoidFallback");
        System.clearProperty("wdm.forceCache");
        logger.info("Offline mode disabled for WebDriverManager");
    }

    /**
     * Main method for command-line driver setup
     */
    public static void main(String[] args) {
        System.out.println("=== Driver Setup Utility ===");
        
        if (args.length > 0 && "setup".equals(args[0])) {
            setupAllDriversOffline();
            System.out.println("Driver setup completed. Check logs for details.");
        } else {
            System.out.println("Usage: java DriverSetupUtils setup");
            System.out.println("This will download and cache all browser drivers for offline use.");
        }
    }
}