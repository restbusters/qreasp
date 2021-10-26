package com.restbusters.integraton.stash.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.restbusters.exception.InvalidParameterException;
import com.restbusters.integraton.stash.client.model.StashRequests;
import com.restbusters.integraton.stash.client.model.StashResponse;
import com.restbusters.integraton.stash.client.resoures.StashConstant;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRestRequest;
import com.restbusters.util.common.RBFileUtils;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

/**
 * @author smatsaylo on 6/2/21
 * @project stash-client
 */
public class StashRestClient {

    private String serverUrl;
    private String projectName;
    private String repoName;
    private OkHttpClient tcClient;
    private String workSpaceName;
    private StashRequests stashRequests;
    private String jsonStashRequests;
    private final String requestsFile = "stash-http-requests.json";

    public StashRestClient(String serverUrl, String authToken, String projectName, String repoName) throws Exception {
        if(StringUtils.isBlank(authToken)){
            throw new IllegalArgumentException("Auth token must be provided");
        }
        init(serverUrl, null, null, projectName, repoName, authToken);
    }

    public StashRestClient(String serverUrl, String userName, String password, String projectName, String repoName) throws Exception {
        if(StringUtils.isBlank(userName)){
            throw new IllegalArgumentException("User Name must be provided");
        }
        if(StringUtils.isBlank(userName)){
            throw new IllegalArgumentException("User Password must be provided");
        }
       init(serverUrl, userName, password, projectName, repoName, null);
    }

    private void init(String serverUrl, @Nullable String userName, @Nullable String password, String projectName, String repoName, @Nullable String authToken) throws JsonProcessingException {
        if(StringUtils.isNoneBlank(authToken)){
            this.tcClient = RestClientHelper.getInstance().buildBearerClientWithCustomInterceptor(authToken, new HeaderInterceptor());
        }
        else {
            this.tcClient = RestClientHelper.getInstance().buildBasicAuthClientWithCustomInterceptor(userName, password, new HeaderInterceptor());
        }

        if(StringUtils.isBlank(serverUrl)){
            throw new IllegalArgumentException("Server url must be provided");
        }
        this.serverUrl = serverUrl.replaceAll("/$", "");

        if(StringUtils.isBlank(projectName)){
            throw new IllegalArgumentException("Project/WorkSpace name must be provided");
        }
        //for v2 api project calls work space for now I will keep two variable pointed to the same value projectName
        this.projectName = projectName;
        this.workSpaceName = projectName;

        if(StringUtils.isBlank(repoName)){
            throw new IllegalArgumentException("Repo name must be provided");
        }
        this.repoName = repoName;
        this.jsonStashRequests = RBFileUtils.getFileOnClassPathAsString(this.requestsFile);
        this.stashRequests = GlobalResourceManager.getInstance().getObjectMapper().readValue(jsonStashRequests, StashRequests.class);
        this.initServerUrl(this.serverUrl);
    }


    public Response getCommitsInRangeV1(String since, String until, @Nullable Integer start, @Nullable Integer limit) {
        HttpRestRequest httpRestRequest = this.stashRequests.getGetCommitsInRange();
        Map<String, String> queryParams = httpRestRequest.getQueryParams();
        queryParams.put(StashConstant.MAP_QUERY_PARAM_KEY_SINCE, since);
        queryParams.put(StashConstant.MAP_QUERY_PARAM_KEY_UNTIL, until);
        httpRestRequest.setQueryParams(setQueryParamsStarLimit(start, limit, queryParams));
        httpRestRequest.setQueryParams(queryParams);
        return executeCall(httpRestRequest, StashConstant.API_V1);
    }

    public Response getTagsV1(@Nullable Integer start, @Nullable Integer limit) {
        HttpRestRequest httpRestRequest = this.stashRequests.getGetTags();
        Map<String, String> queryParams = httpRestRequest.getQueryParams();
        httpRestRequest.setQueryParams(setQueryParamsStarLimit(start, limit, queryParams));
        return executeCall(httpRestRequest, StashConstant.API_V1);
    }

