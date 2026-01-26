package com.restbusters.webdriver.enums;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * Tests for ActionType enum.
 */
public class ActionTypeTest {

    @Test(description = "Test all ActionType values exist")
    public void testAllActionTypesExist() {
        // Should have 43 action types based on the enum
        assertTrue(ActionType.values().length >= 40, "Should have at least 40 action types");
    }

    @Test(description = "Test basic element actions exist")
    public void testBasicElementActionsExist() {
        assertNotNull(ActionType.CLICK);
        assertNotNull(ActionType.SEND_KEYS);
        assertNotNull(ActionType.CLEAR);
        assertNotNull(ActionType.SUBMIT);

        assertEquals(ActionType.CLICK.getDescription(), "Click on element");
        assertEquals(ActionType.SEND_KEYS.getDescription(), "Send keys to element");
        assertEquals(ActionType.CLEAR.getDescription(), "Clear element content");
        assertEquals(ActionType.SUBMIT.getDescription(), "Submit form element");
    }

    @Test(description = "Test text and attribute operations exist")
    public void testTextAndAttributeOperationsExist() {
        assertNotNull(ActionType.GET_TEXT);
        assertNotNull(ActionType.GET_ATTRIBUTE);
        assertNotNull(ActionType.GET_VALUE);

        assertEquals(ActionType.GET_TEXT.getDescription(), "Get element text");
        assertEquals(ActionType.GET_ATTRIBUTE.getDescription(), "Get element attribute");
        assertEquals(ActionType.GET_VALUE.getDescription(), "Get element value");
    }

    @Test(description = "Test selection operations exist")
    public void testSelectionOperationsExist() {
        assertNotNull(ActionType.SELECT_FROM_DROPDOWN);
        assertNotNull(ActionType.SELECT_BY_INDEX);
        assertNotNull(ActionType.SELECT_BY_VALUE);
    }

    @Test(description = "Test wait operations exist")
    public void testWaitOperationsExist() {
        assertNotNull(ActionType.WAIT_FOR_ELEMENT);
        assertNotNull(ActionType.WAIT_FOR_ELEMENT_CLICKABLE);
        assertNotNull(ActionType.WAIT_FOR_ELEMENT_VISIBLE);
        assertNotNull(ActionType.WAIT_FOR_ELEMENT_INVISIBLE);
        assertNotNull(ActionType.WAIT_FOR_TEXT_PRESENT);
    }

    @Test(description = "Test navigation operations exist")
    public void testNavigationOperationsExist() {
        assertNotNull(ActionType.NAVIGATE_TO);
        assertNotNull(ActionType.NAVIGATE_BACK);
        assertNotNull(ActionType.NAVIGATE_FORWARD);
        assertNotNull(ActionType.REFRESH);
    }

    @Test(description = "Test window and frame operations exist")
    public void testWindowAndFrameOperationsExist() {
        assertNotNull(ActionType.SWITCH_TO_FRAME);
        assertNotNull(ActionType.SWITCH_TO_DEFAULT_CONTENT);
        assertNotNull(ActionType.SWITCH_TO_WINDOW);
        assertNotNull(ActionType.CLOSE_WINDOW);
        assertNotNull(ActionType.MAXIMIZE_WINDOW);
        assertNotNull(ActionType.MINIMIZE_WINDOW);
        assertNotNull(ActionType.SET_WINDOW_SIZE);
    }

    @Test(description = "Test advanced mouse operations exist")
    public void testAdvancedMouseOperationsExist() {
        assertNotNull(ActionType.DOUBLE_CLICK);
        assertNotNull(ActionType.RIGHT_CLICK);
        assertNotNull(ActionType.HOVER);
        assertNotNull(ActionType.DRAG_AND_DROP);
        assertNotNull(ActionType.CLICK_AND_HOLD);
        assertNotNull(ActionType.RELEASE);
    }

    @Test(description = "Test scroll operations exist")
    public void testScrollOperationsExist() {
        assertNotNull(ActionType.SCROLL_TO_ELEMENT);
        assertNotNull(ActionType.SCROLL_UP);
        assertNotNull(ActionType.SCROLL_DOWN);
        assertNotNull(ActionType.SCROLL_TO_TOP);
        assertNotNull(ActionType.SCROLL_TO_BOTTOM);
    }

