package com.restbusters.core.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.restbusters.core.config.GlobalConfig;
import com.sumologic.client.Credentials;
import com.sumologic.client.SumoLogicClient;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;

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
    private Faker faker = new Faker();
    private SumoLogicClient sumoClient;




    private GlobalResourceManager(){
        try {
            createSumoLogicClient();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public static synchronized GlobalResourceManager getInstance(){
        if(instance == null){
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

    public Faker getFaker() {
        return faker;
    }

    private void createSumoLogicClient() throws MalformedURLException {
        Credentials credential = new Credentials("suaFcml4FWzzYw", "ipTgYq3h7k6IuO66bu3It1iPiRi7wd5pF7JVptbs59k4Id1qkvQxPfbo9QNmDFvd");
        this.sumoClient = new SumoLogicClient(credential);
        this.sumoClient.setURL("https://api.us2.sumologic.com");
    }

    public SumoLogicClient getSumoClient() {
        return sumoClient;
    }
}

