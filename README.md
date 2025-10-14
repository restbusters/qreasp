# QREASP - Quality, Release and Automation Support Library

🧪 Comprehensive test automation framework (Java-based)

This framework provides a robust and flexible solution for automating both API and UI tests, written in Java. It features a state-driven WebDriver architecture for browser automation, REST API client utilities, templating for easy test case creation, and seamless integration with key development and project management tools using Gradle as its build system.

## ✨ Features

### API Testing
- **REST Client**: Modern HTTP client wrapper built on OkHttp3 with support for various authentication methods
- **Templating**: Utilize templates for creating API test requests and assertions, simplifying test case development and promoting consistency
- **Swagger Integration**: Utilize Swagger definitions to generate or validate API requests and responses, streamlining API test creation and ensuring adherence to API specifications
- **Request/Response Management**: Flexible HttpRequest model for declarative API testing

### UI Testing (WebDriver)
- **State-Driven Architecture**: Innovative state management approach for WebDriver actions - pass state objects instead of imperative commands
- **Comprehensive Action Support**: 40+ action types including clicks, navigation, waits, validations, form filling, and advanced mouse operations
- **Smart Retry Mechanisms**: Built-in retry strategies (immediate, linear backoff, exponential backoff) for flaky tests
- **Screenshot Management**: Automatic screenshots before/after actions or on failure for better debugging
- **Multi-Browser Support**: Chrome, Firefox, Edge, and Safari with automatic driver management
- **Fluent API**: Clean, readable test code with builder pattern support

### Integrations
- **Jira Integration**: Automate the creation and updating of Jira issues based on test results, improving issue tracking and collaboration
- **Stash Integration**: Version control integration for test scripts and related files
- **TeamCity Integration**: Seamless integration with TeamCity for continuous integration and automated test execution within your build pipelines

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- Gradle 7.x or higher
- Supported browsers (Chrome, Firefox, Edge, Safari) for UI testing

### Installation

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd qreasp
   ```

2. **Build the Project**
   ```bash
   ./gradlew clean build
   ```

3. **Configure Integrations**
   Configure the framework with your Jira, Stash, and TeamCity instances as needed (e.g., provide API credentials or configuration files)

## 📡 REST API Client Usage

The framework provides two HTTP client implementations: the legacy `RestClientHelper` (singleton-based) and the modern `HttpClientManager` (dependency injection friendly).

### Modern Approach: HttpClientManager (Recommended)

The new `HttpClientManager` class provides better testability, no singleton dependency, and cleaner API. It manages multiple OkHttpClient instances and can execute HttpRequest objects with any client configuration.

#### Basic GET Request

```java
import com.restbusters.rest.client.RestClient;
import com.restbusters.rest.model.HttpRequest;
import okhttp3.OkHttpClient;
import okhttp3.Response;

// Create RestClient instance
RestClient restClient = new RestClient();

// Create HTTP client
OkHttpClient client = restClient.createClient();

// Build request
HttpRequest httpRequest = new HttpRequest();
httpRequest.setHttpMethod("GET");
httpRequest.setUrl("https://api.example.com/users");

// Execute request
Response response = restClient.executeRequest(client, httpRequest);

if (response.isSuccessful()) {
    String responseBody = response.body().string();
    System.out.println(responseBody);
}
```

#### POST Request with JSON Body

```java
RestClient restClient = new RestClient();

// Create request with JSON body
HttpRequest httpRequest = new HttpRequest();
httpRequest.setHttpMethod("POST");
httpRequest.setUrl("https://api.example.com/users");
httpRequest.setRequestBody("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}");
httpRequest.setContentType("application/json");

// Execute with basic client
OkHttpClient client = restClient.createClient();
Response response = restClient.executeRequest(client, httpRequest);
```

#### Bearer Token Authentication

```java
RestClient restClient = new RestClient();

// Create client with Bearer token
String token = "your-bearer-token";
OkHttpClient client = restClient.createBearerClient(token);

// Build request
HttpRequest httpRequest = new HttpRequest();
httpRequest.setHttpMethod("GET");
httpRequest.setUrl("https://api.example.com/protected/data");

// Execute - Authorization header is automatically added
Response response = restClient.executeRequest(client, httpRequest);
```

#### Basic Authentication

```java
RestClient restClient = new RestClient();

