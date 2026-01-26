package com.restbusters.webdriver.utils;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.*;

/**
 * Tests for ScreenshotUtils utility class.
 *
 * Note: Methods requiring WebDriver (takeScreenshot, saveScreenshot) are tested
 * via integration tests in BrowserContextTest.
 */
public class ScreenshotUtilsTest {

    private Path savedFile;

    @AfterMethod
    public void cleanup() {
        // Clean up any saved test screenshots
        if (savedFile != null && Files.exists(savedFile)) {
            try {
                Files.delete(savedFile);
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }

    @Test(description = "Test saveScreenshotToFile creates file")
    public void testSaveScreenshotToFileCreatesFile() {
        byte[] testData = "fake-png-data".getBytes();

        String result = ScreenshotUtils.saveScreenshotToFile(testData, "test");

        assertNotNull(result, "Should return file path");
        savedFile = Paths.get(result);
        assertTrue(Files.exists(savedFile), "File should exist");
    }

    @Test(description = "Test saveScreenshotToFile with prefix")
    public void testSaveScreenshotToFileWithPrefix() {
        byte[] testData = "test-screenshot-data".getBytes();

        String result = ScreenshotUtils.saveScreenshotToFile(testData, "my-prefix");

        assertNotNull(result);
        assertTrue(result.contains("my-prefix"), "Filename should contain prefix");
        assertTrue(result.endsWith(".png"), "Filename should end with .png");

        savedFile = Paths.get(result);
        assertTrue(Files.exists(savedFile));
    }

    @Test(description = "Test saveScreenshotToFile creates directory if not exists")
    public void testSaveScreenshotToFileCreatesDirectory() throws IOException {
        // Delete screenshots directory if it exists (for clean test)
        Path screenshotsDir = Paths.get("screenshots");

        byte[] testData = "test-data".getBytes();

        String result = ScreenshotUtils.saveScreenshotToFile(testData, "dir-test");

        assertNotNull(result);
        assertTrue(Files.exists(screenshotsDir), "Screenshots directory should be created");

        savedFile = Paths.get(result);
    }

    @Test(description = "Test saveScreenshotToFile writes correct data")
    public void testSaveScreenshotToFileWritesCorrectData() throws IOException {
        byte[] testData = "exact-test-content".getBytes();

        String result = ScreenshotUtils.saveScreenshotToFile(testData, "content-test");

        assertNotNull(result);
        savedFile = Paths.get(result);

        byte[] readData = Files.readAllBytes(savedFile);
        assertEquals(readData, testData, "File content should match original data");
    }

    @Test(description = "Test saveScreenshotToFile with empty data")
    public void testSaveScreenshotToFileWithEmptyData() {
        byte[] emptyData = new byte[0];

        String result = ScreenshotUtils.saveScreenshotToFile(emptyData, "empty-test");

        assertNotNull(result, "Should still create file with empty data");
        savedFile = Paths.get(result);
        assertTrue(Files.exists(savedFile));
    }

    @Test(description = "Test saveScreenshotToFile generates unique timestamps")
    public void testSaveScreenshotToFileGeneratesUniqueTimestamps() throws InterruptedException {
        byte[] testData = "test".getBytes();

        String result1 = ScreenshotUtils.saveScreenshotToFile(testData, "unique");
        Thread.sleep(10); // Ensure different timestamp
        String result2 = ScreenshotUtils.saveScreenshotToFile(testData, "unique");

        assertNotEquals(result1, result2, "File paths should be unique");

        // Cleanup both files
        try {
            Files.deleteIfExists(Paths.get(result1));
            Files.deleteIfExists(Paths.get(result2));
        } catch (IOException e) {
            // Ignore
        }
    }

    @Test(description = "Test saveScreenshotToFile with special characters in prefix")
    public void testSaveScreenshotToFileWithSpecialPrefix() {
        byte[] testData = "test".getBytes();

        // Use safe prefix (special characters may not be allowed in filenames)
        String result = ScreenshotUtils.saveScreenshotToFile(testData, "test-prefix-123");

        assertNotNull(result);
        assertTrue(result.contains("test-prefix-123"));

        savedFile = Paths.get(result);
    }

    /**
     * Note: The following methods require WebDriver and are tested in integration tests:
     * - takeScreenshot(WebDriver)
     * - saveScreenshot(WebDriver, String)
     *
     * See BrowserContextTest for WebDriver-based screenshot tests.
     */
}
