package com.restbusters.webdriver.core;

import com.restbusters.webdriver.enums.ActionType;
import com.restbusters.webdriver.exceptions.StateExecutionException;
import com.restbusters.webdriver.models.ExecutionResult;
import com.restbusters.webdriver.models.FormData;
import com.restbusters.webdriver.models.ValidationResult;
import com.restbusters.webdriver.models.WebDriverState;
import com.restbusters.webdriver.utils.LocatorUtils;
import com.restbusters.webdriver.utils.ScreenshotUtils;
import com.restbusters.webdriver.utils.WaitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Core class responsible for executing WebDriver actions based on WebDriverState.
 * This is where the state-driven approach is implemented.
 * @author amatsaylo on 9/26/25
 * @project qreasp
 */
public class ActionExecutor {

    private static final Logger log = LoggerFactory.getLogger(ActionExecutor.class);

    /**
     * Executes the action specified in the WebDriverState.
     */
    public ExecutionResult executeAction(WebDriverState state) {
        LocalDateTime startTime = LocalDateTime.now();
        String actionTypeStr = state.getActionType() != null ? state.getActionType().name() : "UNKNOWN";

        log.info("Executing action: {} with locator: {}", actionTypeStr, state.getLocatorString());

        try {
            validateState(state);

            // Take screenshot before action if requested
            byte[] screenshotBefore = null;
            if (state.isTakeScreenshotBefore()) {
                screenshotBefore = ScreenshotUtils.takeScreenshot(state.getWebDriver());
            }

            // Execute the action
            String resultValue = performAction(state);

            // Take screenshot after action if requested, or if the action IS taking a screenshot
            byte[] screenshotAfter = null;
            if (state.isTakeScreenshotAfter() || state.getActionType() == ActionType.TAKE_SCREENSHOT) {
                screenshotAfter = ScreenshotUtils.takeScreenshot(state.getWebDriver());
            }

            // Create successful result
            ExecutionResult result = ExecutionResult.builder()
                    .successful(true)
                    .actionType(actionTypeStr)
                    .startTime(startTime)
                    .endTime(LocalDateTime.now())
                    .executionDuration(Duration.between(startTime, LocalDateTime.now()))
                    .resultValue(resultValue)
                    .screenshotBefore(screenshotBefore)
                    .screenshotAfter(screenshotAfter)
                    .elementLocator(state.getLocatorString())
                    .build();

            log.info("Successfully executed action: {}", actionTypeStr);
            return result;

        } catch (Exception e) {
            log.error("Failed to execute action: {}", actionTypeStr, e);

            // Take screenshot on failure if requested
            byte[] screenshotOnFailure = null;
            if (state.isTakeScreenshotOnFailure()) {
                screenshotOnFailure = ScreenshotUtils.takeScreenshot(state.getWebDriver());
            }

            ExecutionResult result = ExecutionResult.builder()
                    .successful(false)
                    .actionType(actionTypeStr)
                    .startTime(startTime)
                    .endTime(LocalDateTime.now())
                    .executionDuration(Duration.between(startTime, LocalDateTime.now()))
                    .errorMessage(e.getMessage())
                    .screenshotAfter(screenshotOnFailure)
                    .elementLocator(state.getLocatorString())
                    .build();

            return result;
        }
    }

    /**
     * Validates that the state has all required information for execution.
     */
    private void validateState(WebDriverState state) {
        if (state == null) {
            throw new StateExecutionException("WebDriverState cannot be null", state);
        }

        if (state.getWebDriver() == null) {
            throw new StateExecutionException("WebDriver cannot be null", state);
        }

        if (state.getActionType() == null) {
            throw new StateExecutionException("ActionType cannot be null", state);
        }

        // Validate locator for actions that require it
        if (requiresLocator(state.getActionType()) && !LocatorUtils.isValidLocatorMap(state.getLocator())) {
            throw new StateExecutionException("Valid locator is required for action: " + state.getActionType(), state);
        }
    }

    /**
     * Determines if an action type requires a locator.
     */
    private boolean requiresLocator(ActionType actionType) {
        switch (actionType) {
            case NAVIGATE_TO:
            case NAVIGATE_BACK:
            case NAVIGATE_FORWARD:
            case REFRESH:
            case MAXIMIZE_WINDOW:
            case MINIMIZE_WINDOW:
            case CLOSE_WINDOW:
            case SWITCH_TO_DEFAULT_CONTENT:
            case TAKE_SCREENSHOT:
            case EXECUTE_JAVASCRIPT:
            case VALIDATE_TITLE:  // Title validation doesn't need an element locator
            case FILL_FORM:  // FILL_FORM uses formDataList, not a single locator
                return false;
            default:
                return true;
        }
    }

