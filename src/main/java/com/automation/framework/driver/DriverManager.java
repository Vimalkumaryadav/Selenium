package com.automation.framework.driver;

import com.automation.framework.config.ConfigManager;
import com.automation.framework.utils.DriverSetupUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * WebDriver Manager for handling browser initialization and cleanup
 */
public class DriverManager {
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConfigManager config = ConfigManager.getInstance();

    private DriverManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initialize WebDriver based on browser configuration
     */
    public static void initializeDriver() {
        String browserName = config.getBrowser().toLowerCase();
        logger.info("Initializing {} driver", browserName);

        try {
            // Configure WebDriverManager globally before creating driver
            DriverSetupUtils.configureWebDriverManagerGlobally();
            
            // Enable offline mode if configured
            if (config.isDriverOfflineMode()) {
                DriverSetupUtils.enableOfflineMode();
            }

            WebDriver driver = createDriver(browserName);
            driverThreadLocal.set(driver);
            configureDriver(driver);
            logger.info("Driver initialized successfully: {}", browserName);
        } catch (Exception e) {
            logger.error("Failed to initialize {} driver: {}", browserName, e.getMessage());
            throw new RuntimeException("Driver initialization failed: " + e.getMessage(), e);
        }
    }

    /**
     * Create WebDriver instance based on browser type
     */
    private static WebDriver createDriver(String browserName) {
        return switch (browserName) {
            case "chrome" -> createChromeDriver();
            case "firefox" -> createFirefoxDriver();
            case "edge" -> createEdgeDriver();
            default -> {
                logger.warn("Unsupported browser: {}. Defaulting to Chrome", browserName);
                yield createChromeDriver();
            }
        };
    }

    /**
     * Create Chrome WebDriver with offline fallback support
     */
    private static WebDriver createChromeDriver() {
        setupChromeDriver();
        ChromeOptions options = createChromeOptions();
        return new ChromeDriver(options);
    }

    /**
     * Setup ChromeDriver with offline fallback
     */
    private static void setupChromeDriver() {
        try {
            // Check for local driver first
            String driverPath = DriverSetupUtils.getDriverPath("chromedriver.exe");
            if (driverPath != null) {
                System.setProperty("webdriver.chrome.driver", driverPath);
                logger.info("Using local ChromeDriver: {}", driverPath);
                return;
            }

            // Try WebDriverManager with configured settings
            if (config.isDriverAutoDownload() && DriverSetupUtils.isInternetAvailable()) {
                WebDriverManager chromeManager = WebDriverManager.chromedriver();
                chromeManager.cachePath(config.getDriverCachePath());
                chromeManager.timeout(config.getDriverDownloadTimeout());
                chromeManager.setup();
                logger.info("Downloaded ChromeDriver using WebDriverManager");
                return;
            }
            
        } catch (Exception e) {
            logger.warn("Primary ChromeDriver setup failed: {}", e.getMessage());
        }

        // Final fallback to system PATH
        String systemChromeDriver = findDriverInPath("chromedriver.exe");
        if (systemChromeDriver != null) {
            System.setProperty("webdriver.chrome.driver", systemChromeDriver);
            logger.info("Using system ChromeDriver: {}", systemChromeDriver);
        } else {
            String errorMsg = "ChromeDriver not found. Please:\n" +
                    "1. Place chromedriver.exe in " + config.getDriverLocalPath() + "\n" +
                    "2. Or ensure Chrome is installed and chromedriver is in PATH\n" +
                    "3. Or enable auto-download with internet connection";
            logger.error(errorMsg);
            throw new RuntimeException("ChromeDriver not found");
        }
    }

    /**
     * Create Chrome options
     */
    private static ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        
        if (config.isBrowserHeadless()) {
            options.addArguments("--headless=new");
        }
        if (config.isBrowserIncognito()) {
            options.addArguments("--incognito");
        }
        
