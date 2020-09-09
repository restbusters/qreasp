package com.restbusters.rest.util.deployment;

import com.restbusters.rest.util.restclient.RestClientHelper;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author smatsaylo on 8/28/20
 * @project rest-support
 */
public class StashHelper {

    private static StashHelper instance;
    private String token;
    private OkHttpClient bearerAuthClient;
    private String stashHost;

    private StashHelper() throws Exception {
    }

    public static synchronized StashHelper getInstance() throws Exception {
        if (instance == null) {
            instance = new StashHelper();
        }
        return instance;
    }

    public void init(String stashHost, String token) throws Exception {
        this.stashHost = stashHost;
        this.token = token;
        this.bearerAuthClient = RestClientHelper.getInstance().getOkHttpClientBearerWithToken(this.token);
    }

    public Optional<Response> getManifest(String apiPath, String env, String tag){
        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("at", "refs/tags/"+tag);
        Map<String,String> urlParams = new HashMap<>();
        urlParams.put("env", env);
            return Optional.ofNullable(executeRequest(apiPath, urlParams, queryParams));
    }

    public Optional<Response> getCommitByHash(String apiPath, String gitSha){
        Map<String,String> urlParams = new HashMap<>();
        urlParams.put("gitSha", gitSha);
        return Optional.ofNullable(executeRequest(apiPath, urlParams, null));
    }

    public Optional<Response> getTags(String apiPath){
        return Optional.ofNullable(executeRequest(apiPath, null, null));
    }

    private Response executeRequest(String apiPath, @Nullable Map<String,String> urlParams, @Nullable Map<String,String> queryParams){
        Response response = null;
        try {
            response =  RestClientHelper.getInstance().doGetRequest(bearerAuthClient, stashHost + apiPath, urlParams, queryParams );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String shortenLocalizedMessage(String message) {
        return message.substring(0, Math.min(message.length(), 255));
    }

}