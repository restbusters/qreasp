package com.restbusters.util.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.client.HttpMethods;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRequest;
import net.minidev.json.JSONArray;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * @author restbusters on 10/15/18
 * @project qreasp
 */

public class WireMockManager {

    private static WireMockManager instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private WireMockServer wireMockServer;
    private final int wireMockPort = 8090;
    private final String wireMockAdminUrl = "http://localhost:8090/__admin/mappings";
    private final OkHttpClient wireMockClient = RestClientHelper.getInstance().buildNoAuthClient();
    private String jsonWireMockStubs;


    private WireMockManager(String listOfWireMockStubsAsJson) throws IOException {
        this.jsonWireMockStubs = listOfWireMockStubsAsJson;
        this.wireMockSetInitialState();
    }


    public static synchronized WireMockManager getInstance(String listOfWireMockStubsAsJson) throws IOException {
        if (instance == null) {
            instance = new WireMockManager(listOfWireMockStubsAsJson);
        }
        return instance;
    }

    public void startWireMock() {
        this.wireMockServer.start();
    }

    public void stopWireMock() {
        this.wireMockServer.stop();
    }

    public void resetWireMock() {
        this.wireMockServer.resetAll();
    }

    public void resetScenarios(){
        this.wireMockServer.resetScenarios();
    }

    public List<StubMapping> getWireMockStubs(){
        return this.wireMockServer.getStubMappings();
    }

    private void wireMockSetInitialState() throws IOException {
        wireMockServer = new WireMockServer(wireMockConfig().port(wireMockPort));
        startWireMock();
        JSONArray jsonArray = JsonPath.read(this.jsonWireMockStubs, "$");
        for (Object stub : jsonArray) {
            String jsonStub = GlobalResourceManager.getInstance().getObjectMapper().writeValueAsString(stub);
            HttpRequest httpRequest = new HttpRequest(HttpMethods.POST.getValue(), wireMockAdminUrl);
            httpRequest.setRequestBody(jsonStub);
            Response response = RestClientHelper.getInstance().executeRequest(wireMockClient, httpRequest);
            if(!response.isSuccessful()){
                logger.error("Failed to set wire mock stub {}", jsonStub);
                logger.error(jsonStub);
            }
        }
    }
}



