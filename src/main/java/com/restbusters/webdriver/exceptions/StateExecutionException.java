package com.restbusters.webdriver.exceptions;

import com.restbusters.webdriver.models.WebDriverState;
import lombok.Getter;

/**
 * Exception thrown when state execution fails.
 * Contains the state that failed and action type information.
 * @author amatsaylo on 9/26/25
 * @project qreasp
 */
@Getter
public class StateExecutionException extends WebDriverStateException {

    private final WebDriverState state;
    private final String actionType;

    public StateExecutionException(String message, WebDriverState state) {
        super(message);
        this.state = state;
        this.actionType = state != null && state.getActionType() != null ?
                state.getActionType().name() : "UNKNOWN";
    }

    public StateExecutionException(String message, Throwable cause, WebDriverState state) {
        super(message, cause);
        this.state = state;
        this.actionType = state != null && state.getActionType() != null ?
                state.getActionType().name() : "UNKNOWN";
    }
}