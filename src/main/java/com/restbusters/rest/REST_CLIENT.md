# REST Client Module

This module provides a robust HTTP client implementation built on OkHttp3 with support for authentication, interceptors, and request building.

## Architecture

```
rest/
├── client/
│   ├── RestClientHelper.java       # Main singleton for HTTP operations
│   ├── BasicAuthInterceptor.java   # Basic authentication
│   ├── BearerAuthInterceptor.java  # Bearer token authentication
│   ├── CustomHeaderInterceptor.java    # Single header interceptor
│   ├── CustomHeadersInterceptor.java   # Multiple headers interceptor (Map)
│   ├── LoggingInterceptor.java     # Request/response logging
│   ├── HttpMethods.java            # HTTP method enum
│   └── ConstantsErrors.java        # Error messages
├── model/
│   ├── HttpRequest.java            # Request DTO with Lombok @Builder
│   └── HttpRequestBuilder.java     # Legacy builder (deprecated)
└── payload/
    ├── FreeMarkerPayloadManager.java   # Template-based payload generation
    └── PayloadManager.java             # Deprecated, use FreeMarkerPayloadManager
```

## Usage

### Creating Clients

```java
RestClientHelper helper = RestClientHelper.getInstance();

// No authentication
OkHttpClient noAuthClient = helper.buildNoAuthClient();

// Basic authentication
OkHttpClient basicClient = helper.buildBasicAuthClient("username", "password");

// Basic auth with headers
Map<String, String> headers = Map.of("X-Api-Key", "my-key");
OkHttpClient basicWithHeaders = helper.buildBasicAuthClient("user", "pass", headers);

// Bearer token authentication
OkHttpClient bearerClient = helper.buildBearerClient("my-token");

// Client with custom headers (no auth)
Map<String, String> headers = Map.of(
    "X-Custom-Header", "value1",
    "X-Another-Header", "value2"
);
OkHttpClient headersClient = helper.buildClientWithHeaders(headers);

// With custom timeouts
OkHttpClient customTimeouts = helper.buildClientWithHeaders(headers, 30L, 60L, 60L);
```

### Making Requests

```java
// Using HttpRequest builder (Lombok)
HttpRequest request = HttpRequest.builder()
    .httpMethod("POST")
    .url("https://api.example.com/users")
    .requestBody("{\"name\": \"John\"}")
    .contentType("application/json")
    .build();

Response response = helper.executeRequest(client, request);

// Simple constructor
HttpRequest getRequest = new HttpRequest("GET", "https://api.example.com/users");
Response response = helper.executeRequest(client, getRequest);
```

### Adding Interceptors

```java
// Single header
OkHttpClient client = helper.registerInterceptor(
    existingClient,
    new CustomHeaderInterceptor("X-Api-Key", "my-key")
);

// Multiple headers
Map<String, String> headers = Map.of(
    "X-Api-Key", "key",
    "X-Request-Id", "uuid"
);
OkHttpClient client = helper.registerInterceptor(
    existingClient,
    new CustomHeadersInterceptor(headers)
);

// Logging interceptor
OkHttpClient client = helper.registerLoggerInterceptor(existingClient);
```

### OAuth2 Token Retrieval

```java
Map<String, String> params = Map.of(
    "grant_type", "client_credentials",
    "client_id", "my-client",
    "client_secret", "secret"
);
String token = helper.getOAuth2Token(
    "https://auth.example.com/oauth/token",
    params,
    "$.access_token"  // JsonPath to extract token
);
```

### URL Building

```java
// Add query parameters with proper encoding
Map<String, String> queryParams = Map.of(
    "search", "hello world",
    "filter", "name=test"
);
String url = helper.addQueryParams("https://api.example.com/search", queryParams);
// Result: https://api.example.com/search?search=hello%20world&filter=name%3Dtest
```

## Tests

### TestRestHelper

Location: `src/test/java/com/restbusters/rest/TestRestHelper.java`

| Test | Description |
|------|-------------|
| `testCreate2NewRestClient` | Verifies two different clients are not equal |
| `testDoGetRequest` | GET request returns 200 |
| `testDoPostRequestWithObject` | POST with body and content-type |
| `testDoPostRequestWithObjectNoUrl` | Empty URL throws RuntimeException |
| `testDoPostRequestWithObjectInvalidHttpMethod` | Invalid method throws RuntimeException |
| `testDoPostRequestWithObjectAndNullHttpMethod` | Null method throws RuntimeException |
| `testDoPostRequestWithObjectAndBlankHttpMethod` | Blank method throws RuntimeException |
| `testDoPutRequest` | PUT request returns 200 |
| `testDoPatchRequest` | PATCH request returns 200 |
| `testDoDeleteRequestWithRequestBody` | DELETE with body returns 200 |
| `testDoDeleteRequestWithNoBody` | DELETE without body returns 200 |
| `buildUrlWithQueryParams` | Query params are URL-encoded correctly |
| `get_oath2_token` | OAuth2 token extraction with JsonPath |
| `testCustomHeaderListener` | CustomHeaderInterceptor adds header to request |
| `testPerformanceUtil` | Performance testing utility integration |

### TestRequestBuilder

Location: `src/test/java/com/restbusters/rest/TestRequestBuilder.java`

| Test | Description |
|------|-------------|
| `testRequestBuilder` | HttpRequestBuilder creates valid HttpRequest |

### TestFreeMarkerPayloadManager

Location: `src/test/java/com/restbusters/rest/TestFreeMarkerPayloadManager.java`

| Test | Description |
|------|-------------|
| `getPayloadMetaData` | Template rendering with FreeMarker variables |

## Running Tests

```bash
# Run all REST module tests
./gradlew test --tests "com.restbusters.rest.*"

# Run specific test class
./gradlew test --tests "com.restbusters.rest.TestRestHelper"

# Run specific test method
./gradlew test --tests "com.restbusters.rest.TestRestHelper.testDoGetRequest"
```

## WireMock Integration

Tests use WireMock for mocking HTTP responses. Stubs are defined in:
`src/test/resources/wiremock/wiremock-stubs.json`

```java
@BeforeClass
public void setUp() throws IOException {
    String stubs = RBFileUtils.getFileOnClassPathAsString("wiremock/wiremock-stubs.json");
    this.wireMockManager = WireMockManager.getInstance(stubs);
}
```

## Interceptor Summary

| Interceptor | Purpose | Constructor |
|-------------|---------|-------------|
| `BasicAuthInterceptor` | Basic HTTP auth | `(username, password)` |
| `BearerAuthInterceptor` | Bearer token auth | `(token)` |
| `CustomHeaderInterceptor` | Single header | `(name, value)` |
| `CustomHeadersInterceptor` | Multiple headers | `(Map<String,String>)` |
| `LoggingInterceptor` | Log requests/responses | `()` |

## Deprecated

- `PayloadManager` - Use `FreeMarkerPayloadManager` instead
- `HttpRequestBuilder` - Use `HttpRequest.builder()` (Lombok)
- `addHeader()`, `addHeaders()`, `removeHeader()` - These methods don't work (result discarded)
