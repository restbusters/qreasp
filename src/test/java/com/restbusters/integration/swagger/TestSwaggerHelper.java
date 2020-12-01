package com.restbusters.integration.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.io.Resources;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.integraton.swagger.PayloadManager;
import com.restbusters.integraton.swagger.SwaggerHelper;
import com.restbusters.integraton.swagger.SwaggerManager;
import com.restbusters.integraton.swagger.model.HttpRestRequest;
import com.restbusters.integraton.swagger.model.HttpRestResponse;
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
    private SwaggerManager swaggerManager;
    private final String swaggetTitle = "SwaggerPetstore";
    private final String swaggerOperationId = "addPet";
    private PayloadManager pm;
    private String payloadResources;
    private String petIdFromPost;



    @BeforeClass
    private void setUp() throws IOException {
        this.payloadResources = readResourceFileAsString("swagger/request-payload.json");
        this.pm = PayloadManager.getInstance(payloadResources);
        this.okHttpClient = RestClientHelper.getInstance().buildNoAuthClient();
        this.wireMockSetInitialState();
        swaggerUrls.add(this.url);
        swaggerUrls.add(this.openApiUrl);
        this.swaggerManager = set_swagger_manager();
        Map<String,String> headers = new HashMap<>();
        headers.put("test", "test");
        this.swaggerManager.setNoneAuthHttpClientForSwagger(swaggetTitle, headers);
    }


    public SwaggerManager set_swagger_manager(){
        List<Map<String,Object>> swaggers = new ArrayList<>();
        Map<String,Object> swagger1 = new HashMap<>();
        swagger1.put("url", this.swaggerUrls);
        swaggers.add(swagger1);
        return new SwaggerManager(swaggers);
    }

    @Test(enabled = true)
    public void swagger_manager_instantiation(){
        Assert.assertNotNull(this.swaggerManager);
    }


    @Test(enabled = true)
    public void build_swagger_descriptor_list(){
        List<Map<String,Object>> swaggers = new ArrayList<>();
        Map<String,Object> swagger1 = new HashMap<>();
        swagger1.put("url", this.swaggerUrls);
        swaggers.add(swagger1);
        Assert.assertEquals(this.swaggerManager.getSwaggerDescriptor().size(), this.swaggerUrls.size());
        this.swaggerManager = swaggerManager;
        Assert.assertNotNull(swaggerManager.findSwaggerResource("SwaggerPetstore","addPet"));
    }

    @Test(enabled = true, dependsOnMethods = "build_swagger_descriptor_list")
    public void test_swagger_filter(){
        Assert.assertNotNull(swaggerManager.findSwaggerResource(this.swaggetTitle, this.swaggerOperationId));
    }

    @Test(enabled = true, dependsOnMethods = "build_swagger_descriptor_list")
    public void test_swagger_filter_invalid_swagger_title(){
        Assert.assertNull(swaggerManager.findSwaggerResource("invalid",this.swaggerOperationId));
    }

    @Test(enabled = true, dependsOnMethods = "build_swagger_descriptor_list")
    public void filter_invalid_operationId(){
        Assert.assertNull(swaggerManager.findSwaggerResource(this.swaggetTitle,"invalid"));
    }

    @Test(enabled = true, dependsOnMethods = "build_swagger_descriptor_list")
    public void find_descriptor(){
        Assert.assertNotNull(swaggerManager.findSwaggerDescriptor(this.swaggetTitle));
    }

    @Test(enabled = true, dependsOnMethods = "execute_rest_post")
    public void execute_rest_get(){
        String swaggetTitle = "SwaggerPetstore";
        String operationId = "getPetById";
        Map<String,String> urlParams = new HashMap<>();
        urlParams.put("petId", this.petIdFromPost);
        HttpRestRequest httpRestRequest = new HttpRestRequest();
        httpRestRequest.setUrlParams(urlParams);
        httpRestRequest.setApiTitle(swaggetTitle);
        httpRestRequest.setOperationId(operationId);
        HttpRestResponse httpRestResponse = this.swaggerManager.executeSwaggerEndPoint(httpRestRequest);
        Assert.assertEquals(httpRestResponse.getStatus(), "FINISHED");
        Assert.assertEquals(httpRestResponse.getHttpCode(), 200);
    }

    @Test(enabled = true, dependsOnMethods = "build_swagger_descriptor_list")
    public void create_template_from_swagger(){
        String actualName = "myName";
        Map<String,Object>payload = new HashMap<>();
        payload.put("categoryName", actualName);
        payload.put("petName", actualName);
        String requestBody = pm.getPayload(this.swaggetTitle, this.swaggerOperationId, null, payload);
        String expectedName = JsonPath.read(requestBody, "$.category.name");
        Assert.assertEquals(actualName, expectedName);
    }

    @Test(enabled = true, dependsOnMethods = "build_swagger_descriptor_list")
    public void execute_rest_post(){
        String swaggerTitle = "SwaggerPetstore";
        String operationId = "addPet";
        String actualName = "Iva";
        Map<String,Object>payload = new HashMap<>();
        payload.put("categoryName", actualName);
        payload.put("petName", actualName);
        String requestBody = pm.getPayload(this.swaggetTitle, operationId, null, payload);
        //run post
        HttpRestRequest httpRestRequest = new HttpRestRequest();
        httpRestRequest.setApiTitle(swaggetTitle);
        httpRestRequest.setOperationId(operationId);
        httpRestRequest.setRequestBody(requestBody);
        HttpRestResponse httpRestResponse = this.swaggerManager.executeSwaggerEndPoint(httpRestRequest);
        Assert.assertEquals(httpRestResponse.getHttpCode(), 200);
        Assert.assertEquals(httpRestResponse.getStatus(), "FINISHED");
        Long id = JsonPath.read(httpRestResponse.getResponseBody(), "$.id");
        this.petIdFromPost = String.valueOf(id);
    }

    @Test(enabled = true, dependsOnMethods = "execute_rest_get")
    public void send_put_request(){
        String swaggerTitle = "SwaggerPetstore";
        String operationId = "updatePet";
        String actualName = "Iva2";
        Map<String,Object>payload = new HashMap<>();
        payload.put("categoryName", actualName);
        payload.put("petName", actualName);
        payload.put("id", this.petIdFromPost);
        String requestBody = pm.getPayload(this.swaggetTitle, operationId, null, payload);
        //run post
        HttpRestRequest httpRestRequest = new HttpRestRequest();
        httpRestRequest.setApiTitle(swaggerTitle);
        httpRestRequest.setOperationId(operationId);
        httpRestRequest.setRequestBody(requestBody);
        HttpRestResponse httpRestResponse = this.swaggerManager.executeSwaggerEndPoint(httpRestRequest);
        Assert.assertEquals(httpRestResponse.getHttpCode(), 200);
        Assert.assertEquals(httpRestResponse.getStatus(), "FINISHED");
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

    private void wireMockSetInitialState() throws IOException {
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

    private String readResourceFileAsString(String fileResourcePath){
        try {
            URL url = Resources.getResource(fileResourcePath);
            return Resources.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
