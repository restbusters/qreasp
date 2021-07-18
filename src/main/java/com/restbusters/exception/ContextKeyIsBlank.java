package com.restbusters.exception;

/**
 * @author sasha on 2020-09-10
 * @project qreasp
 */
public class ContextKeyIsBlank extends Exception{
    public ContextKeyIsBlank(String errorMessage) {
        super(errorMessage);
    }
}
