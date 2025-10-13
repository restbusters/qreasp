package com.restbusters.rest.client;

import com.restbusters.rest.model.HttpRequest;
import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * TestNG test class for RestClient
 */
public class RestClientTest {

    private RestClient restClient;
    private MockWebServer mockWebServer;

    @BeforeMethod
    public void setUp() throws IOException {
        restClient = new RestClient();
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterMethod
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private String getBaseUrl() {
        return mockWebServer.url("/").toString();
    }

    // ==================== Client Creation Tests ====================

    @Test(description = "Should create basic client with default timeouts")
    public void testCreateClient() {
        OkHttpClient client = restClient.createClient();

        assertNotNull(client);
        assertEquals(client.connectTimeoutMillis() / 1000, 180);
        assertEquals(client.readTimeoutMillis() / 1000, 180);
        assertEquals(client.writeTimeoutMillis() / 1000, 180);
    }

    @Test(description = "Should create client with custom timeouts")
    public void testCreateClientWithCustomTimeouts() {
        RestClient customRestClient = new RestClient(30L, 60L, 90L);

        assertEquals(customRestClient.getDefaultConnectTimeout(), 30L);
        assertEquals(customRestClient.getDefaultReadTimeout(), 60L);
        assertEquals(customRestClient.getDefaultWriteTimeout(), 90L);
    }

    @Test(description = "Should create client without logging")
    public void testCreateClientNoLogging() {
        OkHttpClient client = restClient.createClientNoLogging();

        assertNotNull(client);
        assertTrue(client.interceptors().isEmpty());
    }

    @Test(description = "Should create client with custom headers")
    public void testCreateClientWithHeaders() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "test-value");
        headers.put("X-API-Key", "secret-key");

        OkHttpClient client = restClient.createClientWithHeaders(headers);

        assertNotNull(client);
        assertFalse(client.interceptors().isEmpty());

        // Test that headers are applied
        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        Request request = new Request.Builder()
                .url(getBaseUrl())
                .build();