    public Response getCommitsInRangeV2() {
        HttpRestRequest httpRestRequest = this.stashRequests.getGetCommitsInRangeApiV2();
        httpRestRequest = setDefaultUrlParamsV2(httpRestRequest);
        return executeCall(httpRestRequest, StashConstant.API_V2);
    }

    /**
     * Returns file Content as String
     * The filePath argument must specify an relative path from repo root <a href="#{@filePath}">{@filePath filePath}</a>. The gitRef
     * argument is a specifier for reference to branch refs/heads/yourBranch or tag refs/tags/yourTag.
     * <p>
     * This method always returns OkHttp Response if response Success check the body
     * of response for file content.
     * </p>
     * @param  filePath  path to the file from the repo root
     * @param  gitReference git reference to the branch or tag
     * @return      fileContent as String
     */
    public StashResponse getFileContent(String filePath, String gitReference) throws InvalidParameterException {
        if (StringUtils.isBlank(filePath)) {
            throw new InvalidParameterException("Parameter filePath must not be null");
        }
        if (StringUtils.isBlank(gitReference)) {
            throw new InvalidParameterException("Parameter gitReference must not be null");
        }
        HttpRestRequest httpRestRequest = this.stashRequests.getGetFileContent();
        Map<String, String> queryParams = httpRestRequest.getQueryParams();
        queryParams.put("at", StringUtils.removeStartIgnoreCase(gitReference, "/"));
        httpRestRequest.setQueryParams(queryParams);
        Map<String,String> urlParam = httpRestRequest.getUrlParams();
        urlParam.put("filePath", StringUtils.removeStartIgnoreCase(filePath, "/"));
        return buildStashResponse(executeCall(httpRestRequest, StashConstant.API_V1));

    }

    public Response getCommitByHash(String gitHash) {
        HttpRestRequest httpRestRequest = this.stashRequests.getGetCommitByHash();
        Map<String, String> urlParams = httpRestRequest.getUrlParams();
        urlParams.put(StashConstant.MAP_URL_PARAM_KEY_GIT_HASH, gitHash);
        httpRestRequest.setUrlParams(urlParams);
        return executeCall(httpRestRequest, StashConstant.API_V1);
    }

    private StashResponse buildStashResponse(Response response){
        StashResponse stashResponse = new StashResponse();
        try {
            String body = response.body().string();
            stashResponse.setHttpStatus(response.code());
            if(!response.isSuccessful()){
                stashResponse.setErrorReason(body);
            }
            else {
                stashResponse.setRequestBody(body);
            }
            closeResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            closeResponse(response);
        }
        return stashResponse;
    }

    private static void closeResponse(Response response){
        response.body().close();
        response.close();
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

    private Response executeCall(HttpRestRequest httpRestRequest, String apiVersion) {
        try {
            if(apiVersion.equalsIgnoreCase(StashConstant.API_V1)){
                httpRestRequest = setDefaultUrlParamsV1(httpRestRequest);
            }
            else if(apiVersion.equalsIgnoreCase(StashConstant.API_V2)){
                httpRestRequest = setDefaultUrlParamsV1(httpRestRequest);
            }
            else {
                throw new RuntimeException("Api Version not supported");
            }
            return RestClientHelper.getInstance().executeRequest(tcClient, httpRestRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public void initServerUrl(String serverUrl) {
        Map<String, HttpRestRequest> restRequestMap;
        restRequestMap =
                GlobalResourceManager.getInstance().getObjectMapper().convertValue(
                        stashRequests, new TypeReference<Map<String, HttpRestRequest>>() {});
        for (Map.Entry<String, HttpRestRequest> entry : restRequestMap.entrySet()) {
            entry.getValue().setUrl(serverUrl + entry.getValue().getUri());
        }
        try {
            this.jsonStashRequests = GlobalResourceManager.getInstance().getObjectMapper().writeValueAsString(restRequestMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            this.stashRequests = GlobalResourceManager.getInstance().getObjectMapper().readValue(jsonStashRequests, StashRequests.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}