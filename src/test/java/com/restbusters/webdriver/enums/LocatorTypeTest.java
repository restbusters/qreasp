package com.restbusters.webdriver.enums;

import com.restbusters.webdriver.utils.LocatorUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Tests for LocatorType enum.
 *
 * Critical bug coverage: Verifies that LocatorType.name().toLowerCase()
 * produces values that are compatible with LocatorUtils.getByLocator().
 *
 * The bug was: LocatorType.CSS_SELECTOR.name().toLowerCase() = "css_selector"
 * but LocatorUtils only accepted "css", "cssselector", "css selector" (no underscore).
 */
public class LocatorTypeTest {

    @Test(description = "Test all LocatorType values exist")
    public void testAllLocatorTypesExist() {
        assertEquals(LocatorType.values().length, 8, "Should have 8 locator types");

        assertNotNull(LocatorType.ID);
        assertNotNull(LocatorType.NAME);
        assertNotNull(LocatorType.CLASS_NAME);
        assertNotNull(LocatorType.TAG_NAME);
        assertNotNull(LocatorType.LINK_TEXT);
        assertNotNull(LocatorType.PARTIAL_LINK_TEXT);
        assertNotNull(LocatorType.CSS_SELECTOR);
        assertNotNull(LocatorType.XPATH);
    }

    @Test(description = "Test LocatorType selenium method values")
    public void testSeleniumMethodValues() {
        assertEquals(LocatorType.ID.getSeleniumMethod(), "id");
        assertEquals(LocatorType.NAME.getSeleniumMethod(), "name");
        assertEquals(LocatorType.CLASS_NAME.getSeleniumMethod(), "className");
        assertEquals(LocatorType.TAG_NAME.getSeleniumMethod(), "tagName");
        assertEquals(LocatorType.LINK_TEXT.getSeleniumMethod(), "linkText");
        assertEquals(LocatorType.PARTIAL_LINK_TEXT.getSeleniumMethod(), "partialLinkText");
        assertEquals(LocatorType.CSS_SELECTOR.getSeleniumMethod(), "cssSelector");
        assertEquals(LocatorType.XPATH.getSeleniumMethod(), "xpath");
    }

    @DataProvider(name = "locatorTypes")
    public Object[][] locatorTypes() {
        return new Object[][] {
            { LocatorType.ID, "my-id" },
            { LocatorType.NAME, "my-name" },
            { LocatorType.CLASS_NAME, "my-class" },
            { LocatorType.TAG_NAME, "div" },
            { LocatorType.LINK_TEXT, "Click Here" },
            { LocatorType.PARTIAL_LINK_TEXT, "Click" },
            { LocatorType.CSS_SELECTOR, ".my-class" },
            { LocatorType.XPATH, "//div[@id='test']" },
        };
    }

    /**
     * CRITICAL BUG FIX TEST
     *
     * This test verifies that using LocatorType.name().toLowerCase()
     * works with LocatorUtils.getByLocator().
     *
     * Before the fix, this would fail for:
     * - CSS_SELECTOR -> "css_selector" (not supported)
     * - CLASS_NAME -> "class_name" (not supported)
     * - TAG_NAME -> "tag_name" (not supported)
     * - LINK_TEXT -> "link_text" (not supported)
     * - PARTIAL_LINK_TEXT -> "partial_link_text" (not supported)
     */
    @Test(dataProvider = "locatorTypes",
          description = "Test LocatorType.name().toLowerCase() is compatible with LocatorUtils")
    public void testLocatorTypeNameCompatibleWithLocatorUtils(LocatorType locatorType, String testValue) {
        // This is exactly what BrowserContext and FormData do
        String strategy = locatorType.name().toLowerCase();

        Map<String, String> locator = LocatorUtils.createLocatorMap(strategy, testValue);

        // This should NOT throw an exception after the bug fix
        assertNotNull(LocatorUtils.getByLocator(locator),
            "LocatorUtils should support strategy: " + strategy);
    }

    @Test(description = "Test LocatorType valueOf")
    public void testValueOf() {
        assertEquals(LocatorType.valueOf("ID"), LocatorType.ID);
        assertEquals(LocatorType.valueOf("CSS_SELECTOR"), LocatorType.CSS_SELECTOR);
        assertEquals(LocatorType.valueOf("CLASS_NAME"), LocatorType.CLASS_NAME);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          description = "Test valueOf with invalid name throws exception")
    public void testValueOfInvalidThrowsException() {
        LocatorType.valueOf("INVALID");
    }

    /**
     * Test that demonstrates the underscore naming pattern.
     * LocatorType enum names use SCREAMING_SNAKE_CASE which becomes
     * lowercase_snake_case when .name().toLowerCase() is called.
     */
    @Test(description = "Test underscore pattern in enum names")
    public void testUnderscorePatternInEnumNames() {
        // These produce underscore variants
        assertEquals(LocatorType.CSS_SELECTOR.name().toLowerCase(), "css_selector");
        assertEquals(LocatorType.CLASS_NAME.name().toLowerCase(), "class_name");
        assertEquals(LocatorType.TAG_NAME.name().toLowerCase(), "tag_name");
        assertEquals(LocatorType.LINK_TEXT.name().toLowerCase(), "link_text");
        assertEquals(LocatorType.PARTIAL_LINK_TEXT.name().toLowerCase(), "partial_link_text");

        // These don't have underscores
        assertEquals(LocatorType.ID.name().toLowerCase(), "id");
        assertEquals(LocatorType.NAME.name().toLowerCase(), "name");
        assertEquals(LocatorType.XPATH.name().toLowerCase(), "xpath");
    }
}
