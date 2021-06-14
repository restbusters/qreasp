package com.restbusters.integraton.tc.client;

import com.restbusters.integraton.tc.client.resoures.TCResourceManager;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRestRequest;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author smatsaylo on 6/2/21
 * @project tc-client
 */
public class TeamCityClient {

    private String authToken;
    private String serverUrl;
    private OkHttpClient tcClient;
    private TCResourceManager resourceManager = TCResourceManager.getInstance();

    public TeamCityClient(String serverUrl, String authToken) throws Exception {
        this.authToken = authToken;
        this.serverUrl = serverUrl.replaceAll("/$", "");
        this.tcClient = RestClientHelper.getInstance().buildBearerClient(authToken, getHeaders());
        this.resourceManager.initServerUrl(this.serverUrl);
        RestClientHelper.getInstance().addHeader(tcClient, "Accept", "application/json");
}

    public Response getBuilds() {
        try {
            return RestClientHelper.getInstance().executeRequest(tcClient, resourceManager.getTcRequests().getGetBuilds());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response getBuildById(String buildId) throws Exception {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("id", buildId);
        //HttpRestRequest httpRestRequest = new HttpRestRequest();
        HttpRestRequest httpRestRequest = resourceManager.getTcRequests().getGetBuildById();
        httpRestRequest.setUrlParams(urlParams);
        return executeCall(httpRestRequest);
    }

    public Response postBuild(String jsonRequestBody) throws Exception {
        HttpRestRequest httpRestRequest = new HttpRestRequest();
        httpRestRequest = resourceManager.getTcRequests().getPostBuild();
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

}