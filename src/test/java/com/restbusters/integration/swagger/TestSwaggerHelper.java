package com.restbusters.integration.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.io.Resources;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.exception.SwaggerTitleNotSet;
import com.restbusters.integraton.swagger.SwaggerHelper;
import com.restbusters.integraton.swagger.SwaggerManager;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.client.RestClientHelper;
import net.minidev.json.JSONArray;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * @author Sasha Matsaylo on 2020-10-02
 * @project qreasp
 */
public class TestSwaggerHelper {

    private SwaggerHelper swaggerHelper = SwaggerHelper.getInstance();
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String url = "https://petstore.swagger.io/v2/swagger.json";
    private final String openApiUrl = "http://localhost:8090/v2/openapi.json";
    private final List<String> swaggerUrls = new ArrayList<>();
    private final ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();
    private WireMockServer wireMockServer;
    private final int wireMockPort = 8090;
    private final String wireMockAdminUrl = "http://localhost:8090/__admin/mappings";
    private OkHttpClient okHttpClient;



    @BeforeClass
    private void setUp() throws IOException {
        this.okHttpClient = RestClientHelper.getInstance().buildNoAuthClient();
        this.wireMockSetInitialState();
        swaggerUrls.add(this.url);
        swaggerUrls.add(this.url);
        swaggerUrls.add(this.openApiUrl);
    }


    @Test(enabled = false)
    public void build_api_resource() throws SwaggerTitleNotSet {
        SwaggerDescriptor swaggerDescriptor = this.swaggerHelper.getSwaggerDescriptor(url);
        Assert.assertEquals(swaggerDescriptor.getApiTitle(), "Swagger Petstore");
        try {
            logger.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(swaggerDescriptor));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = false)
    public void build_api_resources_list() {
        List<SwaggerDescriptor> swaggerDescriptors = this.swaggerHelper.getSwaggerApiResources(swaggerUrls);
        Assert.assertEquals(swaggerDescriptors.size(), 1, "2 swagger descriptors");

    }

    @Test(enabled = true)
    public void test_swagger_manager(){
        List<Map<String,Object>> swaggers = new ArrayList<>();
        Map<String,Object> swagger1 = new HashMap<>();
        swagger1.put("url", this.swaggerUrls);
        swaggers.add(swagger1);
        SwaggerManager swaggerManager = new SwaggerManager(swaggers);
        Assert.assertEquals(swaggerManager.getSwaggerDescriptor().size(), 3);
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
        URL url = Resources.getResource("swagger/wiremock-stub.json");
        String stubs = Resources.toString(url, StandardCharsets.UTF_8);
        JSONArray jsonArray = JsonPath.read(stubs, "$");
        for (Object stub : jsonArray) {
            String jsonStub = objectMapper.writeValueAsString(stub);
            Response response =
                    RestClientHelper.getInstance().doPostRequest(this.okHttpClient, this.wireMockAdminUrl, jsonStub, null);
            Assert.assertEquals(response.code(), 201);
        }
    }



}
