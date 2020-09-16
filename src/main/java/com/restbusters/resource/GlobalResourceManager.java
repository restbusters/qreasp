package com.restbusters.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.restbusters.config.GlobalConfig;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * @author restbusters on 10/15/18
 * @project qreasp
 */

public class GlobalResourceManager {

    private static GlobalResourceManager instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private GlobalConfig globalConfig = ConfigFactory.create(GlobalConfig.class, System.getProperties(), System.getenv());
    private final Configuration configuration = Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();
    private ObjectMapper objectMapper = new ObjectMapper();


    private GlobalResourceManager() {
    }


    public static synchronized GlobalResourceManager getInstance() {
        if (instance == null) {
            instance = new GlobalResourceManager();
        }
        return instance;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }


    public Configuration getConfiguration() {
        return configuration;
    }


    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}