// Create client with Basic Auth
OkHttpClient client = restClient.createBasicAuthClient("username", "password");

HttpRequest httpRequest = new HttpRequest();
httpRequest.setHttpMethod("GET");
httpRequest.setUrl("https://api.example.com/secure/endpoint");

Response response = restClient.executeRequest(client, httpRequest);
```

#### Custom Headers

```java
RestClient restClient = new RestClient();

// Add headers at client level
Map<String, String> clientHeaders = new HashMap<>();
clientHeaders.put("X-API-Key", "your-api-key");
clientHeaders.put("X-Client-Version", "1.0");

OkHttpClient client = restClient.createClientWithHeaders(clientHeaders);

// Or add headers at request level
HttpRequest httpRequest = new HttpRequest();
httpRequest.setHttpMethod("GET");
httpRequest.setUrl("https://api.example.com/data");

Map<String, String> requestHeaders = new HashMap<>();
requestHeaders.put("X-Request-ID", "req-12345");
httpRequest.setHeaders(requestHeaders);

Response response = restClient.executeRequest(client, httpRequest);
```

#### URL Parameters and Query Strings

```java
RestClient restClient = new RestClient();
OkHttpClient client = restClient.createClient();

HttpRequest httpRequest = new HttpRequest();
httpRequest.setHttpMethod("GET");
httpRequest.setUrl("https://api.example.com/users/{userId}/posts/{postId}");

// URL path parameters
Map<String, String> urlParams = new HashMap<>();
urlParams.put("userId", "123");
urlParams.put("postId", "456");
httpRequest.setUrlParams(urlParams);

// Query parameters
Map<String, String> queryParams = new HashMap<>();
queryParams.put("page", "1");
queryParams.put("size", "20");
httpRequest.setQueryParams(queryParams);

// Final URL: https://api.example.com/users/123/posts/456?page=1&size=20
Response response = restClient.executeRequest(client, httpRequest);
```

#### OAuth2 Token Retrieval

```java
RestClient restClient = new RestClient();

Map<String, String> formBody = new HashMap<>();
formBody.put("grant_type", "client_credentials");
formBody.put("client_id", "your-client-id");
formBody.put("client_secret", "your-client-secret");

String token = restClient.getOAuth2Token(
    "https://auth.example.com/oauth/token",
    formBody,
    "$.access_token"  // JsonPath to extract token
);

// Use the token
OkHttpClient client = restClient.createBearerClient(token);
```

#### Custom Timeouts

```java
// Create RestClient with custom timeouts (in seconds)
RestClient restClient = new RestClient(
    30L,  // connect timeout
    60L,  // read timeout
    60L   // write timeout
);

Map<String, String> headers = new HashMap<>();
headers.put("X-API-Key", "key");

// Or specify timeouts per request
OkHttpClient client = restClient.createClientWithHeaders(
    headers,
    10L,  // connect timeout
    30L,  // read timeout
    30L   // write timeout
);
```

### Legacy Approach: RestClientHelper

The legacy singleton-based approach is still supported for backward compatibility.

```java
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRequest;
import okhttp3.OkHttpClient;
import okhttp3.Response;

// Get singleton instance
RestClientHelper helper = RestClientHelper.getInstance();

// Create client
OkHttpClient client = helper.buildBearerClient("your-token");

// Build and execute request
HttpRequest httpRequest = new HttpRequest();
httpRequest.setHttpMethod("GET");
httpRequest.setUrl("https://api.example.com/data");

Response response = helper.executeRequest(client, httpRequest);
```

### Migration Guide: Old to New

| Old (RestClientHelper) | New (RestClient) |
|------------------------|------------------|
| `RestClientHelper.getInstance()` | `new RestClient()` |
| `buildBearerClient(token)` | `createBearerClient(token)` |
| `buildBasicAuthClient(user, pass)` | `createBasicAuthClient(user, pass)` |
| `buildNoAuthClient()` | `createClient()` |
| `buildClientWithHeaders(headers)` | `createClientWithHeaders(headers)` |
| `executeRequest(client, request)` | `executeRequest(client, request)` (same) |

**Benefits of migrating:**
- ✅ No singleton dependency - easier testing with dependency injection
- ✅ Configurable timeouts per instance
- ✅ Better resource management
- ✅ Cleaner, more predictable API
- ✅ Proper exception handling with `IllegalArgumentException`

## 📖 WebDriver Usage

### Basic Example

```java
import com.restbusters.webdriver.core.WebDriverHelper;
import com.restbusters.webdriver.enums.ActionType;
import com.restbusters.webdriver.enums.DriverType;
import com.restbusters.webdriver.models.WebDriverState;
import com.restbusters.webdriver.models.ExecutionResult;
import org.openqa.selenium.WebDriver;

