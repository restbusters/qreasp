package com.restbusters.http.helper;

import com.restbusters.http.helper.model.HttpExecutionResult;
import com.restbusters.rest.client.HttpMethods;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRequest;
import com.restbusters.util.common.RBFileUtils;
import com.restbusters.util.wiremock.WireMockManager;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for HttpRequestHelper - a simplified wrapper for REST client operations.
 */
public class HttpRequestHelperTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private WireMockManager wireMockManager;
    private OkHttpClient client;
    private static final String WIREMOCK_BASE_URL = "http://localhost:8090";

    @BeforeClass
    public void setUp() throws IOException {
        String wireMockStubs = RBFileUtils.getFileOnClassPathAsString("wiremock/wiremock-stubs.json");
        this.wireMockManager = WireMockManager.getInstance(wireMockStubs);
        this.client = RestClientHelper.getInstance().buildNoAuthClient();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        // Don't stop WireMock - let it run for other test classes using the singleton
        // WireMockManager.getInstance() handles restarting if needed
    }

    @Test(description = "Execute GET request and verify successful response")
    public void testExecuteHttpRequest_GetSuccess() throws Exception {
        // Arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethods.GET.getValue())
                .url(WIREMOCK_BASE_URL + "/rest/api/1.0/projects/myproject/repos/myrepo/tags")
                .headers(headers)
                .build();

        OkHttpClient authClient = RestClientHelper.getInstance().buildBearerClient("dummyToken", headers);

        // Act
        HttpExecutionResult result = HttpRequestHelper.executeHttpRequest(authClient, request);

        // Assert
        Assert.assertEquals(result.getStatus(), ExecutionState.COMPLETED);
        Assert.assertEquals(result.getStatusCode(), 200);
        Assert.assertTrue(result.isSuccessful());
        Assert.assertNotNull(result.getResponseBody());
        Assert.assertTrue(result.getResponseBody().contains("TAG"));
        Assert.assertNotNull(result.getExecutionTime());
        Assert.assertTrue(result.getExecutionTime() >= 0);
        Assert.assertNotNull(result.getConvertedExecutionTime());
        logger.info("Execution time: {} ms ({})", result.getExecutionTime(), result.getConvertedExecutionTime());
    }

    @Test(description = "Execute POST request with body")
    public void testExecuteHttpRequest_PostWithBody() throws Exception {
        // Arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        String requestBody = "{\"buildType\": {\"id\": \"testBuildConfigId\"}}";
        HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethods.POST.getValue())
                .url(WIREMOCK_BASE_URL + "/app/rest/buildQueue")
                .requestBody(requestBody)
                .contentType("application/json")
                .headers(headers)
                .build();

        OkHttpClient authClient = RestClientHelper.getInstance().buildBearerClient("dummyToken", headers);

        // Act
        HttpExecutionResult result = HttpRequestHelper.executeHttpRequest(authClient, request);

        // Assert
        Assert.assertEquals(result.getStatus(), ExecutionState.COMPLETED);
        Assert.assertEquals(result.getStatusCode(), 200);
        Assert.assertTrue(result.isSuccessful());
        Assert.assertNotNull(result.getResponseBody());
        Assert.assertTrue(result.getResponseBody().contains("test_job"));
    }

    @Test(description = "Execute request to OAuth endpoint")
    public void testExecuteHttpRequest_OAuthEndpoint() {
        // Arrange
        HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethods.POST.getValue())
                .url(WIREMOCK_BASE_URL + "/oauth/token")
                .requestBody("{}")
                .contentType("application/json")
                .build();

        // Act
        HttpExecutionResult result = HttpRequestHelper.executeHttpRequest(client, request);

        // Assert
        Assert.assertEquals(result.getStatus(), ExecutionState.COMPLETED);
        Assert.assertEquals(result.getStatusCode(), 200);
        Assert.assertTrue(result.isSuccessful());
        Assert.assertTrue(result.getResponseBody().contains("access_token"));
    }

    @Test(description = "Verify execution time is captured correctly")
    public void testExecuteHttpRequest_ExecutionTimeCapture() throws Exception {
        // Arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethods.GET.getValue())
                .url(WIREMOCK_BASE_URL + "/rest/api/1.0/projects/myproject/repos/myrepo/commits")
                .headers(headers)
                .build();

        OkHttpClient authClient = RestClientHelper.getInstance().buildBearerClient("dummyToken", headers);

        // Act
        HttpExecutionResult result = HttpRequestHelper.executeHttpRequest(authClient, request);

        // Assert
        Assert.assertEquals(result.getStatus(), ExecutionState.COMPLETED);
        Assert.assertNotNull(result.getExecutionTime());
        Assert.assertTrue(result.getExecutionTime() >= 0, "Execution time should be non-negative");
        Assert.assertNotNull(result.getConvertedExecutionTime());
        Assert.assertTrue(result.getConvertedExecutionTime().contains("second"));
        Assert.assertTrue(result.getConvertedExecutionTime().contains("millisecond"));
    }

    @Test(description = "Execute request with invalid URL returns SKIPPED status")
    public void testExecuteHttpRequest_InvalidUrl() {
        // Arrange
        HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethods.GET.getValue())
                .url("http://invalid-host-that-does-not-exist-12345.com/api")
                .build();

        // Act
        HttpExecutionResult result = HttpRequestHelper.executeHttpRequest(client, request);

        // Assert
        Assert.assertEquals(result.getStatus(), ExecutionState.SKIPPED);
        Assert.assertNotNull(result.getError());
        Assert.assertFalse(result.isSuccessful());
        logger.info("Expected error: {}", result.getError());
    }

    @Test(description = "Execute GET request for commits endpoint")
    public void testExecuteHttpRequest_GetCommits() throws Exception {
        // Arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethods.GET.getValue())
                .url(WIREMOCK_BASE_URL + "/rest/api/1.0/projects/myproject/repos/myrepo/commits/dummyGitHash")
                .headers(headers)
                .build();

        OkHttpClient authClient = RestClientHelper.getInstance().buildBearerClient("dummyToken", headers);

        // Act
        HttpExecutionResult result = HttpRequestHelper.executeHttpRequest(authClient, request);

        // Assert
        Assert.assertEquals(result.getStatus(), ExecutionState.COMPLETED);
        Assert.assertEquals(result.getStatusCode(), 200);
        Assert.assertTrue(result.isSuccessful());
        Assert.assertTrue(result.getResponseBody().contains("dummyGitHash"));
    }

    @Test(description = "Execute request without body")
    public void testExecuteHttpRequest_NoBody() throws Exception {
        // Arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethods.GET.getValue())
                .url(WIREMOCK_BASE_URL + "/rest/api/1.0/projects/myproject/repos/myrepo/tags")
                .headers(headers)
                .build();

        OkHttpClient authClient = RestClientHelper.getInstance().buildBearerClient("dummyToken", headers);

        // Act
        HttpExecutionResult result = HttpRequestHelper.executeHttpRequest(authClient, request);

        // Assert
        Assert.assertEquals(result.getStatus(), ExecutionState.COMPLETED);
        Assert.assertNull(result.getError());
    }

    @Test(description = "HttpExecutionResult contains all expected fields")
    public void testHttpExecutionResult_AllFieldsPopulated() throws Exception {
        // Arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethods.GET.getValue())
                .url(WIREMOCK_BASE_URL + "/rest/api/1.0/projects/myproject/repos/myrepo/tags")
                .headers(headers)
                .build();

        OkHttpClient authClient = RestClientHelper.getInstance().buildBearerClient("dummyToken", headers);

        // Act
        HttpExecutionResult result = HttpRequestHelper.executeHttpRequest(authClient, request);

        // Assert - verify all fields are properly set
        Assert.assertNotNull(result.getStatus(), "Status should not be null");
        Assert.assertTrue(result.getStatusCode() > 0, "Status code should be set");
        Assert.assertNotNull(result.getResponseBody(), "Response body should not be null");
        Assert.assertNotNull(result.getExecutionTime(), "Execution time should not be null");
        Assert.assertNotNull(result.getConvertedExecutionTime(), "Converted execution time should not be null");
        // isSuccessful is a primitive boolean, so it's always set
    }
}
