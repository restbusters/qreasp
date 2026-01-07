# Changelog: feature/rest-refactor

## Summary
Refactored `src/main/java/com/restbusters/rest` module and related utilities to fix bugs, improve thread safety, add performance metrics, and expand test coverage.

---

## Bug Fixes

### Critical Bugs Fixed

| File | Line | Issue | Fix |
|------|------|-------|-----|
| `RestClientHelper.java` | 44-51 | Timeout configuration discarded (result of `newBuilder()` not assigned) | Assign built client back to `sharedOkHttpClient` |
| `FreeMarkerPayloadManager.java` | 74-76 | Thread-safety: shared `Configuration` modified by multiple threads | Create local `Configuration` per call |
| `FreeMarkerPayloadManager.java` | 70-78 | Missing null checks for `relativePath` and template content | Added null checks with error logging |
| `PayloadManager.java` | 181 | `buildFilter()` criteria not assigned back | Fixed: `criteria = criteria.and(...)` |
| `FreeMarkerPayloadManager.java` | 146 | Same `buildFilter()` bug | Fixed: `criteria = criteria.and(...)` |
| `RBFileUtils.java` | 51 | NPE when file not found (`getResourceAsStream` returns null) | Added null check before `IOUtils.toString()` |
| `RBFileUtils.java` | 66 | Same NPE bug in byte array method | Added null check |
| `PerformanceTestUtil.java` | 50 | Thread-safety: `ArrayList` accessed by multiple threads | Changed to `Collections.synchronizedList()` |
| `WireMockManager.java` | 44-48 | Race condition in singleton when server stopped | Added `isRunning()` check and `waitForWireMockReady()` |

### WebDriver Tests - Browser Cleanup
- Changed all WebDriver tests to use `CHROME_HEADLESS` mode
- Added `alwaysRun=true` to `@AfterClass`/`@AfterSuite` methods
- Added try-catch in teardown to ensure cleanup

---

## New Features

### Performance Metrics (P50, P95, P99)

**Files Modified:**
- `PerfExecResult.java` - Added `p50`, `p95`, `p99` fields
- `HttpResultAnalyzer.java` - Added `calculatePercentile()` method with linear interpolation

**Usage:**
```java
PerfExecResult result = HttpResultAnalyzer.analyzePerformanceResults(results);
result.getP50();  // Median latency
result.getP95();  // 95th percentile
result.getP99();  // 99th percentile
```

**Output Example:**
```
Performance Results:
  Total Requests: 100
  Success Rate: 99.0%
  Min Time: 10 ms
  Average Time: 150.5 ms
  P50 (Median): 120 ms
  P95: 450 ms
  P99: 890 ms
  Max Time: 1200 ms
```

### CustomHeadersInterceptor
New OkHttp interceptor that accepts a `Map<String, String>` of headers.

**File:** `src/main/java/com/restbusters/rest/client/CustomHeadersInterceptor.java`

**Usage:**
```java
Map<String, String> headers = Map.of(
    "X-Api-Key", "key",
    "X-Request-Id", "uuid"
);
OkHttpClient client = helper.registerInterceptor(
    existingClient,
    new CustomHeadersInterceptor(headers)
);
```

---

## Code Quality Improvements

### RestClientHelper.java
- Changed `Object` parameters to `Interceptor` for type safety
- Deprecated broken methods: `addHeader()`, `addHeaders()`, `removeHeader()`
- Converted anonymous classes to lambdas
- Replaced `e.printStackTrace()` with proper `logger.error()` calls

### FreeMarkerPayloadManager.java
- Removed duplicate initialization in constructor
- Removed unused instance fields (`templateLoader`, `cfg`, `stringWriter`)
- Improved error logging

### PayloadManager.java
- Marked entire class as `@Deprecated` (use `FreeMarkerPayloadManager` instead)
- Fixed bugs and improved logging

### Model Classes - Lombok @Builder
Added `@Builder` annotation to:
- `HttpRequest.java`
- `PayloadTemplate.java`
- `Parameter.java`
- `Payload.java`
- Deprecated `HttpRequestBuilder.java` (kept for backward compatibility)

---

## New Tests