public class LoginTest {
    
    @Test
    public void testLogin() {
        // Get WebDriver helper instance
        WebDriverHelper helper = WebDriverHelper.getInstance();
        
        // Create browser
        WebDriver driver = helper.createWebDriver(DriverType.CHROME);
        
        try {
            // Navigate to login page
            WebDriverState navigateState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://example.com/login")
                .build();
            
            helper.executeAction(navigateState);
            
            // Enter username
            WebDriverState usernameState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.SEND_KEYS)
                .locator(Map.of("locatorType", "ID", "locatorValue", "username"))
                .value("testuser")
                .build();
            
            helper.executeAction(usernameState);
            
            // Enter password
            WebDriverState passwordState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.SEND_KEYS)
                .locator(Map.of("locatorType", "ID", "locatorValue", "password"))
                .value("password123")
                .build();
            
            helper.executeAction(passwordState);
            
            // Click login button
            WebDriverState clickState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.CLICK)
                .locator(Map.of("locatorType", "ID", "locatorValue", "login-btn"))
                .takeScreenshotAfter(true)
                .build();
            
            ExecutionResult result = helper.executeAction(clickState);
            
            // Validate login success
            assert result.isSuccessful();
            
        } finally {
            helper.quitWebDriver(driver);
        }
    }
}
```

### Fluent API Example

```java
// Using fluent API for cleaner code
WebDriverState clickState = WebDriverState.create(driver)
    .withAction(ActionType.CLICK)
    .withLocator(LocatorType.ID, "submit-button")
    .withTimeout(15)
    .withRetry(RetryStrategy.EXPONENTIAL_BACKOFF)
    .withScreenshots(true, true, true);

ExecutionResult result = helper.executeAction(clickState);
```

### Form Filling Example

```java
import com.restbusters.webdriver.models.FormData;

// Fill multiple form fields at once
WebDriverState formState = WebDriverState.builder()
    .webDriver(driver)
    .formData(new FormData(LocatorType.ID, "firstName", "John", "First Name"))
    .formData(new FormData(LocatorType.ID, "lastName", "Doe", "Last Name"))
    .formData(new FormData(LocatorType.ID, "email", "john@example.com", "Email"))
    .build();

ExecutionResult result = helper.fillForm(formState);
```

### Wait and Retry Example

```java
// Wait for element with retry strategy
WebDriverState waitState = WebDriverState.builder()
    .webDriver(driver)
    .actionType(ActionType.WAIT_FOR_ELEMENT_CLICKABLE)
    .locator(Map.of("locatorType", "XPATH", "locatorValue", "//button[@class='dynamic-btn']"))
    .timeoutInSeconds(15)
    .retryStrategy(RetryStrategy.EXPONENTIAL_BACKOFF)
    .takeScreenshotOnFailure(true)
    .build();

