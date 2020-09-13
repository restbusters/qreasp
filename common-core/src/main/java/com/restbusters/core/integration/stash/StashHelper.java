package com.restbusters.core.integration.stash;

import com.restbusters.rest.util.restclient.RestClientHelper;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;

/**
 * @author Sasha matsaylo on 2020-09-10
 * @project qreasp
 */
public class StashHelper {

    private static StashHelper instance;
    private OkHttpClient okHttpClient;
    private String url;
    private String token;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());



    private StashHelper() throws Exception {
    }

    public static synchronized StashHelper getInstance() throws Exception {
        if (instance == null) {
            instance = new StashHelper();
        }
        return instance;
    }

    public void init(String url, String token) throws Exception {
        this.url = url;
        this.token = token;
        this.okHttpClient = RestClientHelper.getInstance().getOkHttpClientBearerWithToken(this.token);
    }

    public Optional<Response> getFileContent(String url, @Nullable Map<String, String> urlParams,
                                             @Nullable Map<String, String> queryParams){
        Optional<Response> response = Optional.empty();
        try {
            response = Optional.ofNullable(RestClientHelper.getInstance().doGetRequest(okHttpClient, url, urlParams, queryParams));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}