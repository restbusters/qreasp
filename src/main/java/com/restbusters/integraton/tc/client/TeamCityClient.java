package com.restbusters.integraton.tc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.integraton.tc.client.model.TCRequests;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRequest;
import com.restbusters.util.common.RBFileUtils;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author smatsaylo
 * @project tc-client
 */
public class TeamCityClient {

    private String authToken;
    private String serverUrl;
    private OkHttpClient tcClient;
    private ObjectMapper objectMapper;
    private TCRequests tcRequests;
    private String jsonTcRequests;
    private final String requestsFile = "tc-http-requests.json";

    public TeamCityClient(String serverUrl, String authToken) throws Exception {
        if(StringUtils.isBlank(serverUrl)){
            throw new IllegalArgumentException("Server url must be provided");
        }
        if(StringUtils.isBlank(authToken)){
            throw new IllegalArgumentException("Auth token must be provided");
        }
        this.authToken = authToken;
        this.serverUrl = serverUrl.replaceAll("/$", "");
        this.tcClient = RestClientHelper.getInstance().buildBearerClient(authToken, getHeaders());
        this.objectMapper = GlobalResourceManager.getInstance().getObjectMapper();
        this.jsonTcRequests = RBFileUtils.getFileOnClassPathAsString(this.requestsFile);
        this.tcRequests = objectMapper.readValue(jsonTcRequests, TCRequests.class);
        this.initServerUrl(this.serverUrl);
        RestClientHelper.getInstance().addHeader(tcClient, "Accept", "application/json");
}

    public Response getBuilds() {
        try {
            return RestClientHelper.getInstance().executeRequest(tcClient, this.tcRequests.getGetBuilds());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response getBuildById(String buildId){
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("id", buildId);
        HttpRequest httpRequest = this.tcRequests.getGetBuildById();
        httpRequest.setUrlParams(urlParams);
        return executeCall(httpRequest);
    }

    public Response postBuild(String jsonRequestBody) {
        HttpRequest httpRequest = this.tcRequests.getPostBuild();
        httpRequest.setRequestBody(jsonRequestBody);
        return executeCall(httpRequest);
    }

    private Response executeCall(HttpRequest httpRequest){
        try {
            return RestClientHelper.getInstance().executeRequest(tcClient, httpRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> getHeaders(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        return headers;
    }

    public void initServerUrl(String serverUrl) {
        Map<String, HttpRequest> restRequestMap;
        restRequestMap =
                objectMapper.convertValue(
                        tcRequests, new TypeReference<Map<String, HttpRequest>>() {});
        for (Map.Entry<String, HttpRequest> entry : restRequestMap.entrySet()) {
            entry.getValue().setUrl(serverUrl + entry.getValue().getUri());
        }
        try {
            this.jsonTcRequests = this.objectMapper.writeValueAsString(restRequestMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            this.tcRequests = objectMapper.readValue(jsonTcRequests, TCRequests.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