    /**
     * Performs the actual WebDriver action and returns result value if applicable.
     */
    private String performAction(WebDriverState state) {
        switch (state.getActionType()) {
            case CLICK:
                return performClick(state);
            case SEND_KEYS:
                return performSendKeys(state);
            case CLEAR:
                return performClear(state);
            case SUBMIT:
                return performSubmit(state);
            case GET_TEXT:
                return performGetText(state);
            case GET_ATTRIBUTE:
                return performGetAttribute(state);
            case GET_VALUE:
                return performGetValue(state);
            case SELECT_FROM_DROPDOWN:
                return performSelectFromDropdown(state);
            case WAIT_FOR_ELEMENT:
                return performWaitForElement(state);
            case WAIT_FOR_ELEMENT_CLICKABLE:
                return performWaitForElementClickable(state);
            case WAIT_FOR_ELEMENT_VISIBLE:
                return performWaitForElementVisible(state);
            case NAVIGATE_TO:
                return performNavigateTo(state);
            case NAVIGATE_BACK:
                return performNavigateBack(state);
            case NAVIGATE_FORWARD:
                return performNavigateForward(state);
            case REFRESH:
                return performRefresh(state);
            case DOUBLE_CLICK:
                return performDoubleClick(state);
            case RIGHT_CLICK:
                return performRightClick(state);
            case HOVER:
                return performHover(state);
            case SCROLL_TO_ELEMENT:
                return performScrollToElement(state);
            case TAKE_SCREENSHOT:
                return performTakeScreenshot(state);
            case FILL_FORM:
                return performFillForm(state);
            case MAXIMIZE_WINDOW:
                return performMaximizeWindow(state);
            case VALIDATE_TITLE:
                return performValidateTitle(state);
            case VALIDATE_TEXT:
                return performValidateText(state);
            default:
                throw new StateExecutionException("Action type not implemented: " + state.getActionType(), state);
        }
    }

    // ===== Action Implementations =====

    private String performClick(WebDriverState state) {
        WebElement element = findElement(state);
        element.click();
        return "clicked";
    }

    private String performSendKeys(WebDriverState state) {
        WebElement element = findElement(state);
        element.sendKeys(state.getValue());
        return "keys sent: " + state.getValue();
    }

    private String performClear(WebDriverState state) {
        WebElement element = findElement(state);
        element.clear();
        return "cleared";
    }

    private String performSubmit(WebDriverState state) {
        WebElement element = findElement(state);
        element.submit();
        return "submitted";
    }

    private String performGetText(WebDriverState state) {
        WebElement element = findElement(state);
        return element.getText();
    }

    private String performGetAttribute(WebDriverState state) {
        WebElement element = findElement(state);
        String attributeName = state.getAttributeName();
        if (attributeName == null || attributeName.trim().isEmpty()) {
            throw new StateExecutionException("Attribute name is required for GET_ATTRIBUTE action", state);
        }
        return element.getAttribute(attributeName);
    }

    private String performGetValue(WebDriverState state) {
        WebElement element = findElement(state);
        return element.getAttribute("value");
    }

    private String performSelectFromDropdown(WebDriverState state) {
        WebElement element = findElement(state);
        Select select = new Select(element);

        if (state.getDropdownIndex() != null) {
            select.selectByIndex(state.getDropdownIndex());
            return "selected by index: " + state.getDropdownIndex();
        } else if (state.getDropdownValue() != null) {
            select.selectByValue(state.getDropdownValue());
            return "selected by value: " + state.getDropdownValue();
        } else if (state.getDropdownText() != null) {
            select.selectByVisibleText(state.getDropdownText());
            return "selected by text: " + state.getDropdownText();
        } else if (state.getValue() != null) {
            select.selectByVisibleText(state.getValue());
            return "selected by text: " + state.getValue();
        } else {
            throw new StateExecutionException("No selection criteria provided for dropdown", state);
        }
    }

    private String performWaitForElement(WebDriverState state) {
        By locator = LocatorUtils.getByLocator(state.getLocator());
        WaitUtils.waitForElement(state.getWebDriver(), locator, state.getTimeoutInSeconds());
        return "element found";
    }

    private String performWaitForElementClickable(WebDriverState state) {
        By locator = LocatorUtils.getByLocator(state.getLocator());
        WaitUtils.waitForElementClickable(state.getWebDriver(), locator, state.getTimeoutInSeconds());
        return "element clickable";
    }

