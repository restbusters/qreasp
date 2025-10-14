Here's an improved version of your markdown file with WebDriver usage included:

```markdown
# QREASP - Quality, Release and Automation Support Library

🧪 Comprehensive test automation framework (Java-based)

This framework provides a robust and flexible solution for automating both API and UI tests, written in Java. It features a state-driven WebDriver architecture for browser automation, templating for easy test case creation, and seamless integration with key development and project management tools using Gradle as its build system.

## ✨ Features

### API Testing
- **Templating**: Utilize templates for creating API test requests and assertions, simplifying test case development and promoting consistency
- **Swagger Integration**: Utilize Swagger definitions to generate or validate API requests and responses, streamlining API test creation and ensuring adherence to API specifications

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

## 📖 WebDriver Usage

### Basic Example

```java
import com.restbusters.webdriver.core.WebDriverHelper;
import com.restbusters.webdriver.enums.ActionType;
import com.restbusters.webdriver.enums.DriverType;
import com.restbusters.webdriver.enums.LocatorType;
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

Test results can be exported as JSON through TestNG listeners for integration with reporting tools.

## 🔧 Configuration

### Gradle Dependencies
```gradle
dependencies {
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.15.0'
    testImplementation 'io.github.bonigarcia:webdrivermanager:5.6.2'
    testImplementation 'org.testng:testng:7.8.0'
    // ... other dependencies
}
```

## 🤝 Contributing

Contributions are welcome! Please ensure:
- All tests pass before submitting a PR
- Code follows the existing style and patterns
- Documentation is updated for new features

## 📝 License

[Your License Here]

## 🆘 Support

For issues or questions, please create an issue in the repository or contact the development team.

---

**Note**: This framework uses a state-driven approach for WebDriver automation, which provides better test maintainability, debugging capabilities, and context management compared to traditional imperative WebDriver code.
```

This updated README includes:
- ✅ Clear WebDriver usage examples
- ✅ Architecture overview
- ✅ Available action types
- ✅ Multiple usage patterns (basic, fluent, forms, waits)
- ✅ Running tests with different configurations
- ✅ Better structure and organization
- ✅ Visual hierarchy with emojis
- ✅ Package structure visualization