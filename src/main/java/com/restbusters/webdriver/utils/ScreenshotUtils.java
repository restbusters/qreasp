package com.restbusters.webdriver.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for taking and managing screenshots.
 * @author amatsaylo on 9/26/25
 * @project qreasp
 */
@UtilityClass
@Slf4j
public class ScreenshotUtils {

    private static final String SCREENSHOT_DIR = "screenshots";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    /**
     * Takes a screenshot of the current browser state.
     */
    public byte[] takeScreenshot(WebDriver driver) {
        try {
            if (driver instanceof TakesScreenshot) {
                return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            }
        } catch (Exception e) {
            log.warn("Failed to take screenshot", e);
        }
        return new byte[0];
    }

    /**
     * Takes a screenshot and saves it to file with timestamp.
     */
    public String saveScreenshot(WebDriver driver, String prefix) {
        try {
            byte[] screenshot = takeScreenshot(driver);
            if (screenshot.length > 0) {
                return saveScreenshotToFile(screenshot, prefix);
            }
        } catch (Exception e) {
            log.error("Failed to save screenshot", e);
        }
        return null;
    }

    /**
     * Saves screenshot bytes to file system.
     */
    public String saveScreenshotToFile(byte[] screenshot, String prefix) {
        try {
            createScreenshotDirectory();
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String filename = String.format("%s_%s.png", prefix, timestamp);
            Path filePath = Paths.get(SCREENSHOT_DIR, filename);
            Files.write(filePath, screenshot);
            log.debug("Screenshot saved: {}", filePath.toAbsolutePath());
            return filePath.toString();
        } catch (IOException e) {
            log.error("Failed to save screenshot to file", e);
            return null;
        }
    }

    private void createScreenshotDirectory() throws IOException {
        Path dir = Paths.get(SCREENSHOT_DIR);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }
}
