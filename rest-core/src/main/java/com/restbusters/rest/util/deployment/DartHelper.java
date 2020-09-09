package com.restbusters.rest.util.deployment;

import com.restbusters.rest.util.restclient.RestClientHelper;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @author smatsaylo on 8/28/20
 * @project rest-support
 */
public class DartHelper {

    private static DartHelper instance;
    private String password;
    private OkHttpClient okHttpClient;
    private String dartHost;
    private String dartUser;

    private DartHelper() throws Exception {
    }

    public static synchronized DartHelper getInstance() throws Exception {
        if (instance == null) {
            instance = new DartHelper();
        }
        return instance;
    }

    public void init(String dartHost, String user, String password) throws Exception {
        this.dartHost = dartHost;
        this.dartUser = user;
        this.password = password;
        this.okHttpClient = RestClientHelper.getInstance().getOkHttpClient(this.dartUser, this.password);
    }

    public Optional<Response> uploadManifest(String ymlManifest){
        Response response = null;
        try {
            response = RestClientHelper.getInstance().doPostRequestWithMediaType(okHttpClient, this.dartHost + "/manifest/v1/upload", ymlManifest, null, "application/x-yaml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(response);
    }


    private Response executeGetRequest(String apiPath, @Nullable Map<String,String> urlParams, @Nullable Map<String,String> queryParams){
        Response response = null;
        try {
            response =  RestClientHelper.getInstance().doGetRequest(okHttpClient, this.dartHost + apiPath, urlParams, queryParams );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}