        // Essential Chrome options for automation
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-first-run");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-translate");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-ipc-flooding-protection");
        
        // Remove automation indicators
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");

        return options;
    }

    /**
     * Create Firefox WebDriver with offline fallback support
     */
    private static WebDriver createFirefoxDriver() {
        setupFirefoxDriver();
        FirefoxOptions options = createFirefoxOptions();
        return new FirefoxDriver(options);
    }

    /**
     * Setup FirefoxDriver with offline fallback
     */
    private static void setupFirefoxDriver() {
        try {
            // Check for local driver first
            String driverPath = DriverSetupUtils.getDriverPath("geckodriver.exe");
            if (driverPath != null) {
                System.setProperty("webdriver.gecko.driver", driverPath);
                logger.info("Using local FirefoxDriver: {}", driverPath);
                return;
            }

            // Try WebDriverManager with configured settings
            if (config.isDriverAutoDownload() && DriverSetupUtils.isInternetAvailable()) {
                WebDriverManager firefoxManager = WebDriverManager.firefoxdriver();
                firefoxManager.cachePath(config.getDriverCachePath());
                firefoxManager.timeout(config.getDriverDownloadTimeout());
                firefoxManager.setup();
                logger.info("Downloaded FirefoxDriver using WebDriverManager");
                return;
            }
            
        } catch (Exception e) {
            logger.warn("Primary FirefoxDriver setup failed: {}", e.getMessage());
        }

        // Final fallback to system PATH
        String systemFirefoxDriver = findDriverInPath("geckodriver.exe");
        if (systemFirefoxDriver != null) {
            System.setProperty("webdriver.gecko.driver", systemFirefoxDriver);
            logger.info("Using system FirefoxDriver: {}", systemFirefoxDriver);
        } else {
            String errorMsg = "FirefoxDriver not found. Please:\n" +
                    "1. Place geckodriver.exe in " + config.getDriverLocalPath() + "\n" +
                    "2. Or ensure Firefox is installed and geckodriver is in PATH\n" +
                    "3. Or enable auto-download with internet connection";
            logger.error(errorMsg);
            throw new RuntimeException("FirefoxDriver not found");
        }
    }

    /**
     * Create Firefox options
     */
    private static FirefoxOptions createFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        
        if (config.isBrowserHeadless()) {
            options.addArguments("--headless");
        }
        
        // Common Firefox options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        return options;
    }

    /**
     * Create Edge WebDriver with offline fallback support
     */
    private static WebDriver createEdgeDriver() {
        setupEdgeDriver();
        EdgeOptions options = createEdgeOptions();
        return new EdgeDriver(options);
    }

    /**
     * Setup EdgeDriver with offline fallback
     */
    private static void setupEdgeDriver() {
        try {
            // Check for local driver first
            String driverPath = DriverSetupUtils.getDriverPath("msedgedriver.exe");
            if (driverPath != null) {
                System.setProperty("webdriver.edge.driver", driverPath);
                logger.info("Using local EdgeDriver: {}", driverPath);
                return;
            }

            // Try WebDriverManager with configured settings
            if (config.isDriverAutoDownload() && DriverSetupUtils.isInternetAvailable()) {
                WebDriverManager edgeManager = WebDriverManager.edgedriver();
                edgeManager.cachePath(config.getDriverCachePath());
                edgeManager.timeout(config.getDriverDownloadTimeout());
                edgeManager.setup();
                logger.info("Downloaded EdgeDriver using WebDriverManager");
                return;
            }
            
        } catch (Exception e) {
            logger.warn("Primary EdgeDriver setup failed: {}", e.getMessage());
        }

        // Final fallback to system PATH
        String systemEdgeDriver = findDriverInPath("msedgedriver.exe");
        if (systemEdgeDriver != null) {
            System.setProperty("webdriver.edge.driver", systemEdgeDriver);
            logger.info("Using system EdgeDriver: {}", systemEdgeDriver);
        } else {
            String errorMsg = "EdgeDriver not found. Please:\n" +
                    "1. Place msedgedriver.exe in " + config.getDriverLocalPath() + "\n" +
                    "2. Or ensure Edge is installed and msedgedriver is in PATH\n" +
                    "3. Or enable auto-download with internet connection";
            logger.error(errorMsg);
            throw new RuntimeException("EdgeDriver not found");
        }
    }

    /**
     * Create Edge options
     */
    private static EdgeOptions createEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        
        if (config.isBrowserHeadless()) {
            options.addArguments("--headless=new");
        }
        if (config.isBrowserIncognito()) {
            options.addArguments("--inprivate");
        }
        
        // Essential Edge options for automation
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-first-run");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-translate");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        // Remove automation indicators
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        return options;
    }

    /**
     * Configure WebDriver with timeouts and window settings
     */
    private static void configureDriver(WebDriver driver) {
        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitTimeout()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));

        // Configure window
        if (config.isBrowserMaximize()) {
            driver.manage().window().maximize();
        } else {
            driver.manage().window().setSize(
                new org.openqa.selenium.Dimension(
                    config.getBrowserWindowWidth(),
                    config.getBrowserWindowHeight()
                )
            );
        }
    }

    /**
     * Get the current thread's WebDriver instance
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new RuntimeException("Driver not initialized for current thread");
        }
        return driver;
    }

    /**
     * Quit WebDriver and clean up resources
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("Driver quit successfully");
            } catch (Exception e) {
                logger.error("Error while quitting driver", e);
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Check if driver is initialized for current thread
     */
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }

    /**
     * Configure WebDriverManager with proxy and cache settings
     */
    private static void configureWebDriverManager() {
        // Set cache to local drivers directory
        String driversCache = System.getProperty("user.dir") + "/drivers/.cache";
        System.setProperty("wdm.cachePath", driversCache);
        
        // Enable offline mode if drivers exist
        System.setProperty("wdm.avoidBrowserDetection", "true");
        System.setProperty("wdm.avoidFallback", "false");
        
        // Set timeouts
        System.setProperty("wdm.timeout", "30");
        
        // Configure proxy if needed (uncomment and modify as needed)
        // String proxyHost = System.getProperty("https.proxyHost");
        // String proxyPort = System.getProperty("https.proxyPort");
        // if (proxyHost != null && proxyPort != null) {
        //     System.setProperty("wdm.proxy", proxyHost + ":" + proxyPort);
        // }
        
        logger.info("WebDriverManager configured with cache path: {}", driversCache);
    }

    /**
     * Find driver executable in system PATH
     */
    private static String findDriverInPath(String driverName) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null) {
            return null;
        }

        String[] paths = pathEnv.split(System.getProperty("path.separator"));
        for (String path : paths) {
            java.io.File driverFile = new java.io.File(path, driverName);
            if (driverFile.exists() && driverFile.canExecute()) {
                logger.info("Found driver in PATH: {}", driverFile.getAbsolutePath());
                return driverFile.getAbsolutePath();
            }
        }

        // Also check common installation locations
        String[] commonPaths = {
            "C:/Program Files/Google/Chrome/Application",
            "C:/Program Files (x86)/Google/Chrome/Application", 
            "C:/Program Files/Microsoft/Edge/Application",
            "C:/Program Files (x86)/Microsoft/Edge/Application",
            "C:/Program Files/Mozilla Firefox",
            "C:/Program Files (x86)/Mozilla Firefox"
        };

        for (String commonPath : commonPaths) {
            java.io.File driverFile = new java.io.File(commonPath, driverName);
            if (driverFile.exists() && driverFile.canExecute()) {
                logger.info("Found driver in common location: {}", driverFile.getAbsolutePath());
                return driverFile.getAbsolutePath();
            }
        }

        return null;
    }

    /**
     * Download drivers manually for offline use
     */
    public static void downloadDriversForOfflineUse() {
        logger.info("Attempting to download drivers for offline use...");
        String driversDir = System.getProperty("user.dir") + "/drivers";
        
        try {
            // Create drivers directory if it doesn't exist
            java.io.File dir = new java.io.File(driversDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            configureWebDriverManager();

            // Download Chrome driver
            try {
                WebDriverManager.chromedriver().cachePath(driversDir).setup();
                logger.info("ChromeDriver downloaded successfully");
            } catch (Exception e) {
                logger.warn("Failed to download ChromeDriver: {}", e.getMessage());
            }

            // Download Firefox driver  
            try {
                WebDriverManager.firefoxdriver().cachePath(driversDir).setup();
                logger.info("FirefoxDriver downloaded successfully");
            } catch (Exception e) {
                logger.warn("Failed to download FirefoxDriver: {}", e.getMessage());
            }

            // Download Edge driver
            try {
                WebDriverManager.edgedriver().cachePath(driversDir).setup();
                logger.info("EdgeDriver downloaded successfully");
            } catch (Exception e) {
                logger.warn("Failed to download EdgeDriver: {}", e.getMessage());
            }

        } catch (Exception e) {
            logger.error("Error setting up offline drivers: {}", e.getMessage());
        }
    }
}