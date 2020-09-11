package com.restbusters.core.exception;

/**
 * @author sasha on 2020-09-10
 * @project qreasp
 */
public class RecordNotFound extends Exception{
    public RecordNotFound(String errorMessage) {
        super(errorMessage);
    }
}
