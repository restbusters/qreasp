package com.restbusters.rest;

import com.restbusters.rest.model.HttpRequestBuilder;
import com.restbusters.rest.model.HttpRestRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * @author Sasha Matsaylo on 7/14/21
 * @project qreasp
 */
public class TestRequestBuilder {

    @BeforeClass(alwaysRun = true)
    private void setUp(){

    }

    @Test
    public void testRequestBuilder(){
        HttpRequestBuilder builder = new HttpRequestBuilder("GET", "url")
                .setUrlParams(new HashMap<>())
                .setQueryParams(new HashMap<>())
                .setHeaders(new HashMap<>())
                .setRequestBody("")
                .setContentType("");

        HttpRestRequest httpRestRequest = builder.build();
        Assert.assertEquals(httpRestRequest.getHttpMethod(), "GET");
    }
}
