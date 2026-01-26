package com.restbusters.webdriver.utils;

import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Unit tests for LocatorUtils.
 * Tests all locator strategy variants including underscore formats (e.g., css_selector).
 *
 * Bug fix coverage:
 * - css_selector variant (previously only css, cssselector, css selector worked)
 * - class_name variant (previously only className, classname, class worked)
 * - tag_name variant (previously only tagName, tagname, tag worked)
 * - link_text variant (previously only linkText, linktext, link worked)
 * - partial_link_text variant (previously only partialLinkText, partiallinktext, partiallink worked)
 */
public class LocatorUtilsTest {

    private static final String TEST_VALUE = "test-value";

    // ==================== CSS Selector Tests ====================

    @DataProvider(name = "cssSelectorStrategies")
    public Object[][] cssSelectorStrategies() {
        return new Object[][] {
            { "css" },
            { "cssselector" },
            { "css selector" },
            { "css_selector" },  // Bug fix: underscore variant
            { "CSS" },           // Case insensitive
            { "CSS_SELECTOR" },  // Case insensitive with underscore
        };
    }

    @Test(dataProvider = "cssSelectorStrategies", description = "Test CSS selector strategy variants")
    public void testCssSelectorStrategies(String strategy) {
        Map<String, String> locator = LocatorUtils.createLocatorMap(strategy, ".my-class");

        By result = LocatorUtils.getByLocator(locator);

        assertNotNull(result, "By locator should not be null for strategy: " + strategy);
        assertTrue(result instanceof By.ByCssSelector,
            "Should return ByCssSelector for strategy: " + strategy);
        assertEquals(result.toString(), "By.cssSelector: .my-class");
    }

    // ==================== Class Name Tests ====================

    @DataProvider(name = "classNameStrategies")
    public Object[][] classNameStrategies() {
        return new Object[][] {
            { "className" },
            { "classname" },
            { "class" },
            { "class_name" },    // Bug fix: underscore variant
            { "CLASSNAME" },     // Case insensitive
            { "CLASS_NAME" },    // Case insensitive with underscore
        };
    }

    @Test(dataProvider = "classNameStrategies", description = "Test class name strategy variants")
    public void testClassNameStrategies(String strategy) {
        Map<String, String> locator = LocatorUtils.createLocatorMap(strategy, "my-class");

        By result = LocatorUtils.getByLocator(locator);

        assertNotNull(result, "By locator should not be null for strategy: " + strategy);
        assertTrue(result instanceof By.ByClassName,
            "Should return ByClassName for strategy: " + strategy);
        assertEquals(result.toString(), "By.className: my-class");
    }

    // ==================== Tag Name Tests ====================

    @DataProvider(name = "tagNameStrategies")
    public Object[][] tagNameStrategies() {
        return new Object[][] {
            { "tagName" },
            { "tagname" },
            { "tag" },
            { "tag_name" },      // Bug fix: underscore variant
            { "TAGNAME" },       // Case insensitive
            { "TAG_NAME" },      // Case insensitive with underscore
        };
    }

    @Test(dataProvider = "tagNameStrategies", description = "Test tag name strategy variants")
    public void testTagNameStrategies(String strategy) {
        Map<String, String> locator = LocatorUtils.createLocatorMap(strategy, "div");

        By result = LocatorUtils.getByLocator(locator);

        assertNotNull(result, "By locator should not be null for strategy: " + strategy);
        assertTrue(result instanceof By.ByTagName,
            "Should return ByTagName for strategy: " + strategy);
        assertEquals(result.toString(), "By.tagName: div");
    }

    // ==================== Link Text Tests ====================

    @DataProvider(name = "linkTextStrategies")
    public Object[][] linkTextStrategies() {
        return new Object[][] {
            { "linkText" },
            { "linktext" },
            { "link" },
            { "link_text" },     // Bug fix: underscore variant
            { "LINKTEXT" },      // Case insensitive
            { "LINK_TEXT" },     // Case insensitive with underscore
        };
    }

