package com.restbusters.http.helper.model;


import com.restbusters.http.helper.ExecutionState;
import lombok.Getter;
import lombok.Setter;

/**
 * @author alexander matsaylo on 07/01/2025
 * @project qreasp
 */
@Setter
@Getter
public class HttpExecutionResult {

    private ExecutionState status;
    private int statusCode;
    private String responseBody;
    private String error;
    private Long executionTime;
    private String testCaseName;
    private String payload;
    private String convertedExecutionTime;
    private boolean isSuccessful;
}
