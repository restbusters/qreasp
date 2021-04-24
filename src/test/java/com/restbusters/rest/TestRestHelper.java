package com.restbusters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.client.RestClientHelper;
import net.minidev.json.JSONArray;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * @author amatsaylo on 9/17/19
 * @project qreasp
 */
public class TestRestHelper {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String userName = "test";
    private final String password = "password";
    private final Map<String, String> headers = new HashMap<>();
    private final String requestBody = "{\\\"key\\\": \\\"value\\\"}";
    private WireMockServer wireMockServer;
    private final int wireMockPort = 8090;
    private final String wireMockAdminUrl = "http://localhost:8090/__admin/mappings";
    private final OkHttpClient wireMockClient = RestClientHelper.getInstance().buildNoAuthClient();
    private final ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();

    @BeforeClass
    public void setUp() throws IOException {
        this.headers.put("fromSetup", "fromSetup");
        this.wireMockSetInitialState();
    }

    @Test
    public void testCreateNewRestClient() {
        OkHttpClient okHttpClient = RestClientHelper.getInstance().buildBasicAuthClient(userName, password);
        Assert.assertTrue(okHttpClient instanceof OkHttpClient);
    }

    @Test
    public void testCreate2NewRestClient() {
        Map<String, String> headers = new HashMap<>();
        headers.put("headerName", "headerValue");
        RestClientHelper.getInstance().registerLoggerInterceptorForSharedClient();
        OkHttpClient okHttpClient1 = RestClientHelper.getInstance().buildBasicAuthClient(userName, password);
        OkHttpClient okHttpClient2 = RestClientHelper.getInstance().buildBasicAuthClient("myUser", "mypassword", headers);
        Assert.assertFalse(okHttpClient1.equals(okHttpClient2));
    }


    @Test//(threadPoolSize = 3, invocationCount = 6)
    public void testDoGetRequest() throws IOException {
        String url = "https://httpbin.org/get";
        OkHttpClient okHttpClient = RestClientHelper.getInstance().buildBasicAuthClient(userName, password, headers);
        Response response = RestClientHelper.getInstance().doGetRequest(okHttpClient, url, null, null);
        Assert.assertTrue(response.code() == 200);

    }

    @Test
    public void testDoPostRequest() throws IOException {
        String url = "https://httpbin.org/anything";
        OkHttpClient okHttpClient = RestClientHelper.getInstance().buildBasicAuthClient(userName, password, headers);
        Response response = RestClientHelper.getInstance().doPostRequest(okHttpClient, url, this.requestBody, null);
        Assert.assertTrue(response.code() == 200);
    }

    @Test
    public void testDoPutRequest() throws IOException {
        String url = "https://httpbin.org/anything";
        OkHttpClient okHttpClient = RestClientHelper.getInstance().buildBasicAuthClient(userName, password, headers);
        Response response = RestClientHelper.getInstance().doPutRequest(okHttpClient, url, this.requestBody, null);
        Assert.assertTrue(response.code() == 200);
    }

    @Test
    public void execute_general_request() throws IOException {
        String url = "https://httpbin.org/anything";
        OkHttpClient okHttpClient = RestClientHelper.getInstance().buildBasicAuthClient(userName, password, headers);
        Response response = RestClientHelper.getInstance().executeRequest(okHttpClient, "POST", url, null, null, this.requestBody);
        Assert.assertTrue(response.code() == 200);
    }

    @Test
    public void testDoPatchRequest() throws IOException {
        String url = "https://httpbin.org/anything";
        OkHttpClient okHttpClient = RestClientHelper.getInstance().buildBasicAuthClient(userName, password, headers);
        Response response = RestClientHelper.getInstance().doPatchRequest(okHttpClient, url, this.requestBody, null);
        Assert.assertTrue(response.code() == 200);
    }

    @Test
    public void testDoDeleteRequest() throws IOException {
        String url = "https://httpbin.org/anything";
        OkHttpClient okHttpClient = RestClientHelper.getInstance().buildBasicAuthClient(userName, password, headers);
        Response response = RestClientHelper.getInstance().doDeleteRequest(okHttpClient, url, null, null);
        Assert.assertTrue(response.code() == 200);
    }

    @Test
    public void buildUrlWithQueryParams() {
        String url = "http://test/search";
        String expectedUrl = "http://test/search?test2=t%26%3F&test=t%2Ftkljl";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("test", "t/tkljl");
        queryParams.put("test2", "t&?");
        String actualUrl = RestClientHelper.getInstance().addQueryParams(url, queryParams);
        Assert.assertEquals(actualUrl, expectedUrl, "url must match");
    }

    @Test
    public void execute_general_request_with_header_client() throws IOException {
        String url = "https://httpbin.org/anything";
        Long timeout = Long.valueOf(10);
        headers.put("key1", "value1");
        OkHttpClient okHttpClient = RestClientHelper.getInstance().buildClientWithHeaders(headers, timeout, timeout, timeout);
        okHttpClient = RestClientHelper.getInstance().registerLoggerInterceptor(okHttpClient);
        Response response = RestClientHelper.getInstance().executeRequest(okHttpClient, "POST", url, null, null, this.requestBody);
        String body = response.body().string();
        Assert.assertTrue(response.code() == 200);
        Assert.assertTrue(body.contains("value1"));
    }

    @Test(enabled = true)
    public void get_oath2_token(){
        Map<String,String> params = new HashMap<>();
        params.put("param1", "paramValue1");
        params.put("param2", "paramValue2");
        params.put("param3", "paramValue3");
        String token = RestClientHelper.getInstance().getOAuth2Token("http://localhost:8090/oauth/token", params, "$.access_token");
        Assert.assertEquals(token, "dummytoken", "tokens should match");
    }

    private void startWireMock() {
        this.wireMockServer.start();
    }

    private void stopWireMock() {
        this.wireMockServer.stop();
    }

    private void resetWireMock() {
        this.wireMockServer.resetAll();
    }

    public void wireMockSetInitialState() throws IOException {
        wireMockServer = new WireMockServer(wireMockConfig().port(wireMockPort));
        startWireMock();
        String stubs = readFile("wiremock/wiremock-stubs.json");
        JSONArray jsonArray = JsonPath.read(stubs, "$");
        for (Object stub : jsonArray) {
            String jsonStub = objectMapper.writeValueAsString(stub);
            Response response = RestClientHelper.getInstance().doPostRequest(wireMockClient, wireMockAdminUrl, jsonStub, null);
            Assert.assertEquals(response.code(), 201);
        }
    }

    public String readFile(String fileName) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return IOUtils.toString(classloader.getResourceAsStream(fileName), "UTF-8");
    }


}
