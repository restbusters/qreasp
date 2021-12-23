package com.restbusters.integraton.tc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.integraton.tc.client.model.TCRequests;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRestRequest;
import com.restbusters.util.common.RBFileUtils;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author smatsaylo on 6/2/21
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
        HttpRestRequest httpRestRequest = this.tcRequests.getGetBuildById();
        httpRestRequest.setUrlParams(urlParams);
        return executeCall(httpRestRequest);
    }

    public Response postBuild(String jsonRequestBody) {
        HttpRestRequest httpRestRequest = this.tcRequests.getPostBuild();
        httpRestRequest.setRequestBody(jsonRequestBody);
        return executeCall(httpRestRequest);
    }

    private Response executeCall(HttpRestRequest httpRestRequest){
        try {
            return RestClientHelper.getInstance().executeRequest(tcClient, httpRestRequest);
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
        Map<String, HttpRestRequest> restRequestMap;
        restRequestMap =
                objectMapper.convertValue(
                        tcRequests, new TypeReference<Map<String, HttpRestRequest>>() {});
        for (Map.Entry<String, HttpRestRequest> entry : restRequestMap.entrySet()) {
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