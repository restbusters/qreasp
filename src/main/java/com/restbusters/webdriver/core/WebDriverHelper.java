package com.restbusters.webdriver.core;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for WebDriver operations and utilities.
 * Provides convenient methods for creating and managing WebDriver instances.
 * @author amatsaylo on 9/26/25
 * @project qreasp
 */
public class WebDriverHelper {

    private static final Logger log = LoggerFactory.getLogger(WebDriverHelper.class);

    /**
     * Creates a WebDriver based on driver type and headless mode.
     *
     * @param driverType the type of driver ("chrome", "firefox")
     * @param headless whether to run in headless mode
     * @return configured WebDriver instance
     */
    public static WebDriver createWebDriver(String driverType, boolean headless) {
        log.info("Creating WebDriver: type={}, headless={}", driverType, headless);

        if (driverType == null || driverType.trim().isEmpty()) {
            log.warn("Driver type not specified, defaulting to Chrome");
            driverType = "chrome";
        }

        String normalizedType = driverType.toLowerCase().trim();

        switch (normalizedType) {
            case "chrome":
                return headless ?
                        DriverManager.createDriver(DriverManager.BrowserType.CHROME_HEADLESS) :
                        DriverManager.createDriver(DriverManager.BrowserType.CHROME);

            case "firefox":
                return headless ?
                        DriverManager.createDriver(DriverManager.BrowserType.FIREFOX_HEADLESS) :
                        DriverManager.createDriver(DriverManager.BrowserType.FIREFOX);

            default:
                log.warn("Unsupported driver type: {}, defaulting to Chrome", driverType);
                return headless ?
                        DriverManager.createDriver(DriverManager.BrowserType.CHROME_HEADLESS) :
                        DriverManager.createDriver(DriverManager.BrowserType.CHROME);
        }
    }

    /**
     * Creates a WebDriver based on driver type (non-headless).
     *
     * @param driverType the type of driver ("chrome", "firefox")
     * @return configured WebDriver instance
     */
    public static WebDriver createWebDriver(String driverType) {
        return createWebDriver(driverType, false);
    }

    /**
     * Creates a default Chrome WebDriver.
     *
     * @return Chrome WebDriver instance
     */
    public static WebDriver createDefaultDriver() {
        return DriverManager.createChromeDriver();
    }

    /**
     * Creates a headless Chrome WebDriver.
     *
     * @return headless Chrome WebDriver instance
     */
    public static WebDriver createHeadlessDriver() {
        return DriverManager.createHeadlessChromeDriver();
    }

    /**
     * Creates a WebDriver from system property or environment variable.
     * Looks for: browser.type (system property) or BROWSER_TYPE (env var)
     * Looks for: browser.headless (system property) or BROWSER_HEADLESS (env var)
     *
     * @return configured WebDriver instance
     */
    public static WebDriver createDriverFromConfig() {
        String driverType = System.getProperty("browser.type",
                System.getenv("BROWSER_TYPE"));

        String headlessStr = System.getProperty("browser.headless",
                System.getenv("BROWSER_HEADLESS"));

        boolean headless = "true".equalsIgnoreCase(headlessStr);

        if (driverType == null) {
            log.info("No browser configuration found, using default Chrome");
            return headless ? createHeadlessDriver() : createDefaultDriver();
        }

        return createWebDriver(driverType, headless);
    }

    /**
     * Safely quits a WebDriver instance.
     *
     * @param driver the driver to quit
     */
    public static void quitDriver(WebDriver driver) {
        DriverManager.quitDriver(driver);
    }

    /**
     * Checks if a WebDriver instance is still active.
     *
     * @param driver the driver to check
     * @return true if driver is active, false otherwise
     */
    public static boolean isDriverActive(WebDriver driver) {
        if (driver == null) {
            return false;
        }

        try {
            driver.getCurrentUrl();
            return true;
        } catch (Exception e) {
            log.debug("Driver is not active", e);
            return false;
        }
    }

    /**
     * Gets the current page title safely.
     *
     * @param driver the WebDriver instance
     * @return the page title, or null if error occurs
     */
    public static String getPageTitle(WebDriver driver) {
        try {
            return driver != null ? driver.getTitle() : null;
        } catch (Exception e) {
            log.error("Error getting page title", e);
            return null;
        }
    }

    /**
     * Gets the current URL safely.
     *
     * @param driver the WebDriver instance
     * @return the current URL, or null if error occurs
     */
    public static String getCurrentUrl(WebDriver driver) {
        try {
            return driver != null ? driver.getCurrentUrl() : null;
        } catch (Exception e) {
            log.error("Error getting current URL", e);
            return null;
        }
    }

    /**
     * Navigates to a URL safely with error handling.
     *
     * @param driver the WebDriver instance
     * @param url the URL to navigate to
     * @return true if navigation successful, false otherwise
     */
    public static boolean navigateTo(WebDriver driver, String url) {
        try {
            if (driver == null) {
                log.error("Driver is null, cannot navigate");
                return false;
            }

            if (url == null || url.trim().isEmpty()) {
                log.error("URL is null or empty");
                return false;
            }

            log.info("Navigating to: {}", url);
            driver.get(url);
            return true;
        } catch (Exception e) {
            log.error("Error navigating to URL: {}", url, e);
            return false;
        }
    }

    /**
     * Refreshes the current page safely.
     *
     * @param driver the WebDriver instance
     * @return true if refresh successful, false otherwise
     */
    public static boolean refresh(WebDriver driver) {
        try {
            if (driver == null) {
                log.error("Driver is null, cannot refresh");
                return false;
            }

            log.info("Refreshing page");
            driver.navigate().refresh();
            return true;
        } catch (Exception e) {
            log.error("Error refreshing page", e);
            return false;
        }
    }

    /**
     * Maximizes the browser window safely.
     *
     * @param driver the WebDriver instance
     * @return true if maximize successful, false otherwise
     */
    public static boolean maximizeWindow(WebDriver driver) {
        try {
            if (driver == null) {
                log.error("Driver is null, cannot maximize");
                return false;
            }

            log.info("Maximizing window");
            driver.manage().window().maximize();
            return true;
        } catch (Exception e) {
            log.error("Error maximizing window", e);
            return false;
        }
    }
}