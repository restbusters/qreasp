package com.restbusters.rest.util.deployment.model.execution;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskExecResult {

    @JsonProperty("result")
    private String result;

    @JsonProperty("executionType")
    private String executionType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("error")
    private String error;

    @JsonProperty("status")
    private String status;

    @JsonProperty("state")
    private String state;

}