    @Test(description = "Test screenshot and debugging operations exist")
    public void testScreenshotAndDebuggingOperationsExist() {
        assertNotNull(ActionType.TAKE_SCREENSHOT);
        assertNotNull(ActionType.HIGHLIGHT_ELEMENT);
    }

    @Test(description = "Test JavaScript and file operations exist")
    public void testJavaScriptAndFileOperationsExist() {
        assertNotNull(ActionType.EXECUTE_JAVASCRIPT);
        assertNotNull(ActionType.UPLOAD_FILE);
    }

    @Test(description = "Test form operations exist")
    public void testFormOperationsExist() {
        assertNotNull(ActionType.FILL_FORM);
        assertEquals(ActionType.FILL_FORM.getDescription(), "Fill multiple form fields");
    }

    @Test(description = "Test validation operations exist")
    public void testValidationOperationsExist() {
        assertNotNull(ActionType.VALIDATE_TITLE);
        assertNotNull(ActionType.VALIDATE_TEXT);
        assertNotNull(ActionType.VALIDATE_ATTRIBUTE);
        assertNotNull(ActionType.VALIDATE_ELEMENT_PRESENT);
        assertNotNull(ActionType.VALIDATE_ELEMENT_VISIBLE);
    }

    @Test(description = "Test all descriptions are not null or empty")
    public void testAllDescriptionsNotNullOrEmpty() {
        for (ActionType action : ActionType.values()) {
            assertNotNull(action.getDescription(),
                "Description should not be null for: " + action.name());
            assertFalse(action.getDescription().isEmpty(),
                "Description should not be empty for: " + action.name());
        }
    }

    @Test(description = "Test all descriptions are unique")
    public void testAllDescriptionsAreUnique() {
        Set<String> descriptions = new HashSet<>();
        for (ActionType action : ActionType.values()) {
            boolean added = descriptions.add(action.getDescription());
            assertTrue(added, "Duplicate description found: " + action.getDescription());
        }
    }

    @Test(description = "Test valueOf")
    public void testValueOf() {
        assertEquals(ActionType.valueOf("CLICK"), ActionType.CLICK);
        assertEquals(ActionType.valueOf("NAVIGATE_TO"), ActionType.NAVIGATE_TO);
        assertEquals(ActionType.valueOf("FILL_FORM"), ActionType.FILL_FORM);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          description = "Test valueOf with invalid name throws exception")
    public void testValueOfInvalidThrowsException() {
        ActionType.valueOf("INVALID_ACTION");
    }

    @Test(description = "Test action type name pattern")
    public void testActionTypeNamePattern() {
        // All enum names should be SCREAMING_SNAKE_CASE
        for (ActionType action : ActionType.values()) {
            String name = action.name();
            assertTrue(name.equals(name.toUpperCase()),
                "Enum name should be uppercase: " + name);
            assertFalse(name.contains(" "),
                "Enum name should not contain spaces: " + name);
        }
    }

    @Test(description = "Test action categories by prefix")
    public void testActionCategoriesByPrefix() {
        long waitActions = Arrays.stream(ActionType.values())
                .filter(a -> a.name().startsWith("WAIT_"))
                .count();
        assertTrue(waitActions >= 4, "Should have at least 4 WAIT_ actions");

        long validateActions = Arrays.stream(ActionType.values())
                .filter(a -> a.name().startsWith("VALIDATE_"))
                .count();
        assertTrue(validateActions >= 4, "Should have at least 4 VALIDATE_ actions");

        long scrollActions = Arrays.stream(ActionType.values())
                .filter(a -> a.name().startsWith("SCROLL_"))
                .count();
        assertTrue(scrollActions >= 4, "Should have at least 4 SCROLL_ actions");

        long navigateActions = Arrays.stream(ActionType.values())
                .filter(a -> a.name().startsWith("NAVIGATE_"))
                .count();
        assertTrue(navigateActions >= 3, "Should have at least 3 NAVIGATE_ actions");
    }
}
