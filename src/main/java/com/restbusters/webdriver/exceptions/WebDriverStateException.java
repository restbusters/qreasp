package com.restbusters.webdriver.exceptions;

/**
 * @author amatsaylo on 9/26/25
 * @project qreasp
 * Base exception for all WebDriver state framework related errors.
 */
public class WebDriverStateException extends RuntimeException {

    public WebDriverStateException(String message) {
        super(message);
    }

    public WebDriverStateException(String message, Throwable cause) {
        super(message, cause);
    }
}