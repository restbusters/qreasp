package com.restbusters.core.testng;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.core.resource.GlobalResourceManager;
import com.restbusters.core.testng.model.TestCaseStatus;
import com.mashape.unirest.http.Unirest;

public class StatusSender {

    private static final ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json";
    private static final String ELASTICSEARCH_URL = "http://localhost:9200/app/suite";

    public static void send(final TestCaseStatus testCaseStatus){
        try {
            Unirest.post(ELASTICSEARCH_URL)
                    .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                    .body(objectMapper.writeValueAsString(testCaseStatus)).asJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
