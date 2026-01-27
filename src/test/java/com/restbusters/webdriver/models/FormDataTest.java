package com.restbusters.webdriver.models;

import com.restbusters.webdriver.enums.LocatorType;
import com.restbusters.webdriver.utils.LocatorUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Tests for FormData model class.
 *
 * CRITICAL BUG FIX COVERAGE:
 * FormData uses LocatorType.name().toLowerCase() internally (lines 38, 47)
 * which produces underscore variants like "css_selector".
 * These tests verify the fix works end-to-end.
 */
public class FormDataTest {

    @Test(description = "Test basic constructor with LocatorType")
    public void testBasicConstructor() {
        FormData formData = new FormData(LocatorType.ID, "username", "testuser");

        assertNotNull(formData.getLocator());
        assertEquals(formData.getValue(), "testuser");
        assertTrue(formData.isClearBeforeType(), "clearBeforeType should default to true");
    }

    @Test(description = "Test constructor with field name")
    public void testConstructorWithFieldName() {
        FormData formData = new FormData(LocatorType.NAME, "email", "test@example.com", "Email Address");

        assertNotNull(formData.getLocator());
        assertEquals(formData.getValue(), "test@example.com");
        assertEquals(formData.getFieldName(), "Email Address");
        assertTrue(formData.isClearBeforeType());
    }

    /**
     * CRITICAL BUG FIX TEST
     *
     * This tests that FormData correctly creates locators using LocatorType.name().toLowerCase()
     * and that the resulting locator map is compatible with LocatorUtils.getByLocator().
     */
    @DataProvider(name = "allLocatorTypes")
    public Object[][] allLocatorTypes() {
        return new Object[][] {
            { LocatorType.ID, "my-id" },
            { LocatorType.NAME, "my-name" },
            { LocatorType.CLASS_NAME, "my-class" },          // Bug fix: class_name
            { LocatorType.TAG_NAME, "input" },               // Bug fix: tag_name
            { LocatorType.LINK_TEXT, "Click Here" },         // Bug fix: link_text
            { LocatorType.PARTIAL_LINK_TEXT, "Click" },      // Bug fix: partial_link_text
            { LocatorType.CSS_SELECTOR, "#username" },       // Bug fix: css_selector
            { LocatorType.XPATH, "//input[@name='user']" },
        };
    }

    @Test(dataProvider = "allLocatorTypes",
          description = "Test FormData locator is compatible with LocatorUtils")
    public void testFormDataLocatorCompatibleWithLocatorUtils(LocatorType locatorType, String locatorValue) {
        FormData formData = new FormData(locatorType, locatorValue, "test-value");

        // Get the locator map created by FormData
        Map<String, String> locator = formData.getLocator();

        // This should NOT throw an exception after the bug fix
        assertNotNull(LocatorUtils.getByLocator(locator),
            "LocatorUtils should support FormData locator with type: " + locatorType);
    }

    @Test(description = "Test CSS_SELECTOR locator creation - bug fix verification")
    public void testCssSelectorLocatorCreation() {
        FormData formData = new FormData(LocatorType.CSS_SELECTOR, ".login-button", "");

        Map<String, String> locator = formData.getLocator();

        // Verify the strategy is "css_selector" (lowercase with underscore)
        assertEquals(locator.get("strategy"), "css_selector");
        assertEquals(locator.get("value"), ".login-button");

        // Verify it works with LocatorUtils
        assertNotNull(LocatorUtils.getByLocator(locator));
    }

    @Test(description = "Test CLASS_NAME locator creation - bug fix verification")
    public void testClassNameLocatorCreation() {
        FormData formData = new FormData(LocatorType.CLASS_NAME, "form-input", "");

        Map<String, String> locator = formData.getLocator();

        assertEquals(locator.get("strategy"), "class_name");
        assertNotNull(LocatorUtils.getByLocator(locator));
    }

