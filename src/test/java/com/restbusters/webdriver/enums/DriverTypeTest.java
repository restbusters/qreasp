package com.restbusters.webdriver.enums;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests for DriverType enum.
 */
public class DriverTypeTest {

    @Test(description = "Test all DriverType values exist")
    public void testAllDriverTypesExist() {
        assertEquals(DriverType.values().length, 5, "Should have 5 driver types");

        assertNotNull(DriverType.CHROME);
        assertNotNull(DriverType.FIREFOX);
        assertNotNull(DriverType.EDGE);
        assertNotNull(DriverType.SAFARI);
        assertNotNull(DriverType.OPERA);
    }

    @Test(description = "Test display names")
    public void testDisplayNames() {
        assertEquals(DriverType.CHROME.getDisplayName(), "Chrome Browser");
        assertEquals(DriverType.FIREFOX.getDisplayName(), "Firefox Browser");
        assertEquals(DriverType.EDGE.getDisplayName(), "Microsoft Edge");
        assertEquals(DriverType.SAFARI.getDisplayName(), "Safari Browser");
        assertEquals(DriverType.OPERA.getDisplayName(), "Opera Browser");
    }

    @Test(description = "Test valueOf")
    public void testValueOf() {
        assertEquals(DriverType.valueOf("CHROME"), DriverType.CHROME);
        assertEquals(DriverType.valueOf("FIREFOX"), DriverType.FIREFOX);
        assertEquals(DriverType.valueOf("EDGE"), DriverType.EDGE);
        assertEquals(DriverType.valueOf("SAFARI"), DriverType.SAFARI);
        assertEquals(DriverType.valueOf("OPERA"), DriverType.OPERA);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          description = "Test valueOf with invalid name throws exception")
    public void testValueOfInvalidThrowsException() {
        DriverType.valueOf("INVALID_BROWSER");
    }

    @Test(description = "Test ordinal values")
    public void testOrdinalValues() {
        assertEquals(DriverType.CHROME.ordinal(), 0);
        assertEquals(DriverType.FIREFOX.ordinal(), 1);
        assertEquals(DriverType.EDGE.ordinal(), 2);
        assertEquals(DriverType.SAFARI.ordinal(), 3);
        assertEquals(DriverType.OPERA.ordinal(), 4);
    }

    @Test(description = "Test all display names are not null or empty")
    public void testAllDisplayNamesNotNullOrEmpty() {
        for (DriverType driver : DriverType.values()) {
            assertNotNull(driver.getDisplayName(),
                "Display name should not be null for: " + driver.name());
            assertFalse(driver.getDisplayName().isEmpty(),
                "Display name should not be empty for: " + driver.name());
        }
    }

    @Test(description = "Test name method returns uppercase")
    public void testNameMethodReturnsUppercase() {
        for (DriverType driver : DriverType.values()) {
            assertEquals(driver.name(), driver.name().toUpperCase(),
                "Enum name should be uppercase: " + driver.name());
        }
    }
}
