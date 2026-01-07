# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

QREASP (Quality, Release and Automation Support) is a Java-based API and UI test automation framework. It provides:
- REST API testing with FreeMarker templating
- Web UI automation via Selenium WebDriver with a fluent BrowserContext API
- Integrations for Jira, Stash/Bitbucket, TeamCity, and Swagger/OpenAPI

## Build Commands

```bash
# Build and run tests
./gradlew clean build

# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests BrowserContextTest

# Run tests matching pattern
./gradlew test --tests "BrowserContext*Test"

# Install to local Maven repository
./gradlew publishToMavenLocal
```

## Architecture

**Source:** `src/main/java/com/restbusters/`

### Core Modules

| Module | Purpose | Key Classes |
|--------|---------|-------------|
| `rest/` | HTTP client with auth interceptors | `RestClientHelper` (singleton), `HttpRequestBuilder`, `PayloadManager` |
| `webdriver/` | Browser automation with fluent API | `BrowserContext` (facade), `ActionExecutor` (state-driven engine), `DriverManager` |
| `data/` | Template and data management | `Context` (thread-safe storage), `TemplateManager`, `GenericDataProvider` |
| `http/` | HTTP execution utilities | `HttpRequestHelper`, `HttpResultAnalyzer`, `HttpExecutionResult` |
| `integraton/` | External integrations | `SwaggerManager`, `JiraHelper`, `StashRestClient`, `TeamCityClient` |
| `util/` | Common utilities | `GenericUtils`, `RBFileUtils`, `WireMockManager` |

Note: The `integraton` package has a typo (missing 'i') - this is intentional legacy naming.

### Key Design Patterns

- **Singleton:** `RestClientHelper`, `SwaggerManager`, `PayloadManager`, `GlobalResourceManager`
- **Builder:** `HttpRequestBuilder`, `WebDriverState.builder()`
- **Fluent API:** `BrowserContext` for readable test code
- **State-Driven:** `WebDriverState` + `ActionExecutor` separates action definition from execution
- **Strategy:** `RetryStrategy` enum (NO_RETRY, IMMEDIATE_RETRY, LINEAR_BACKOFF, EXPONENTIAL_BACKOFF)

### WebDriver Architecture

The WebDriver module uses a layered approach:
1. `BrowserContext` - High-level fluent facade for tests
2. `ActionExecutor` - Executes actions based on `WebDriverState`
3. `DriverManager` - Manages Chrome/Firefox driver lifecycle
4. `ActionType` enum - Defines actions (NAVIGATE, CLICK, SEND_KEYS, SELECT, etc.)

## Technology Stack

- **Java 11+** (Java 8 compatible)
- **Gradle** build system with Lombok plugin
- **TestNG** for testing
- **OkHttp3** for HTTP client
- **Selenium 4.x** with WebDriverManager for browser automation
- **FreeMarker** for templating
- **Jackson** for JSON processing
- **WireMock** for mock servers in tests

## Test Resources

- Payload templates: `src/test/resources/payload/`
- Swagger specs: `src/test/resources/swagger/`
- WireMock mocks: `src/test/resources/wiremock/`
