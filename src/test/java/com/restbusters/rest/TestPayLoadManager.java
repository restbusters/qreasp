package com.restbusters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.payload.PayloadManager;
import com.restbusters.rest.payload.model.PayloadTemplate;
import com.restbusters.util.common.FileUtils;
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
public class TestPayLoadManager {

    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();
    private String jsonPayloads;
    private PayloadManager payloadManager;
    private Map<String,String> defaultTemplateFilter;

    @BeforeClass(alwaysRun = true)
    private void setUp() throws IOException {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.jsonPayloads = FileUtils.getFileOnClassPathAsString("payload/payloads.json");
        this.payloadManager = PayloadManager.getInstance(jsonPayloads);
        this.defaultTemplateFilter = new HashMap<>();
        defaultTemplateFilter.put("operationId", "testOperationId");
        defaultTemplateFilter.put("payloadName", "testPayloads");
    }

    @Test
    private void getPayloadMetaData() throws IOException {
        Map<String,Object> payloadMetaData = payloadManager.getPayloadMetaData(this.defaultTemplateFilter);
        Assert.assertNotNull(payloadMetaData);
    }

    @Test
    private void getPayloadMetaDataAsString() throws IOException {
        String payloadMetaData = payloadManager.getPayloadMetaDataAsString(this.defaultTemplateFilter);
        Assert.assertNotNull(payloadMetaData);
    }

    @Test
    private void searchPayloadUsing3Parameters() throws IOException {
        Map<String,String> filters = new HashMap<>();
        filters.put("operationId", "testOperationId2");
        filters.put("payloadName", "testPayloads2");
        filters.put("description", "description2");
        String payloadMetaData = payloadManager.getPayloadMetaDataAsString(filters);
        Assert.assertNotNull(payloadMetaData);
        PayloadTemplate payloadTemplate  = objectMapper.readValue(payloadMetaData, PayloadTemplate.class);
        Assert.assertEquals(payloadTemplate.getPayloadName(), filters.get("payloadName"));
        Assert.assertEquals(payloadTemplate.getDescription(), filters.get("description"));
    }

    @Test()
    private void testPayloadManager3Keys() throws IOException {
        Map<String,Object> payLoadValues = new HashMap<>();
        payLoadValues.put("key1", "key1 value");
        payLoadValues.put("list1", Arrays.asList("foo", "bar").toString());
        Map<String,Object> payloadMetaData = payloadManager.getPayloadMetaData(this.defaultTemplateFilter);
        Assert.assertNotNull(payloadMetaData);
        String payloadTemplate = payloadManager.getPayloadTemplateAsString(payloadMetaData);
        String payload = payloadManager.renderPayload(payloadTemplate, payLoadValues);
        logger.info("\n" + payload);
    }

    @Test
    private void realJTwigTemplate() throws IOException {
        //not working as expected
        Map<String,Object> filters = new HashMap<>();
        filters.put("list", Arrays.asList("foo2", "bar2"));
        String twigTemplate = FileUtils.getFileOnClassPathAsString("payload/twig-example.twig");
        String payload = payloadManager.renderPayload(twigTemplate, filters);
        logger.info("payload \n{}", payload);
    }

    @Test
    private void usingPojo() throws IOException {
        String nameExpected = "name";
        Model model1 = Model.builder()
                .age(12)
                .group("Group")
                .name(nameExpected)
                .build();
        String payload1 = payloadManager.renderPayload(model1);
        logger.info("payload \n{}", payload1);
        Assert.assertNotNull(payload1);
        Assert.assertEquals(nameExpected, JsonPath.read(payload1, "$.name"));
    }

}
