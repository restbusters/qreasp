package com.restbusters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.payload.FreeMarkerPayloadManager;
import com.restbusters.rest.payload.PayloadManager;
import com.restbusters.rest.payload.model.PayloadTemplate;
import com.restbusters.util.common.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author smatsaylo on 10/15/19
 * @project qreasp
 */
public class TestFreeMarkerPayloadManager {

    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();
    private String jsonPayloads;
    private FreeMarkerPayloadManager payloadManager;
    private Map<String,String> defaultTemplateFilter;

    @BeforeClass(alwaysRun = true)
    private void setUp() throws IOException {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.jsonPayloads = FileUtils.getFileOnClassPathAsString("payload/payloads.json");
        this.payloadManager = FreeMarkerPayloadManager.getInstance(jsonPayloads);
        this.defaultTemplateFilter = new HashMap<>();
        defaultTemplateFilter.put("operationId", "testOperationId2");
        defaultTemplateFilter.put("payloadName", "testPayloads2");
    }

    @Test(threadPoolSize = 5, invocationCount = 5)
    private void getPayloadMetaData() throws IOException {
        String expectedValue = RandomStringUtils.randomAlphabetic(5);
        Map<String,Object> payLoadValues = new HashMap<>();
        payLoadValues.put("key1", expectedValue);
        payLoadValues.put("key2Attr", "key2");
        payLoadValues.put("list1", Arrays.asList("foo", "bar").toString());
        String payloadMetaData = payloadManager.getPayload(this.defaultTemplateFilter, payLoadValues);
        Assert.assertEquals(expectedValue, JsonPath.read(payloadMetaData, "$.key1"));
        logger.info(payloadMetaData);
    }
}
