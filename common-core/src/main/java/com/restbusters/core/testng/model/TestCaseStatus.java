package com.restbusters.core.testng.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestCaseStatus{

    @JsonProperty("executionTime")
    private String executionTime;

    @JsonProperty("parmameters")
    private String parmameters;

    @JsonProperty("description")
    private String description;

    @JsonProperty("testClass")
    private String testClass;

    @JsonProperty("error")
    private String error;

    @JsonProperty("testName")
    private String testName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("group")
    private String group;

    @JsonProperty("env")
    private String env;

    public void setExecutionTime(String executionTime){
        this.executionTime = executionTime;
    }

    public String getExecutionTime(){
        return executionTime;
    }

    public void setParmameters(String parmameters){
        this.parmameters = parmameters;
    }

    public String getParmameters(){
        return parmameters;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setTestClass(String testClass){
        this.testClass = testClass;
    }

    public String getTestClass(){
        return testClass;
    }

    public void setError(String error){
        this.error = error;
    }

    public String getError(){
        return error;
    }

    public void setTestName(String testName){
        this.testName = testName;
    }

    public String getTestName(){
        return testName;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return "TestCaseStatus{" +
                "executionTime='" + executionTime + '\'' +
                ", parmameters='" + parmameters + '\'' +
                ", description='" + description + '\'' +
                ", testClass='" + testClass + '\'' +
                ", error='" + error + '\'' +
                ", testName='" + testName + '\'' +
                ", status='" + status + '\'' +
                ", group='" + group + '\'' +
                ", env='" + env + '\'' +
                '}';
    }
}