    @Test(description = "Test getLocatorType returns correct type")
    public void testGetLocatorType() {
        FormData formData = new FormData(LocatorType.XPATH, "//input", "value");

        // Note: getLocatorType() uses valueOf(strategy.toUpperCase())
        // "xpath" -> "XPATH" -> LocatorType.XPATH
        assertEquals(formData.getLocatorType(), LocatorType.XPATH);
    }

    @Test(description = "Test getLocatorType with underscore strategy")
    public void testGetLocatorTypeWithUnderscoreStrategy() {
        FormData formData = new FormData(LocatorType.CSS_SELECTOR, ".class", "value");

        // "css_selector" -> "CSS_SELECTOR" -> LocatorType.CSS_SELECTOR
        assertEquals(formData.getLocatorType(), LocatorType.CSS_SELECTOR);
    }

    @Test(description = "Test getLocatorValue")
    public void testGetLocatorValue() {
        FormData formData = new FormData(LocatorType.ID, "submit-btn", "");

        assertEquals(formData.getLocatorValue(), "submit-btn");
    }

    @Test(description = "Test builder pattern")
    public void testBuilderPattern() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("id", "password");

        FormData formData = FormData.builder()
                .locator(locator)
                .value("secret123")
                .fieldName("Password")
                .clearBeforeType(false)
                .validateAfterInput(true)
                .expectedValue("secret123")
                .build();

        assertEquals(formData.getValue(), "secret123");
        assertEquals(formData.getFieldName(), "Password");
        assertFalse(formData.isClearBeforeType());
        assertTrue(formData.isValidateAfterInput());
        assertEquals(formData.getExpectedValue(), "secret123");
    }

    @Test(description = "Test default clearBeforeType is true")
    public void testDefaultClearBeforeType() {
        FormData formData = FormData.builder()
                .locator(LocatorUtils.createLocatorMap("id", "field"))
                .value("test")
                .build();

        assertTrue(formData.isClearBeforeType());
    }

    @Test(description = "Test no-args constructor")
    public void testNoArgsConstructor() {
        FormData formData = new FormData();

        assertNull(formData.getLocator());
        assertNull(formData.getValue());
        assertNull(formData.getFieldName());
    }

    @Test(description = "Test all-args constructor")
    public void testAllArgsConstructor() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("name", "email");

        FormData formData = new FormData(
                locator,
                "user@test.com",
                "Email Field",
                true,
                true,
                "user@test.com"
        );

        assertEquals(formData.getLocator(), locator);
        assertEquals(formData.getValue(), "user@test.com");
        assertEquals(formData.getFieldName(), "Email Field");
        assertTrue(formData.isClearBeforeType());
        assertTrue(formData.isValidateAfterInput());
        assertEquals(formData.getExpectedValue(), "user@test.com");
    }

    @Test(description = "Test getLocatorType with null locator")
    public void testGetLocatorTypeWithNullLocator() {
        FormData formData = new FormData();
        formData.setLocator(null);

        assertNull(formData.getLocatorType());
    }

    @Test(description = "Test getLocatorValue with null locator")
    public void testGetLocatorValueWithNullLocator() {
        FormData formData = new FormData();
        formData.setLocator(null);

        assertNull(formData.getLocatorValue());
    }

    @Test(description = "Test form data for typical login form")
    public void testLoginFormScenario() {
        FormData username = new FormData(LocatorType.ID, "username", "admin", "Username");
        FormData password = new FormData(LocatorType.ID, "password", "secret", "Password");
        FormData submit = new FormData(LocatorType.CSS_SELECTOR, "button[type='submit']", "", "Submit Button");

        // All should create valid locators
        assertNotNull(LocatorUtils.getByLocator(username.getLocator()));
        assertNotNull(LocatorUtils.getByLocator(password.getLocator()));
        assertNotNull(LocatorUtils.getByLocator(submit.getLocator()));
    }
}
