package com.restbusters.exception;

/**
 * @author sasha on 2020-09-10
 * @project qreasp
 */
public class ScenarioExecutionException extends Exception{
    public ScenarioExecutionException(String errorMessage) {
        super(errorMessage);
    }
}
