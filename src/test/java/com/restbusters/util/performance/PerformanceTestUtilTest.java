package com.restbusters.util.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.rest.client.HttpMethods;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRequest;
import com.restbusters.util.common.RBFileUtils;
import com.restbusters.util.wiremock.WireMockManager;
import okhttp3.OkHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for PerformanceTestUtil utility class.
 */
public class PerformanceTestUtilTest {

    private WireMockManager wireMockManager;
    private OkHttpClient client;
    private ObjectMapper objectMapper;
    private static final String WIREMOCK_BASE_URL = "http://localhost:8090";

    @BeforeClass
    public void setUp() throws IOException {
        String wireMockStubs = RBFileUtils.getFileOnClassPathAsString("wiremock/wiremock-stubs.json");
        this.wireMockManager = WireMockManager.getInstance(wireMockStubs);
        this.client = RestClientHelper.getInstance().buildNoAuthClient();
        this.objectMapper = new ObjectMapper();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        // Don't stop WireMock - let it run for other test classes using the singleton
        // WireMockManager.getInstance() handles restarting if needed
    }

    // ==================== Integration Tests ====================

    @Test(description = "Run performance test against WireMock and verify results contain p50, p95, p99")
    public void testRunPerformanceTest_WithWireMock() throws Exception {
        // Arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethods.GET.getValue())
                .url(WIREMOCK_BASE_URL + "/rest/api/1.0/projects/myproject/repos/myrepo/tags")
                .headers(headers)
                .build();

        OkHttpClient authClient = RestClientHelper.getInstance().buildBearerClient("dummyToken", headers);

        // Act - run with small pool size for quick test
        String jsonResult = PerformanceTestUtil.runPerformanceTest(
                authClient,
                request,
                objectMapper,
                2,    // threadPoolSizeStart
                4,    // threadPoolSizeEnd
                2,    // iterations
                10L,  // startTimeRange
                50L   // endTimeRange
        );

        // Assert
        Assert.assertNotNull(jsonResult, "Result should not be null");
        Assert.assertTrue(jsonResult.contains("totalRequests"), "Should contain totalRequests");
        Assert.assertTrue(jsonResult.contains("successRate"), "Should contain successRate");
        Assert.assertTrue(jsonResult.contains("p50"), "Should contain p50");
        Assert.assertTrue(jsonResult.contains("p95"), "Should contain p95");
        Assert.assertTrue(jsonResult.contains("p99"), "Should contain p99");
        Assert.assertTrue(jsonResult.contains("minExecutionTime"), "Should contain minExecutionTime");
        Assert.assertTrue(jsonResult.contains("maxExecutionTime"), "Should contain maxExecutionTime");
    }

    @Test(description = "Run performance test and verify success rate")
    public void testRunPerformanceTest_SuccessRate() throws Exception {
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
        String jsonResult = PerformanceTestUtil.runPerformanceTest(
                authClient,
                request,
                objectMapper,
                3,    // threadPoolSizeStart
                3,    // threadPoolSizeEnd (same as start = constant pool)
                1,    // iterations
                5L,   // startTimeRange
                20L   // endTimeRange
        );

        // Assert - all requests should succeed against WireMock
        Assert.assertTrue(jsonResult.contains("\"successRate\" : 100.0") ||
                         jsonResult.contains("\"successRate\":100.0"),
                "Success rate should be 100% for valid WireMock endpoint");
    }

    // ==================== Unit Tests ====================

    @Test(description = "Thread count calculation with single iteration returns start value")
    public void testGetThreadCountForIteration_SingleIteration() {
        int result = PerformanceTestUtil.getThreadCountForIteration(10, 100, 1, 0);
        Assert.assertEquals(result, 10, "Single iteration should return start value");
    }

    @Test(description = "Thread count calculation with zero iterations returns start value")
    public void testGetThreadCountForIteration_ZeroIterations() {
        int result = PerformanceTestUtil.getThreadCountForIteration(10, 100, 0, 0);
        Assert.assertEquals(result, 10, "Zero iterations should return start value");
    }

