package com.restbusters.integraton.swagger.model;

/**
 * @author Ed Vayn on 2025-05-01
 * @project qreasp
 */

public class OpenApiParseException extends Exception {
    public OpenApiParseException(String message) {
        super(message);
    }

    public OpenApiParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