    @Test(dataProvider = "linkTextStrategies", description = "Test link text strategy variants")
    public void testLinkTextStrategies(String strategy) {
        Map<String, String> locator = LocatorUtils.createLocatorMap(strategy, "Click Here");

        By result = LocatorUtils.getByLocator(locator);

        assertNotNull(result, "By locator should not be null for strategy: " + strategy);
        assertTrue(result instanceof By.ByLinkText,
            "Should return ByLinkText for strategy: " + strategy);
        assertEquals(result.toString(), "By.linkText: Click Here");
    }

    // ==================== Partial Link Text Tests ====================

    @DataProvider(name = "partialLinkTextStrategies")
    public Object[][] partialLinkTextStrategies() {
        return new Object[][] {
            { "partialLinkText" },
            { "partiallinktext" },
            { "partiallink" },
            { "partial_link_text" },  // Bug fix: underscore variant
            { "PARTIALLINKTEXT" },    // Case insensitive
            { "PARTIAL_LINK_TEXT" },  // Case insensitive with underscore
        };
    }

    @Test(dataProvider = "partialLinkTextStrategies", description = "Test partial link text strategy variants")
    public void testPartialLinkTextStrategies(String strategy) {
        Map<String, String> locator = LocatorUtils.createLocatorMap(strategy, "Click");

        By result = LocatorUtils.getByLocator(locator);

        assertNotNull(result, "By locator should not be null for strategy: " + strategy);
        assertTrue(result instanceof By.ByPartialLinkText,
            "Should return ByPartialLinkText for strategy: " + strategy);
        assertEquals(result.toString(), "By.partialLinkText: Click");
    }

    // ==================== ID Tests ====================

    @Test(description = "Test ID strategy")
    public void testIdStrategy() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("id", "my-id");

        By result = LocatorUtils.getByLocator(locator);

