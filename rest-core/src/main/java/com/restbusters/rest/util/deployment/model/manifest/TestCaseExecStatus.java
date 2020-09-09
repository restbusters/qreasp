package com.restbusters.rest.util.deployment.model.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author smatsaylo on 4/8/20
 * @project dart
 */

public class TestCaseExecStatus extends ManifestModel {


    private Long id;


    @JsonProperty("name")

    private String name;


    @JsonProperty("suite")

    private String suite;


    @JsonProperty("className")

    private String className;


    @JsonProperty("group")

    private String group;


    @JsonProperty("runIdGuid")

    private String runIdGuid;


    @JsonProperty("jobName")

    private String jobName;


    @JsonProperty("status")

    private String testStatus;

    @JsonProperty("message")

    private String testResult;


    @JsonProperty("jiraId")

    private String jiraId;


    @JsonProperty("jiraStatus")

    private String jiraStatus;

    @JsonProperty("causes")

    private String causes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRunIdGuid() {
        return runIdGuid;
    }

    public void setRunIdGuid(String runIdGuid) {
        this.runIdGuid = runIdGuid;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getJiraId() {
        return jiraId;
    }

    public void setJiraId(String jiraId) {
        this.jiraId = jiraId;
    }

    public String getJiraStatus() {
        return jiraStatus;
    }

    public void setJiraStatus(String jiraStatus) {
        this.jiraStatus = jiraStatus;
    }

    public String getCauses() {
        return causes;
    }

    public void setCauses(String causes) {
        this.causes = causes;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
