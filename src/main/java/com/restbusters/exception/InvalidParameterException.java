package com.restbusters.exception;

/**
 * @author sasha on 2020-09-10
 * @project qreasp
 */
public class InvalidParameterException extends Exception{
    public InvalidParameterException(String errorMessage) {
        super(errorMessage);
    }
}
