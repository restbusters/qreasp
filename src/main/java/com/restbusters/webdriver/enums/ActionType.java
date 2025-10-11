package com.restbusters.webdriver.enums;

import lombok.Getter;
/**
 * Enumeration of all supported WebDriver actions in the state-driven framework.
 * Each action type represents a specific operation that can be performed on web elements or the browser.
 * @author amatsaylo on 9/26/25
 * @project qreasp
 */
@Getter
public enum ActionType {
    // Basic Element Actions
    CLICK("Click on element"),
    SEND_KEYS("Send keys to element"),
    CLEAR("Clear element content"),
    SUBMIT("Submit form element"),

    // Text and Attribute Operations
    GET_TEXT("Get element text"),
    GET_ATTRIBUTE("Get element attribute"),
    GET_VALUE("Get element value"),

    // Selection Operations
    SELECT_FROM_DROPDOWN("Select from dropdown"),
    SELECT_BY_INDEX("Select by index"),
    SELECT_BY_VALUE("Select by value"),

    // Wait Operations
    WAIT_FOR_ELEMENT("Wait for element presence"),
    WAIT_FOR_ELEMENT_CLICKABLE("Wait for element to be clickable"),
    WAIT_FOR_ELEMENT_VISIBLE("Wait for element visibility"),
    WAIT_FOR_ELEMENT_INVISIBLE("Wait for element to be invisible"),
    WAIT_FOR_TEXT_PRESENT("Wait for text to be present"),

    // Navigation Operations
    NAVIGATE_TO("Navigate to URL"),
    NAVIGATE_BACK("Navigate back"),
    NAVIGATE_FORWARD("Navigate forward"),
    REFRESH("Refresh page"),

    // Window and Frame Operations
    SWITCH_TO_FRAME("Switch to frame"),
    SWITCH_TO_DEFAULT_CONTENT("Switch to default content"),
    SWITCH_TO_WINDOW("Switch to window"),
    CLOSE_WINDOW("Close current window"),
    MAXIMIZE_WINDOW("Maximize window"),
    MINIMIZE_WINDOW("Minimize window"),
    SET_WINDOW_SIZE("Set window size"),

    // Advanced Mouse Operations
    DOUBLE_CLICK("Double click on element"),
    RIGHT_CLICK("Right click on element"),
    HOVER("Hover over element"),
    DRAG_AND_DROP("Drag and drop element"),
    CLICK_AND_HOLD("Click and hold element"),
    RELEASE("Release mouse button"),

    // Scroll Operations
    SCROLL_TO_ELEMENT("Scroll to element"),
    SCROLL_UP("Scroll up"),
    SCROLL_DOWN("Scroll down"),
    SCROLL_TO_TOP("Scroll to top"),
    SCROLL_TO_BOTTOM("Scroll to bottom"),

    // Screenshot and Debugging
    TAKE_SCREENSHOT("Take screenshot"),
    HIGHLIGHT_ELEMENT("Highlight element"),

    // JavaScript Operations
    EXECUTE_JAVASCRIPT("Execute JavaScript"),

    // File Operations
    UPLOAD_FILE("Upload file"),

    // Form Operations
    FILL_FORM("Fill multiple form fields"),

    // Validation Operations
    VALIDATE_TITLE("Validate page title"),
    VALIDATE_TEXT("Validate element text"),
    VALIDATE_ATTRIBUTE("Validate element attribute"),
    VALIDATE_ELEMENT_PRESENT("Validate element is present"),
    VALIDATE_ELEMENT_VISIBLE("Validate element is visible");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }
}