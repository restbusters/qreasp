package com.restbusters.config;


import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
public interface GlobalConfig extends Config {

    @DefaultValue("${java.home}")
    String javaHome();

    @DefaultValue("${ENV_NAME}")
    String env();

    @DefaultValue("${JIRA_URL}")
    String jiraUrl();

    @DefaultValue("${JIRA_USER}")
    String jiraUser();

    @DefaultValue("${JIRA_PASSWORD}")
    String jiraPassword();

    @DefaultValue("true")
    boolean sendTestResultToElastic();


}

