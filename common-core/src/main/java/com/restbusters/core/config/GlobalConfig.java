package com.restbusters.core.config;


import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
public interface GlobalConfig extends Config {

    @DefaultValue("${java.home}")
    String javaHome();

    @DefaultValue("${ENV_NAME}")
    String env();

    @DefaultValue("true")
    boolean sendTestResultToElastic();


}

