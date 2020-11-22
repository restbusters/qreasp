package com.restbusters.exception;

/**
 * @author sasha on 2020-09-10
 * @project qreasp
 */
public class SwaggerTitleNotSet extends Exception{
    public SwaggerTitleNotSet(String errorMessage) {
        super(errorMessage);
    }
}
