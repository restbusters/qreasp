package com.restbusters.integraton.stash.client;

import com.restbusters.integraton.stash.client.resoures.StashConstant;
import com.restbusters.integraton.stash.client.resoures.StashResourceManager;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRestRequest;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author smatsaylo on 6/2/21
 * @project stash-client
 */
public class StashRestClient {

    private String authToken;
    private String serverUrl;
    private String projectName;
    private String repoName;
    private OkHttpClient tcClient;
    private String userName;
    private String password;
    private String workSpaceName;
    private StashResourceManager stashResourceManager = StashResourceManager.getInstance();

    public StashRestClient(String serverUrl, String authToken, String projectName, String repoName) throws Exception {
        this.authToken = authToken;
        this.serverUrl = serverUrl.replaceAll("/$", "");
        this.projectName = projectName;
        this.repoName = repoName;
        this.tcClient = RestClientHelper.getInstance().buildBearerClient(authToken, getHeaders());
        this.stashResourceManager.initServerUrl(this.serverUrl);
    }

    public StashRestClient(String serverUrl, String userName, String password, String workSpaceName, String repoName) throws Exception {
        this.userName = userName;
        this.password  = password;
        this.workSpaceName = workSpaceName;
        this.serverUrl = serverUrl.replaceAll("/$", "");
        this.repoName = repoName;
        this.tcClient = RestClientHelper.getInstance().buildBasicAuthClient(userName, password, getHeaders());
        this.stashResourceManager.initServerUrl(this.serverUrl);
    }


    public Response getCommitsInRangeV1(String since, String until, @Nullable Integer start, @Nullable Integer limit) {
        HttpRestRequest httpRestRequest = this.stashResourceManager.getStashRequests().getGetCommitsInRange();
        Map<String, String> queryParams = httpRestRequest.getQueryParams();
        queryParams.put(StashConstant.MAP_QUERY_PARAM_KEY_SINCE, since);
        queryParams.put(StashConstant.MAP_QUERY_PARAM_KEY_UNTIL, until);
        httpRestRequest.setQueryParams(setQueryParamsStarLimit(start, limit, queryParams));
        httpRestRequest.setQueryParams(queryParams);
        return executeCall(httpRestRequest);
    }

    public Response getTagsV1(@Nullable Integer start, @Nullable Integer limit) {
        HttpRestRequest httpRestRequest = this.stashResourceManager.getStashRequests().getGetTags();
        Map<String, String> queryParams = httpRestRequest.getQueryParams();
        httpRestRequest.setQueryParams(setQueryParamsStarLimit(start, limit, queryParams));
        return executeCall(httpRestRequest);
    }

    public Response getCommitsInRangeV2() {
        HttpRestRequest httpRestRequest = this.stashResourceManager.getStashRequests().getGetCommitsInRangeApiV2();
        httpRestRequest = setDefaultUrlParamsV2(httpRestRequest);
        return executeCall(httpRestRequest);
    }

    private Map<String,String> setQueryParamsStarLimit(Integer start, Integer limit, Map<String,String> queryParams){
        if (start != null) {
            queryParams.put(StashConstant.MAP_QUERY_PARAM_KEY_START, String.valueOf(start));
        }
        if (limit != null) {
            queryParams.put(StashConstant.MAP_QUERY_PARAM_KEY_LIMIT, String.valueOf(limit));
        }
        return queryParams;
    }

    private Response executeCall(HttpRestRequest httpRestRequest) {
        try {
            httpRestRequest = setDefaultUrlParamsV1(httpRestRequest);
            return RestClientHelper.getInstance().executeRequest(tcClient, httpRestRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        return headers;
    }

    private HttpRestRequest setDefaultUrlParamsV1(HttpRestRequest httpRestRequest) {
        Map<String, String> urlParams = httpRestRequest.getUrlParams();
        urlParams.put(StashConstant.MAP_URL_PARAM_KEY_PROJECT_NAME, this.projectName);
        urlParams.put(StashConstant.MAP_URL_PARAM_KEY_REPO_NAME, this.repoName);
        return httpRestRequest;
    }

    private HttpRestRequest setDefaultUrlParamsV2(HttpRestRequest httpRestRequest) {
        Map<String, String> urlParams = httpRestRequest.getUrlParams();
        urlParams.put(StashConstant.MAP_URL_PARAM_KEY_WORKSPACE_NAME, this.workSpaceName);
        urlParams.put(StashConstant.MAP_URL_PARAM_KEY_REPO_NAME, this.repoName);
        return httpRestRequest;
    }

    private void resetProjectAndRepo(String projectName, String repoName) {
        this.projectName = projectName;
        this.repoName = repoName;
    }

    private void resetWorkSpaceAndRepo(String workSpaceName, String repoName) {
        this.workSpaceName = projectName;
        this.repoName = repoName;
    }

}