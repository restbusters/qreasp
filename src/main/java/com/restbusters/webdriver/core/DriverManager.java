package com.restbusters.webdriver.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Manages WebDriver instances with automatic driver binary management.
 * @author amatsaylo on 9/26/25
 * @project qreasp
 */
public class DriverManager {

    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    public enum BrowserType {
        CHROME,
        FIREFOX,
        CHROME_HEADLESS,
        FIREFOX_HEADLESS
    }

    /**
     * Creates and configures a WebDriver instance.
     *
     * @param browserType the type of browser to launch
     * @return configured WebDriver instance
     */
    public static WebDriver createDriver(BrowserType browserType) {
        log.info("Creating {} driver", browserType);

        WebDriver driver;

        switch (browserType) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(getChromeOptions(false));
                break;

            case CHROME_HEADLESS:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(getChromeOptions(true));
                break;

            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(getFirefoxOptions(false));
                break;

            case FIREFOX_HEADLESS:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(getFirefoxOptions(true));
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }

        // Configure timeouts
        driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        // Maximize window
        driver.manage().window().maximize();

        log.info("Driver created successfully: {}", driver.getClass().getSimpleName());
        return driver;
    }

    /**
     * Creates a Chrome driver with default settings.
     *
     * @return Chrome WebDriver instance
     */
    public static WebDriver createChromeDriver() {
        return createDriver(BrowserType.CHROME);
    }

    /**
     * Creates a headless Chrome driver (no UI).
     *
     * @return headless Chrome WebDriver instance
     */
    public static WebDriver createHeadlessChromeDriver() {
        return createDriver(BrowserType.CHROME_HEADLESS);
    }

    /**
     * Configures Chrome options.
     */
    private static ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        // Common options for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");

        // Set page load strategy
        options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.NORMAL);

        return options;
    }

    /**
     * Configures Firefox options.
     */
    private static FirefoxOptions getFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        return options;
    }

    /**
     * Safely quits a WebDriver instance.
     *
     * @param driver the driver to quit
     */
    public static void quitDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
                log.info("Driver quit successfully");
            } catch (Exception e) {
                log.error("Error quitting driver", e);
            }
        }
    }
}