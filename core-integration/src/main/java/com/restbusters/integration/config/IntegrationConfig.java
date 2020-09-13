package com.restbusters.integration.config;


import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
public interface IntegrationConfig extends Config {

    @DefaultValue("${JIRA_URL}")
    String jiraHost();

    @DefaultValue("${JIRA_USER}")
    String jiraUser();

    @DefaultValue("${JIRA_PASSWORD}")
    String jiraPassword();

    @DefaultValue("${STASH_HOST}")
    String stashHost();

    @DefaultValue("${STASH_TOKEN}")
    String stashToken();
}

