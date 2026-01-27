package com.restbusters.webdriver.models;

import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

/**
 * Tests for ValidationResult model class.
 */
public class ValidationResultTest {

    @Test(description = "Test success factory method")
    public void testSuccessFactoryMethod() {
        ValidationResult result = ValidationResult.success("TEXT_EQUALS", "Text matched expected value");

        assertTrue(result.isPassed(), "Should be passed");
        assertEquals(result.getValidationType(), "TEXT_EQUALS");
        assertEquals(result.getMessage(), "Text matched expected value");
        assertNotNull(result.getTimestamp(), "Timestamp should be set");
        assertNull(result.getExpectedValue(), "Expected value should be null for success");
        assertNull(result.getActualValue(), "Actual value should be null for success");
    }

    @Test(description = "Test failure factory method")
    public void testFailureFactoryMethod() {
        ValidationResult result = ValidationResult.failure(
                "TEXT_CONTAINS",
                "Hello World",
                "Goodbye World",
                "Text does not contain expected substring"
        );

        assertFalse(result.isPassed(), "Should not be passed");
        assertEquals(result.getValidationType(), "TEXT_CONTAINS");
        assertEquals(result.getExpectedValue(), "Hello World");
        assertEquals(result.getActualValue(), "Goodbye World");
        assertEquals(result.getMessage(), "Text does not contain expected substring");
        assertNotNull(result.getTimestamp(), "Timestamp should be set");
    }

    @Test(description = "Test builder pattern")
    public void testBuilderPattern() {
        LocalDateTime now = LocalDateTime.now();

        ValidationResult result = ValidationResult.builder()
                .passed(true)
                .validationType("ATTRIBUTE_EQUALS")
                .expectedValue("active")
                .actualValue("active")
                .message("Attribute 'class' contains expected value")
                .timestamp(now)
                .elementLocator("css: .button")
                .build();

        assertTrue(result.isPassed());
        assertEquals(result.getValidationType(), "ATTRIBUTE_EQUALS");
        assertEquals(result.getExpectedValue(), "active");
        assertEquals(result.getActualValue(), "active");
        assertEquals(result.getMessage(), "Attribute 'class' contains expected value");
        assertEquals(result.getTimestamp(), now);
        assertEquals(result.getElementLocator(), "css: .button");
    }

    @Test(description = "Test element locator field")
    public void testElementLocatorField() {
        ValidationResult result = ValidationResult.builder()
                .passed(false)
                .validationType("ELEMENT_VISIBLE")
                .message("Element not visible")
                .elementLocator("xpath: //div[@id='modal']")
                .build();

        assertEquals(result.getElementLocator(), "xpath: //div[@id='modal']");
    }

    @Test(description = "Test different validation types")
    public void testDifferentValidationTypes() {
        String[] validationTypes = {
            "TEXT_EQUALS", "TEXT_CONTAINS", "TEXT_STARTS_WITH", "TEXT_ENDS_WITH",
            "ATTRIBUTE_EQUALS", "ATTRIBUTE_CONTAINS",
            "ELEMENT_VISIBLE", "ELEMENT_EXISTS", "ELEMENT_ENABLED", "ELEMENT_SELECTED",
            "URL_EQUALS", "URL_CONTAINS",
            "TITLE_EQUALS", "TITLE_CONTAINS",
            "VALUE_EQUALS", "VALUE_NOT_EMPTY"
        };

        for (String validationType : validationTypes) {
            ValidationResult success = ValidationResult.success(validationType, "Passed");
            assertEquals(success.getValidationType(), validationType);
            assertTrue(success.isPassed());

            ValidationResult failure = ValidationResult.failure(validationType, "expected", "actual", "Failed");
            assertEquals(failure.getValidationType(), validationType);
            assertFalse(failure.isPassed());
        }
    }

    @Test(description = "Test timestamp is recent")
    public void testTimestampIsRecent() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        ValidationResult result = ValidationResult.success("TEST", "message");

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertTrue(result.getTimestamp().isAfter(before), "Timestamp should be after test start");
        assertTrue(result.getTimestamp().isBefore(after), "Timestamp should be before test end");
    }

    @Test(description = "Test null values in failure")
    public void testNullValuesInFailure() {
        ValidationResult result = ValidationResult.failure("CHECK", null, null, "Generic failure");

        assertFalse(result.isPassed());
        assertNull(result.getExpectedValue());
        assertNull(result.getActualValue());
        assertEquals(result.getMessage(), "Generic failure");
    }

    @Test(description = "Test with special characters in values")
    public void testSpecialCharactersInValues() {
        ValidationResult result = ValidationResult.failure(
                "TEXT_EQUALS",
                "Hello <World> & \"Friends\"",
                "Hello <World> & 'Enemies'",
                "Values don't match"
        );

        assertEquals(result.getExpectedValue(), "Hello <World> & \"Friends\"");
        assertEquals(result.getActualValue(), "Hello <World> & 'Enemies'");
    }

    @Test(description = "Test with multiline values")
    public void testMultilineValues() {
        String expected = "Line 1\nLine 2\nLine 3";
        String actual = "Line 1\nLine 2\nDifferent Line 3";

        ValidationResult result = ValidationResult.failure("TEXT_EQUALS", expected, actual, "Multiline text mismatch");

        assertTrue(result.getExpectedValue().contains("\n"));
        assertTrue(result.getActualValue().contains("\n"));
    }

    @Test(description = "Test with empty strings")
    public void testWithEmptyStrings() {
        ValidationResult result = ValidationResult.failure("VALUE_NOT_EMPTY", "non-empty", "", "Value is empty");

        assertEquals(result.getExpectedValue(), "non-empty");
        assertEquals(result.getActualValue(), "");
    }

    @Test(description = "Test with unicode characters")
    public void testWithUnicodeCharacters() {
        ValidationResult result = ValidationResult.success("TEXT_EQUALS", "Matched: \u4e2d\u6587 \u65e5\u672c\u8a9e");

        assertTrue(result.getMessage().contains("\u4e2d\u6587"));
    }
}