    private String performWaitForElementVisible(WebDriverState state) {
        By locator = LocatorUtils.getByLocator(state.getLocator());
        WaitUtils.waitForElementVisible(state.getWebDriver(), locator, state.getTimeoutInSeconds());
        return "element visible";
    }

    private String performNavigateTo(WebDriverState state) {
        if (state.getUrl() == null || state.getUrl().trim().isEmpty()) {
            throw new StateExecutionException("URL is required for NAVIGATE_TO action", state);
        }
        state.getWebDriver().navigate().to(state.getUrl());
        return "navigated to: " + state.getUrl();
    }

    private String performNavigateBack(WebDriverState state) {
        state.getWebDriver().navigate().back();
        return "navigated back";
    }

    private String performNavigateForward(WebDriverState state) {
        state.getWebDriver().navigate().forward();
        return "navigated forward";
    }

    private String performRefresh(WebDriverState state) {
        state.getWebDriver().navigate().refresh();
        return "page refreshed";
    }

    private String performDoubleClick(WebDriverState state) {
        WebElement element = findElement(state);
        Actions actions = new Actions(state.getWebDriver());
        actions.doubleClick(element).perform();
        return "double clicked";
    }

    private String performRightClick(WebDriverState state) {
        WebElement element = findElement(state);
        Actions actions = new Actions(state.getWebDriver());
        actions.contextClick(element).perform();
        return "right clicked";
    }

    private String performHover(WebDriverState state) {
        WebElement element = findElement(state);
        Actions actions = new Actions(state.getWebDriver());
        actions.moveToElement(element).perform();
        return "hovered";
    }

    private String performScrollToElement(WebDriverState state) {
        WebElement element = findElement(state);
        JavascriptExecutor js = (JavascriptExecutor) state.getWebDriver();
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        return "scrolled to element";
    }

    private String performTakeScreenshot(WebDriverState state) {
        byte[] screenshot = ScreenshotUtils.takeScreenshot(state.getWebDriver());
        String filename = ScreenshotUtils.saveScreenshotToFile(screenshot, "manual_screenshot");
        return "screenshot saved: " + filename;
    }

    private String performFillForm(WebDriverState state) {
        if (state.getFormDataList() == null || state.getFormDataList().isEmpty()) {
            throw new StateExecutionException("Form data list is required for FILL_FORM action", state);
        }

        List<String> filledFields = new ArrayList<>();
        for (FormData formData : state.getFormDataList()) {
            WebElement element = state.getWebDriver().findElement(LocatorUtils.getByLocator(formData.getLocator()));

            if (formData.isClearBeforeType()) {
                element.clear();
            }

            element.sendKeys(formData.getValue());
            filledFields.add(formData.getFieldName() != null ? formData.getFieldName() : "field");
        }

        return "filled " + filledFields.size() + " fields: " + String.join(", ", filledFields);
    }

    private String performMaximizeWindow(WebDriverState state) {
        state.getWebDriver().manage().window().maximize();
        return "window maximized";
    }

    private String performValidateTitle(WebDriverState state) {
        String actualTitle = state.getWebDriver().getTitle();
        String expectedTitle = state.getExpectedTitle();

        if (expectedTitle == null) {
            throw new StateExecutionException("Expected title is required for VALIDATE_TITLE action", state);
        }

        boolean matches = actualTitle.equals(expectedTitle);
        if (!matches) {
            throw new StateExecutionException(
                    String.format("Title validation failed. Expected: '%s', Actual: '%s'", expectedTitle, actualTitle),
                    state);
        }

        return "title validated: " + actualTitle;
    }

    private String performValidateText(WebDriverState state) {
        WebElement element = findElement(state);
        String actualText = element.getText();
        String expectedText = state.getExpectedText();

        if (expectedText == null) {
            throw new StateExecutionException("Expected text is required for VALIDATE_TEXT action", state);
        }

        boolean matches = actualText.equals(expectedText);
        if (!matches) {
            throw new StateExecutionException(
                    String.format("Text validation failed. Expected: '%s', Actual: '%s'", expectedText, actualText),
                    state);
        }

        return "text validated: " + actualText;
    }

    /**
     * Helper method to find element using the state's locator information.
     */
    private WebElement findElement(WebDriverState state) {
        By locator = LocatorUtils.getByLocator(state.getLocator());
        return state.getWebDriver().findElement(locator);
    }
}