helper.executeAction(waitState);
```

### Available Action Types

- **Navigation**: NAVIGATE_TO, NAVIGATE_BACK, NAVIGATE_FORWARD, REFRESH
- **Element Interaction**: CLICK, DOUBLE_CLICK, RIGHT_CLICK, SEND_KEYS, CLEAR, SUBMIT
- **Data Retrieval**: GET_TEXT, GET_ATTRIBUTE, GET_VALUE
- **Selection**: SELECT_FROM_DROPDOWN, SELECT_BY_INDEX, SELECT_BY_VALUE
- **Wait Operations**: WAIT_FOR_ELEMENT, WAIT_FOR_ELEMENT_CLICKABLE, WAIT_FOR_ELEMENT_VISIBLE
- **Mouse Actions**: HOVER, DRAG_AND_DROP, CLICK_AND_HOLD, RELEASE
- **Scrolling**: SCROLL_TO_ELEMENT, SCROLL_UP, SCROLL_DOWN, SCROLL_TO_TOP, SCROLL_TO_BOTTOM
- **Window Management**: MAXIMIZE_WINDOW, MINIMIZE_WINDOW, SWITCH_TO_WINDOW, CLOSE_WINDOW
- **Frame Operations**: SWITCH_TO_FRAME, SWITCH_TO_DEFAULT_CONTENT
- **Validations**: VALIDATE_TITLE, VALIDATE_TEXT, VALIDATE_ELEMENT_PRESENT, VALIDATE_ELEMENT_VISIBLE
- **Utilities**: TAKE_SCREENSHOT, EXECUTE_JAVASCRIPT, UPLOAD_FILE, FILL_FORM

## 🏗️ Framework Architecture

### REST Client Package Structure
```
com.restbusters.rest/
├── client/
│   ├── RestClient.java              # Modern REST client (recommended)
│   ├── RestClientHelper.java        # Legacy singleton client
│   ├── BasicAuthInterceptor.java    # Basic authentication
│   ├── BearerAuthInterceptor.java   # Bearer token authentication
│   └── LoggingInterceptor.java      # Request/response logging
├── model/
│   ├── HttpRequest.java             # HTTP request metadata
│   └── HttpMethods.java             # HTTP method enums
└── exceptions/
    └── ConstantsErrors.java         # Error constants
```

### WebDriver Package Structure
```
com.restbusters.webdriver/
├── core/
│   ├── WebDriverHelper.java      # Main entry point (Singleton)
│   ├── ActionExecutor.java       # Executes actions based on state
│   └── DriverManager.java        # Browser creation/management
├── models/
│   ├── WebDriverState.java       # State object with all test context
│   ├── FormData.java             # Form field data
│   ├── ExecutionResult.java      # Action execution results
│   └── ValidationResult.java     # Validation results
├── enums/
│   ├── ActionType.java           # All supported actions
│   ├── LocatorType.java          # Locator strategies
│   ├── DriverType.java           # Browser types
│   └── RetryStrategy.java        # Retry mechanisms
├── utils/
│   ├── LocatorUtils.java         # Locator conversions
│   ├── WaitUtils.java            # Wait operations
│   └── ScreenshotUtils.java      # Screenshot handling
└── exceptions/
    ├── WebDriverStateException.java
    └── StateExecutionException.java
```

## 🧪 Running Tests

### Run All Tests
```bash
./gradlew clean test
```

### Run Specific Test Suite
```bash
./gradlew test --tests "com.yourpackage.LoginTests"
```

### Run Specific Test Class
```bash
./gradlew test --tests "com.restbusters.rest.client.RestClientTest"
```

### Run with Specific Browser
```bash
./gradlew test -Dbrowser=chrome
./gradlew test -Dbrowser=firefox
```

### Run in Headless Mode
```bash
./gradlew test -Dheadless=true
```

## 📊 Test Reporting

Test results can be exported as JSON through TestNG listeners for integration with reporting tools. Reports are generated at:
```
build/reports/tests/test/index.html
```

## 🔧 Configuration

### Gradle Dependencies
```gradle
dependencies {
    // REST Client
    api 'com.squareup.okhttp3:okhttp:5.0.0-alpha.14'
    implementation 'com.jayway.jsonpath:json-path:2.8.0'
    implementation 'org.apache.commons:commons-collections4:4.4'
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    
    // WebDriver
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.15.0'
    testImplementation 'io.github.bonigarcia:webdrivermanager:5.6.2'
    
    // Testing
    testImplementation 'org.testng:testng:7.8.0'
    testImplementation 'com.squareup.okhttp3:mockwebserver:5.0.0-alpha.14'
    
    // Logging
    implementation 'org.slf4j:slf4j-api:2.0.9'
    implementation 'ch.qos.logback:logback-classic:1.4.11'
}

test {
    useTestNG()
}
```

## 🤝 Contributing

Contributions are welcome! Please ensure:
- All tests pass before submitting a PR
- Code follows the existing style and patterns
- Documentation is updated for new features
- New features include comprehensive tests

## 📝 License

[Your License Here]

## 🆘 Support

For issues or questions, please create an issue in the repository or contact the development team.

---

**Note**: This framework uses modern design patterns including:
- State-driven approach for WebDriver automation (better maintainability and debugging)
- Dependency injection friendly REST client (easier testing and composition)
- Builder pattern for readable test code
- Comprehensive error handling and logging