### HttpRequestHelperTest (8 tests)
**File:** `src/test/java/com/restbusters/http/helper/HttpRequestHelperTest.java`

| Test | Description |
|------|-------------|
| `testExecuteHttpRequest_GetSuccess` | GET request with successful response |
| `testExecuteHttpRequest_PostWithBody` | POST request with JSON body |
| `testExecuteHttpRequest_OAuthEndpoint` | OAuth token endpoint |
| `testExecuteHttpRequest_ExecutionTimeCapture` | Verifies execution time tracking |
| `testExecuteHttpRequest_InvalidUrl` | Error handling for invalid URLs |
| `testExecuteHttpRequest_GetCommits` | GET request for specific commit |
| `testExecuteHttpRequest_NoBody` | GET request without body |
| `testHttpExecutionResult_AllFieldsPopulated` | Verifies all result fields |

### HttpResultAnalyzerTest (17 tests)
**File:** `src/test/java/com/restbusters/http/helper/HttpResultAnalyzerTest.java`

- Percentile calculation tests (P0, P50, P95, P99, P100)
- Performance analysis tests (success/failure, empty lists, null handling)
- Error deduplication verification

### PerformanceTestUtilTest (12 tests)
**File:** `src/test/java/com/restbusters/util/performance/PerformanceTestUtilTest.java`

- 2 WireMock integration tests for `runPerformanceTest()`
- 10 unit tests for `getThreadCountForIteration()`

### TestFreeMarkerPayloadManager (36 tests)
**File:** `src/test/java/com/restbusters/rest/TestFreeMarkerPayloadManager.java`

- `testGetPayload_*` (6 tests + concurrent invocations)
- `testValidateType_*` (3 tests)
- `testFetchTemplate_*` (2 tests)
- `testGetInstance_*` (1 test)
- Thread safety test with `threadPoolSize=10, invocationCount=20`

---

## Documentation

### New Files
- `CLAUDE.md` - Project documentation for AI assistants
- `src/main/java/com/restbusters/rest/REST_CLIENT.md` - REST client module documentation

---

## Files Modified

### Main Source
- `src/main/java/com/restbusters/rest/client/RestClientHelper.java`
- `src/main/java/com/restbusters/rest/client/CustomHeadersInterceptor.java` (NEW)
- `src/main/java/com/restbusters/rest/model/HttpRequest.java`
- `src/main/java/com/restbusters/rest/model/HttpRequestBuilder.java`
- `src/main/java/com/restbusters/rest/payload/FreeMarkerPayloadManager.java`
- `src/main/java/com/restbusters/rest/payload/PayloadManager.java`
- `src/main/java/com/restbusters/rest/payload/model/PayloadTemplate.java`
- `src/main/java/com/restbusters/rest/payload/model/Parameter.java`
- `src/main/java/com/restbusters/rest/payload/model/Payload.java`
- `src/main/java/com/restbusters/http/helper/HttpResultAnalyzer.java`
- `src/main/java/com/restbusters/http/helper/model/PerfExecResult.java`
- `src/main/java/com/restbusters/util/common/RBFileUtils.java`
- `src/main/java/com/restbusters/util/performance/PerformanceTestUtil.java`
- `src/main/java/com/restbusters/util/wiremock/WireMockManager.java`

### Test Source
- `src/test/java/com/restbusters/http/helper/HttpRequestHelperTest.java` (NEW)
- `src/test/java/com/restbusters/http/helper/HttpResultAnalyzerTest.java` (NEW)
- `src/test/java/com/restbusters/util/performance/PerformanceTestUtilTest.java` (NEW)
- `src/test/java/com/restbusters/rest/TestFreeMarkerPayloadManager.java`
- `src/test/java/com/restbusters/webdriver/BrowserContextTest.java`
- `src/test/java/com/restbusters/webdriver/BrowserContextSimpleTest.java`
- `src/test/java/com/restbusters/webdriver/BrowserContextAdditionalTest.java`
- `src/test/java/com/restbusters/webdriver/WebDriverTest.java`

---

## Backward Compatibility

- `PayloadManager` deprecated but not removed
- `HttpRequestBuilder` preserved as wrapper for Lombok builder
- Model `with*()` methods preserved alongside `@Builder`