    @Test(description = "Thread count calculation first iteration returns start value")
    public void testGetThreadCountForIteration_FirstIteration() {
        int result = PerformanceTestUtil.getThreadCountForIteration(10, 100, 10, 0);
        Assert.assertEquals(result, 10, "First iteration should return start value");
    }

    @Test(description = "Thread count calculation last iteration returns end value")
    public void testGetThreadCountForIteration_LastIteration() {
        int result = PerformanceTestUtil.getThreadCountForIteration(10, 100, 10, 9);
        Assert.assertEquals(result, 100, "Last iteration should return end value");
    }

    @Test(description = "Thread count calculation middle iteration returns correct value")
    public void testGetThreadCountForIteration_MiddleIteration() {
        // With 10 iterations from 10 to 100, step = (100-10)/(10-1) = 10
        // iteration 5: 10 + (10 * 5) = 60
        int result = PerformanceTestUtil.getThreadCountForIteration(10, 100, 10, 5);
        Assert.assertEquals(result, 60, "Middle iteration should return interpolated value");
    }

    @Test(description = "Thread count calculation with two iterations")
    public void testGetThreadCountForIteration_TwoIterations() {
        int first = PerformanceTestUtil.getThreadCountForIteration(10, 20, 2, 0);
        int second = PerformanceTestUtil.getThreadCountForIteration(10, 20, 2, 1);
        Assert.assertEquals(first, 10, "First of two iterations should be start");
        Assert.assertEquals(second, 20, "Second of two iterations should be end");
    }

    @Test(description = "Thread count calculation with same start and end")
    public void testGetThreadCountForIteration_SameStartEnd() {
        int result = PerformanceTestUtil.getThreadCountForIteration(50, 50, 5, 2);
        Assert.assertEquals(result, 50, "Same start and end should always return that value");
    }

    @Test(description = "Thread count calculation with decreasing values")
    public void testGetThreadCountForIteration_Decreasing() {
        // From 100 down to 10 over 10 iterations
        // step = (10 - 100) / 9 = -10
        int first = PerformanceTestUtil.getThreadCountForIteration(100, 10, 10, 0);
        int middle = PerformanceTestUtil.getThreadCountForIteration(100, 10, 10, 5);
        int last = PerformanceTestUtil.getThreadCountForIteration(100, 10, 10, 9);

        Assert.assertEquals(first, 100, "First iteration should be start (100)");
        Assert.assertEquals(middle, 50, "Middle iteration should be 50");
        Assert.assertEquals(last, 10, "Last iteration should be end (10)");
    }

    @Test(description = "Thread count calculation with three iterations")
    public void testGetThreadCountForIteration_ThreeIterations() {
        // From 0 to 100 over 3 iterations
        // step = 100 / 2 = 50
        int first = PerformanceTestUtil.getThreadCountForIteration(0, 100, 3, 0);
        int second = PerformanceTestUtil.getThreadCountForIteration(0, 100, 3, 1);
        int third = PerformanceTestUtil.getThreadCountForIteration(0, 100, 3, 2);

        Assert.assertEquals(first, 0, "First iteration should be 0");
        Assert.assertEquals(second, 50, "Second iteration should be 50");
        Assert.assertEquals(third, 100, "Third iteration should be 100");
    }

    @Test(description = "Thread count increments evenly across iterations")
    public void testGetThreadCountForIteration_EvenDistribution() {
        int start = 10;
        int end = 50;
        int iterations = 5;

        int[] expected = {10, 20, 30, 40, 50};
        for (int i = 0; i < iterations; i++) {
            int result = PerformanceTestUtil.getThreadCountForIteration(start, end, iterations, i);
            Assert.assertEquals(result, expected[i], "Iteration " + i + " should have correct thread count");
        }
    }
}
