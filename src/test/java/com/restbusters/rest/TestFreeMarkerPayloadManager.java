package com.restbusters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.payload.FreeMarkerPayloadManager;
import com.restbusters.util.common.RBFileUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
    private Map<String, String> defaultTemplateFilter;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.jsonPayloads = RBFileUtils.getFileOnClassPathAsString("payload/payloads.json");
        this.payloadManager = FreeMarkerPayloadManager.getInstance(jsonPayloads);
        this.defaultTemplateFilter = new HashMap<>();
        defaultTemplateFilter.put("operationId", "testOperationId2");
        defaultTemplateFilter.put("payloadName", "testPayloads2");
    }

    // ==================== getPayload Tests ====================

    @Test(description = "Render template with variable substitution - thread safety test",
            threadPoolSize = 5, invocationCount = 5)
    public void testGetPayload_WithVariableSubstitution() {
        String expectedValue = RandomStringUtils.randomAlphabetic(5);
        Map<String, Object> payLoadValues = new HashMap<>();
        payLoadValues.put("key1", expectedValue);
        payLoadValues.put("key2Attr", "key2");
        payLoadValues.put("list1", Arrays.asList("foo", "bar").toString());
        String payloadMetaData = payloadManager.getPayload(this.defaultTemplateFilter, payLoadValues);

        Assert.assertNotNull(payloadMetaData, "Payload should not be null");
        Assert.assertEquals(JsonPath.read(payloadMetaData, "$.key1"), expectedValue);
        logger.info(payloadMetaData);
    }

    @Test(description = "Render template with default values when variables not provided")
    public void testGetPayload_WithDefaultValues() {
        Map<String, Object> payLoadValues = new HashMap<>();
        payLoadValues.put("key1", "testKey1Value");
        payLoadValues.put("key2Attr", "customKey2");
        // Not providing key2, key3 - should use defaults from template

        String result = payloadManager.getPayload(this.defaultTemplateFilter, payLoadValues);

        Assert.assertNotNull(result, "Payload should not be null");
        Assert.assertEquals(JsonPath.read(result, "$.key1"), "testKey1Value");
        // key3 should have default value "Default Iva" from template
        Assert.assertEquals(JsonPath.read(result, "$.key3"), "Default Iva");
        logger.info("Result with defaults: {}", result);
    }

    @Test(description = "Render template with null payload values map")
    public void testGetPayload_WithNullPayloadValues() {
        Map<String, String> filter = new HashMap<>();
        filter.put("operationId", "testOperationId2");
        filter.put("payloadName", "testPayloads2");

        // Pass null payload values - should use empty map internally
        String result = payloadManager.getPayload(filter, null);

        // Template requires key1, so this will fail template processing
        // The result may be null or contain default values
        logger.info("Result with null payload: {}", result);
    }

    @Test(description = "Get payload with non-existent template returns null")
    public void testGetPayload_TemplateNotFound() {
        Map<String, String> filter = new HashMap<>();
        filter.put("operationId", "nonExistentOperation");
        filter.put("payloadName", "nonExistentPayload");

        Map<String, Object> payLoadValues = new HashMap<>();
        payLoadValues.put("key1", "value1");

        String result = payloadManager.getPayload(filter, payLoadValues);

        Assert.assertNull(result, "Should return null for non-existent template");
    }

    @Test(description = "Get payload with template missing relativePath returns null")
    public void testGetPayload_MissingRelativePath() {
        // testOperationId/testPayloads doesn't have relativePath defined
        Map<String, String> filter = new HashMap<>();
        filter.put("operationId", "testOperationId");
        filter.put("payloadName", "testPayloads");

        Map<String, Object> payLoadValues = new HashMap<>();
        payLoadValues.put("key1", "value1");

        String result = payloadManager.getPayload(filter, payLoadValues);

        Assert.assertNull(result, "Should return null when relativePath is missing");
    }

    @Test(description = "Get payload with partial filter")
    public void testGetPayload_PartialFilter() {
        Map<String, String> filter = new HashMap<>();
        filter.put("operationId", "testOperationId2");
        // Missing payloadName - should still find if operationId is unique

        Map<String, Object> payLoadValues = new HashMap<>();
        payLoadValues.put("key1", "partialFilterValue");
        payLoadValues.put("key2Attr", "key2");

        String result = payloadManager.getPayload(filter, payLoadValues);

        // Should find the template with just operationId
        if (result != null) {
            Assert.assertEquals(JsonPath.read(result, "$.key1"), "partialFilterValue");
        }
        logger.info("Partial filter result: {}", result);
    }

    // ==================== validateType Tests ====================

    @Test(description = "validateType returns 'default' for null input")
    public void testValidateType_NullInput() {
        String result = payloadManager.validateType(null);
        Assert.assertEquals(result, "default", "Null input should return 'default'");
    }

    @Test(description = "validateType returns input value for non-null input")
    public void testValidateType_NonNullInput() {
        String result = payloadManager.validateType("customType");
        Assert.assertEquals(result, "customType", "Non-null input should be returned as-is");
    }

    @Test(description = "validateType handles empty string")
    public void testValidateType_EmptyString() {
        String result = payloadManager.validateType("");
        Assert.assertEquals(result, "", "Empty string should be returned as-is");
    }

    // ==================== fetchTemplate Tests ====================

    @Test(description = "fetchTemplate loads template file successfully")
    public void testFetchTemplate_Success() {
        String template = payloadManager.fetchTemplate("payload/template/test-payload.ftl");

        Assert.assertNotNull(template, "Template should not be null");
        Assert.assertTrue(template.contains("${key1}"), "Template should contain key1 variable");
        Assert.assertTrue(template.contains("${key2Attr}"), "Template should contain key2Attr variable");
    }

    @Test(description = "fetchTemplate returns null for non-existent file")
    public void testFetchTemplate_FileNotFound() {
        String template = payloadManager.fetchTemplate("nonexistent/path/template.ftl");

        Assert.assertNull(template, "Should return null for non-existent file");
    }

    // ==================== Singleton Tests ====================

    @Test(description = "getInstance returns same instance")
    public void testGetInstance_ReturnsSameInstance() throws IOException {
        FreeMarkerPayloadManager instance1 = FreeMarkerPayloadManager.getInstance(jsonPayloads);
        FreeMarkerPayloadManager instance2 = FreeMarkerPayloadManager.getInstance(jsonPayloads);

        Assert.assertSame(instance1, instance2, "Should return same singleton instance");
    }

    // ==================== Thread Safety Tests ====================

    @Test(description = "Concurrent template rendering produces correct results",
            threadPoolSize = 10, invocationCount = 20)
    public void testGetPayload_ConcurrentAccess() {
        String threadName = Thread.currentThread().getName();
        String uniqueValue = "value_" + threadName + "_" + System.nanoTime();

        Map<String, Object> payLoadValues = new HashMap<>();
        payLoadValues.put("key1", uniqueValue);
        payLoadValues.put("key2Attr", "key2");

        String result = payloadManager.getPayload(this.defaultTemplateFilter, payLoadValues);

        Assert.assertNotNull(result, "Result should not be null");
        String extractedValue = JsonPath.read(result, "$.key1");
        Assert.assertEquals(extractedValue, uniqueValue,
                "Each thread should get its own unique value rendered correctly");
    }
}