        client.newCall(request).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getHeader("X-Custom-Header"), "test-value");
        assertEquals(recordedRequest.getHeader("X-API-Key"), "secret-key");
    }

    @Test(description = "Should create client with headers and custom timeouts")
    public void testCreateClientWithHeadersAndTimeouts() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "test");

        OkHttpClient client = restClient.createClientWithHeaders(headers, 10L, 20L, 30L);

        assertNotNull(client);
        assertEquals(client.connectTimeoutMillis() / 1000, 10);
        assertEquals(client.readTimeoutMillis() / 1000, 20);
        assertEquals(client.writeTimeoutMillis() / 1000, 30);
    }

    @Test(description = "Should create basic auth client")
    public void testCreateBasicAuthClient() throws Exception {
        OkHttpClient client = restClient.createBasicAuthClient("username", "password");

        assertNotNull(client);
        assertFalse(client.interceptors().isEmpty());

        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        Request request = new Request.Builder()
                .url(getBaseUrl())
                .build();

        client.newCall(request).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String authHeader = recordedRequest.getHeader("Authorization");
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Basic "));
    }

    @Test(description = "Should create bearer auth client")
    public void testCreateBearerClient() throws Exception {
        String token = "test-bearer-token";
        OkHttpClient client = restClient.createBearerClient(token);

        assertNotNull(client);
        assertFalse(client.interceptors().isEmpty());

        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        Request request = new Request.Builder()
                .url(getBaseUrl())
                .build();

        client.newCall(request).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String authHeader = recordedRequest.getHeader("Authorization");
        assertEquals(authHeader, "Bearer " + token);
    }

    @Test(description = "Should create bearer auth client with headers")
    public void testCreateBearerClientWithHeaders() throws Exception {
        String token = "test-token";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Request-ID", "12345");

        OkHttpClient client = restClient.createBearerClient(token, headers);

        assertNotNull(client);

        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        Request request = new Request.Builder()
                .url(getBaseUrl())
                .build();

        client.newCall(request).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getHeader("Authorization"), "Bearer " + token);
        assertEquals(recordedRequest.getHeader("X-Request-ID"), "12345");
    }

    @Test(description = "Should create client with custom interceptor")
    public void testCreateClientWithInterceptor() throws Exception {
        Interceptor customInterceptor = chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("X-Custom-Interceptor", "true")
                    .build();
            return chain.proceed(request);
        };

        OkHttpClient client = restClient.createClientWithInterceptor(customInterceptor);

        assertNotNull(client);

        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        Request request = new Request.Builder()
                .url(getBaseUrl())
                .build();

        client.newCall(request).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getHeader("X-Custom-Interceptor"), "true");
    }

    @Test(description = "Should add interceptor to existing client")
    public void testAddInterceptor() throws Exception {
        OkHttpClient baseClient = restClient.createClient();

        Interceptor customInterceptor = chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("X-Added-Interceptor", "added")
                    .build();
            return chain.proceed(request);
        };

        OkHttpClient clientWithInterceptor = restClient.addInterceptor(baseClient, customInterceptor);

        assertNotNull(clientWithInterceptor);
        assertNotSame(clientWithInterceptor, baseClient);

        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        Request request = new Request.Builder()
                .url(getBaseUrl())
                .build();

        clientWithInterceptor.newCall(request).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getHeader("X-Added-Interceptor"), "added");
    }

    // ==================== Request Execution Tests ====================

    @Test(description = "Should execute GET request successfully")
    public void testExecuteGetRequest() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"status\":\"success\"}")
                .setHeader("Content-Type", "application/json"));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("GET");
        httpRequest.setUrl(getBaseUrl() + "api/users");

        OkHttpClient client = restClient.createClient();
        Response response = restClient.executeRequest(client, httpRequest);

        assertTrue(response.isSuccessful());
        assertEquals(response.code(), 200);
        assertNotNull(response.body());
        assertTrue(response.body().string().contains("success"));
    }

    @Test(description = "Should execute POST request with JSON body")
    public void testExecutePostRequest() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":1,\"created\":true}")
                .setHeader("Content-Type", "application/json"));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("POST");
        httpRequest.setUrl(getBaseUrl() + "api/users");
        httpRequest.setRequestBody("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}");
        httpRequest.setContentType("application/json");

        OkHttpClient client = restClient.createClient();
        Response response = restClient.executeRequest(client, httpRequest);

        assertTrue(response.isSuccessful());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getMethod(), "POST");
        assertTrue(recordedRequest.getHeader("Content-Type").contains("application/json"));
        assertTrue(recordedRequest.getBody().readUtf8().contains("John Doe"));
    }

    @Test(description = "Should execute PUT request")
    public void testExecutePutRequest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{\"updated\":true}"));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("PUT");
        httpRequest.setUrl(getBaseUrl() + "api/users/1");
        httpRequest.setRequestBody("{\"name\":\"Jane Doe\"}");

        OkHttpClient client = restClient.createClient();
        Response response = restClient.executeRequest(client, httpRequest);

        assertTrue(response.isSuccessful());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getMethod(), "PUT");
    }

    @Test(description = "Should execute PATCH request")
    public void testExecutePatchRequest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{\"patched\":true}"));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("PATCH");
        httpRequest.setUrl(getBaseUrl() + "api/users/1");
        httpRequest.setRequestBody("{\"email\":\"newemail@example.com\"}");

        OkHttpClient client = restClient.createClient();
        Response response = restClient.executeRequest(client, httpRequest);

        assertTrue(response.isSuccessful());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getMethod(), "PATCH");
    }

    @Test(description = "Should execute DELETE request without body")
    public void testExecuteDeleteRequestNoBody() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("DELETE");
        httpRequest.setUrl(getBaseUrl() + "api/users/1");

        OkHttpClient client = restClient.createClient();
        Response response = restClient.executeRequest(client, httpRequest);

        assertEquals(response.code(), 204);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getMethod(), "DELETE");
    }

    @Test(description = "Should execute DELETE request with body")
    public void testExecuteDeleteRequestWithBody() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("DELETE");
        httpRequest.setUrl(getBaseUrl() + "api/users");
        httpRequest.setRequestBody("{\"ids\":[1,2,3]}");

        OkHttpClient client = restClient.createClient();
        Response response = restClient.executeRequest(client, httpRequest);

        assertTrue(response.isSuccessful());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getMethod(), "DELETE");
        assertTrue(recordedRequest.getBody().readUtf8().contains("ids"));
    }

    @Test(description = "Should substitute URL parameters")
    public void testUrlParameterSubstitution() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("GET");
        httpRequest.setUrl(getBaseUrl() + "api/users/{userId}/posts/{postId}");

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("userId", "123");
        urlParams.put("postId", "456");
        httpRequest.setUrlParams(urlParams);

        OkHttpClient client = restClient.createClient();
        Response response = restClient.executeRequest(client, httpRequest);

        assertTrue(response.isSuccessful());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertTrue(recordedRequest.getPath().contains("/users/123/posts/456"));
    }

    @Test(description = "Should add query parameters")
    public void testQueryParameters() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("GET");
        httpRequest.setUrl(getBaseUrl() + "api/users");

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("page", "1");
        queryParams.put("size", "20");
        queryParams.put("sort", "name");
        httpRequest.setQueryParams(queryParams);

        OkHttpClient client = restClient.createClient();
        Response response = restClient.executeRequest(client, httpRequest);

        assertTrue(response.isSuccessful());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String path = recordedRequest.getPath();
        assertTrue(path.contains("page=1"));
        assertTrue(path.contains("size=20"));
        assertTrue(path.contains("sort=name"));
    }

    @Test(description = "Should add custom headers to request")
    public void testRequestHeaders() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("GET");
        httpRequest.setUrl(getBaseUrl() + "api/data");

        Map<String, String> headers = new HashMap<>();
        headers.put("X-API-Key", "secret-key");
        headers.put("X-Request-ID", "req-12345");
        httpRequest.setHeaders(headers);

        OkHttpClient client = restClient.createClient();
        Response response = restClient.executeRequest(client, httpRequest);

        assertTrue(response.isSuccessful());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getHeader("X-API-Key"), "secret-key");
        assertEquals(recordedRequest.getHeader("X-Request-ID"), "req-12345");
    }

    @Test(description = "Should auto-detect JSON content type")
    public void testAutoDetectJsonContentType() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("POST");
        httpRequest.setUrl(getBaseUrl() + "api/data");
        httpRequest.setRequestBody("{\"key\":\"value\"}");
        // Not setting contentType - should auto-detect

        OkHttpClient client = restClient.createClient();
        Response response = restClient.executeRequest(client, httpRequest);

        assertTrue(response.isSuccessful());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String contentType = recordedRequest.getHeader("Content-Type");
        assertNotNull(contentType);
        assertTrue(contentType.contains("application/json"));
    }

    // ==================== Validation Tests ====================

    @Test(description = "Should throw exception when URL is blank",
            expectedExceptions = IllegalArgumentException.class)
    public void testValidationBlankUrl() throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("GET");
        httpRequest.setUrl("");

        OkHttpClient client = restClient.createClient();
        restClient.executeRequest(client, httpRequest);
    }

    @Test(description = "Should throw exception when HTTP method is blank",
            expectedExceptions = IllegalArgumentException.class)
    public void testValidationBlankHttpMethod() throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setUrl(getBaseUrl());
        httpRequest.setHttpMethod("");

        OkHttpClient client = restClient.createClient();
        restClient.executeRequest(client, httpRequest);
    }

    @Test(description = "Should throw exception when HTTP method is invalid",
            expectedExceptions = IllegalArgumentException.class)
    public void testValidationInvalidHttpMethod() throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setUrl(getBaseUrl());
        httpRequest.setHttpMethod("INVALID");

        OkHttpClient client = restClient.createClient();
        restClient.executeRequest(client, httpRequest);
    }

    // ==================== OAuth2 Tests ====================

    @Test(description = "Should retrieve OAuth2 token successfully")
    public void testGetOAuth2Token() throws Exception {
        String tokenResponse = "{\"access_token\":\"test-token-123\",\"token_type\":\"Bearer\"}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(tokenResponse)
                .setHeader("Content-Type", "application/json"));

        Map<String, String> formBody = new HashMap<>();
        formBody.put("grant_type", "client_credentials");
        formBody.put("client_id", "test-client");
        formBody.put("client_secret", "test-secret");

        String token = restClient.getOAuth2Token(
                getBaseUrl() + "oauth/token",
                formBody,
                "$.access_token"
        );

        assertEquals(token, "test-token-123");

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getMethod(), "POST");
        assertTrue(recordedRequest.getBody().readUtf8().contains("grant_type=client_credentials"));
    }

    @Test(description = "Should return null when OAuth2 request fails")
    public void testGetOAuth2TokenFailure() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        Map<String, String> formBody = new HashMap<>();
        formBody.put("grant_type", "client_credentials");

        String token = restClient.getOAuth2Token(
                getBaseUrl() + "oauth/token",
                formBody,
                "$.access_token"
        );

        assertNull(token);
    }

    @Test(description = "Should return null when form body is empty")
    public void testGetOAuth2TokenEmptyFormBody() {
        String token = restClient.getOAuth2Token(
                getBaseUrl() + "oauth/token",
                new HashMap<>(),
                "$.access_token"
        );

        assertNull(token);
    }

    // ==================== Integration Tests ====================

    @Test(description = "Should handle complete request lifecycle")
    public void testCompleteRequestLifecycle() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}")
                .setHeader("Content-Type", "application/json"));

        // Create client with bearer auth
        String token = "secure-token-123";
        Map<String, String> clientHeaders = new HashMap<>();
        clientHeaders.put("X-Client-Version", "1.0");
        OkHttpClient client = restClient.createBearerClient(token, clientHeaders);

        // Build request
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod("POST");
        httpRequest.setUrl(getBaseUrl() + "api/users/{userId}");

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("userId", "123");
        httpRequest.setUrlParams(urlParams);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("include", "profile");
        httpRequest.setQueryParams(queryParams);

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Request-ID", "req-456");
        httpRequest.setHeaders(requestHeaders);

        httpRequest.setRequestBody("{\"name\":\"Updated Name\"}");
        httpRequest.setContentType("application/json");

        // Execute request
        Response response = restClient.executeRequest(client, httpRequest);

        // Verify response
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        String responseBody = response.body().string();
        assertTrue(responseBody.contains("Test User"));

        // Verify request details
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(recordedRequest.getMethod(), "POST");
        assertTrue(recordedRequest.getPath().contains("/users/123"));
        assertTrue(recordedRequest.getPath().contains("include=profile"));
        assertEquals(recordedRequest.getHeader("Authorization"), "Bearer " + token);
        assertEquals(recordedRequest.getHeader("X-Client-Version"), "1.0");
        assertEquals(recordedRequest.getHeader("X-Request-ID"), "req-456");
        assertTrue(recordedRequest.getHeader("Content-Type").contains("application/json"));
    }
}