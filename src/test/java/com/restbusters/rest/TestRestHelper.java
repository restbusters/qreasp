package com.restbusters.rest;

import com.restbusters.rest.client.RestClientHelper;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

/**
 * @author amatsaylo on 9/17/19
 * @project qreasp
 */
public class TestRestHelper {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String userName = "test";
    private final String password = "password";
    private final Map<String,String> headers = new HashMap<>();
    private final String requestBody = "{\\\"key\\\": \\\"value\\\"}";

    @BeforeClass
    public void setUp(){
        this.headers.put("fromSetup", "fromSetup");
    }

    @Test
    public void testCreateNewRestClient(){
        OkHttpClient okHttpClient = RestClientHelper.getInstance().buildBasicAuthClient(userName, password);
        Assert.assertTrue( okHttpClient instanceof  OkHttpClient);
    }

    @Test
    public void testCreate2NewRestClient(){
        Map<String,String> headers = new HashMap<>();
        headers.put("headerName", "headerValue");
        RestClientHelper.getInstance().registerLoggerInterceptor2();
        OkHttpClient okHttpClient1 = RestClientHelper.getInstance().buildBasicAuthClient(userName, password);
        OkHttpClient okHttpClient2 = RestClientHelper.getInstance().buildBasicAuthClient("myUser", "mypassword", headers);
        Assert.assertFalse( okHttpClient1.equals(okHttpClient2));
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
    public void buildUrlWithQueryParams(){
        String url = "http://test/search";
        String expectedUrl = "http://test/search?test2=t%26%3F&test=t%2Ftkljl";
        Map<String,String>queryParams = new HashMap<>();
        queryParams.put("test", "t/tkljl");
        queryParams.put("test2", "t&?");
        String actualUrl = RestClientHelper.getInstance().addQueryParams(url, queryParams);
        Assert.assertEquals(actualUrl, expectedUrl, "url must match");
    }


}