        assertNotNull(result);
        assertTrue(result instanceof By.ById);
        assertEquals(result.toString(), "By.id: my-id");
    }

    @Test(description = "Test ID strategy case insensitive")
    public void testIdStrategyCaseInsensitive() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("ID", "my-id");

        By result = LocatorUtils.getByLocator(locator);

        assertNotNull(result);
        assertTrue(result instanceof By.ById);
    }

    // ==================== Name Tests ====================

    @Test(description = "Test name strategy")
    public void testNameStrategy() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("name", "my-name");

        By result = LocatorUtils.getByLocator(locator);

        assertNotNull(result);
        assertTrue(result instanceof By.ByName);
        assertEquals(result.toString(), "By.name: my-name");
    }

    // ==================== XPath Tests ====================

    @Test(description = "Test xpath strategy")
    public void testXpathStrategy() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("xpath", "//div[@id='test']");

        By result = LocatorUtils.getByLocator(locator);

        assertNotNull(result);
        assertTrue(result instanceof By.ByXPath);
        assertEquals(result.toString(), "By.xpath: //div[@id='test']");
    }

    @Test(description = "Test xpath strategy case insensitive")
    public void testXpathStrategyCaseInsensitive() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("XPATH", "//div");

        By result = LocatorUtils.getByLocator(locator);

        assertNotNull(result);
        assertTrue(result instanceof By.ByXPath);
    }

    // ==================== Validation Tests ====================

    @Test(description = "Test valid locator map")
    public void testIsValidLocatorMap() {
        Map<String, String> validLocator = LocatorUtils.createLocatorMap("css", ".class");

        assertTrue(LocatorUtils.isValidLocatorMap(validLocator));
    }

    @Test(description = "Test null locator map is invalid")
    public void testNullLocatorMapIsInvalid() {
        assertFalse(LocatorUtils.isValidLocatorMap(null));
    }

    @Test(description = "Test empty locator map is invalid")
    public void testEmptyLocatorMapIsInvalid() {
        assertFalse(LocatorUtils.isValidLocatorMap(Map.of()));
    }

    @Test(description = "Test locator map with empty strategy is invalid")
    public void testEmptyStrategyIsInvalid() {
        Map<String, String> locator = Map.of("strategy", "", "value", "test");
        assertFalse(LocatorUtils.isValidLocatorMap(locator));
    }

    @Test(description = "Test locator map with empty value is invalid")
    public void testEmptyValueIsInvalid() {
        Map<String, String> locator = Map.of("strategy", "css", "value", "");
        assertFalse(LocatorUtils.isValidLocatorMap(locator));
    }

    // ==================== Error Handling Tests ====================

    @Test(expectedExceptions = IllegalArgumentException.class,
          description = "Test unsupported strategy throws exception")
    public void testUnsupportedStrategyThrowsException() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("unsupported", "value");
        LocatorUtils.getByLocator(locator);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          description = "Test invalid locator map throws exception")
    public void testInvalidLocatorMapThrowsException() {
        LocatorUtils.getByLocator(null);
    }

    // ==================== Utility Method Tests ====================

    @Test(description = "Test createLocatorMap")
    public void testCreateLocatorMap() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("css", ".class");

        assertEquals(locator.get("strategy"), "css");
        assertEquals(locator.get("value"), ".class");
    }

    @Test(description = "Test getStrategy")
    public void testGetStrategy() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("xpath", "//div");

        assertEquals(LocatorUtils.getStrategy(locator), "xpath");
    }

    @Test(description = "Test getValue")
    public void testGetValue() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("id", "my-id");

        assertEquals(LocatorUtils.getValue(locator), "my-id");
    }

    @Test(description = "Test getStrategy with null returns null")
    public void testGetStrategyWithNullReturnsNull() {
        assertNull(LocatorUtils.getStrategy(null));
    }

    @Test(description = "Test getValue with null returns null")
    public void testGetValueWithNullReturnsNull() {
        assertNull(LocatorUtils.getValue(null));
    }

    @Test(description = "Test byToString")
    public void testByToString() {
        By by = By.cssSelector(".test");
        assertEquals(LocatorUtils.byToString(by), "By.cssSelector: .test");
    }

    @Test(description = "Test byToString with null")
    public void testByToStringWithNull() {
        assertEquals(LocatorUtils.byToString(null), "null");
    }

    @Test(description = "Test locatorMapToString")
    public void testLocatorMapToString() {
        Map<String, String> locator = LocatorUtils.createLocatorMap("xpath", "//div[@id='test']");

        assertEquals(LocatorUtils.locatorMapToString(locator), "xpath: //div[@id='test']");
    }

    @Test(description = "Test locatorMapToString with invalid locator")
    public void testLocatorMapToStringWithInvalid() {
        assertEquals(LocatorUtils.locatorMapToString(null), "invalid locator");
    }

    // ==================== Integration with LocatorType enum ====================

    /**
     * This test verifies the fix for the bug where LocatorType.name().toLowerCase()
     * produces underscore variants (e.g., "css_selector") that weren't previously supported.
     */
    @Test(description = "Test LocatorType enum name compatibility")
    public void testLocatorTypeEnumNameCompatibility() {
        // Simulate what BrowserContext does: LocatorType.CSS_SELECTOR.name().toLowerCase()
        String cssSelector = "CSS_SELECTOR".toLowerCase();  // css_selector
        String className = "CLASS_NAME".toLowerCase();      // class_name
        String tagName = "TAG_NAME".toLowerCase();          // tag_name
        String linkText = "LINK_TEXT".toLowerCase();        // link_text
        String partialLinkText = "PARTIAL_LINK_TEXT".toLowerCase(); // partial_link_text

        // All these should work after the bug fix
        assertNotNull(LocatorUtils.getByLocator(Map.of("strategy", cssSelector, "value", ".test")));
        assertNotNull(LocatorUtils.getByLocator(Map.of("strategy", className, "value", "test")));
        assertNotNull(LocatorUtils.getByLocator(Map.of("strategy", tagName, "value", "div")));
        assertNotNull(LocatorUtils.getByLocator(Map.of("strategy", linkText, "value", "test")));
        assertNotNull(LocatorUtils.getByLocator(Map.of("strategy", partialLinkText, "value", "test")));
    }
}
