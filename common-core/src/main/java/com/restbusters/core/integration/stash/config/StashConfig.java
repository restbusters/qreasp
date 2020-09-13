package com.restbusters.core.integration.stash.config;


import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
public interface StashConfig extends Config {

    @DefaultValue("${STASH_HOST}")
    String jiraUrl();

    @DefaultValue("${STASH_TOKEN}")
    String jiraUser();

}

