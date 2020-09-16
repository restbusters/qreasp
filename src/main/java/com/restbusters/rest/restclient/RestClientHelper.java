package com.restbusters.rest.restclient;

import com.restbusters.util.common.GenericUtils;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author amatsaylo on 9/17/18
 * @project qreasp
 */
public class RestClientHelper {

    private static RestClientHelper instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private OkHttpClient sharedOkHttpClient;
    private MediaType JSON = MediaType.get("application/json; charset=utf-8"); ;

    public String getBearerAuthToken() {
        return bearerAuthToken;
    }


    private String bearerAuthToken;

    private RestClientHelper() {
        createSharedOkHttpClient();
    }

    public static synchronized RestClientHelper getInstance() {
        if (instance == null) {
            instance = new RestClientHelper();
        }
        return instance;
    }

    private void createSharedOkHttpClient() {
        this.sharedOkHttpClient = new OkHttpClient();
    }

    private OkHttpClient buildClientFromShared(Object auth, Map<String, String> headers) {
        return sharedOkHttpClient.newBuilder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .addNetworkInterceptor(new LoggingInterceptor())
                .addNetworkInterceptor(new PostHeaderInterceptor())
                .addInterceptor(
                        chain -> {
                            Request request = chain.request().newBuilder()
                                    .headers(Headers.of(headers))
                                    .build();
                            return chain.proceed(request);
                        })
                .addInterceptor((Interceptor) auth)
                .build();
    }

    public OkHttpClient buildNoAuthClient() {
        return sharedOkHttpClient.newBuilder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .addNetworkInterceptor(new LoggingInterceptor())
                .build();
    }

    private OkHttpClient buildClientFromSharedWithBearerInterceptor(Object auth, Map<String, String> headers) {
        return sharedOkHttpClient.newBuilder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .addNetworkInterceptor(new LoggingInterceptor())
                .addInterceptor(
                        chain -> {
                            Request request = chain.request().newBuilder()
                                    .headers(Headers.of(headers))
                                    .build();
                            return chain.proceed(request);
                        })
                .addInterceptor((Interceptor) auth)
                .build();
    }



    //we can pass headers
    public OkHttpClient getOkHttpClient(String userName, String password, Map<String, String> headers) {
        return buildClientFromShared(new BasicAuthInterceptor(userName, password), headers);
    }

    //we can pass headers
    public OkHttpClient getOkHttpClient(String userName, String password) {
        Map<String, String> headers = new HashMap<>();
        return buildClientFromShared(new BasicAuthInterceptor(userName, password), headers);
    }

    public OkHttpClient getOkHttpClientBearerWithToken(String token) throws Exception {
        Map<String, String> headers = new HashMap<>();
        return buildClientFromSharedWithBearerInterceptor(new BearerAuthInterceptor(token), headers);
    }


    public void addHeader(OkHttpClient okHttpClient, String headerName, String headerValue) {
        okHttpClient.newBuilder()
                .addNetworkInterceptor(new Interceptor() {

                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();

                        Request request = original.newBuilder()
                                .header(headerName, headerValue)
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                }).build();
    }

    public void removeHeader(OkHttpClient okHttpClient, String headerName, String headerValue) {
        okHttpClient.newBuilder()
                .addInterceptor(
                        chain -> {
                            Request request = chain.request().newBuilder()
                                    .header(headerName, headerValue)
                                    .build();
                            return chain.proceed(request);
                        }).build();
    }

    public void registerLoggerInterceptor(OkHttpClient okHttpClient) {
        okHttpClient.newBuilder()
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    public void registerAuthInterceptor(OkHttpClient okHttpClient, String userName, String password) {
        okHttpClient.newBuilder()
                .addInterceptor(new BasicAuthInterceptor(userName, password))
                .build();
    }

    public void registerLoggerInterceptor2() {
        this.sharedOkHttpClient.newBuilder()
                .addInterceptor(new LoggingInterceptor())
                .build();
    }


    public Response doGetRequest(OkHttpClient okHttpClient, String url, @Nullable Map<String,String> urlParams, @Nullable Map<String,String> queryParams) throws IOException {
        if(MapUtils.isNotEmpty(queryParams)){
            url = addQueryParams(url, queryParams);
        }
        Request request = new Request.Builder()
                .url(substituteUrlParams(url, urlParams))
                .build();
        return okHttpClient.newCall(request).execute();

    }

    public Response doPostRequest(OkHttpClient okHttpClient, String url, @Nullable String userDefinedRequestBody, @Nullable Map<String,String> urlParams) throws IOException {
        String body = "";
        RequestBody requestBody = null;
        Request request;
        if(userDefinedRequestBody == null){
            requestBody = RequestBody.create("", null);
        }
        else {
            requestBody = RequestBody.create(JSON, userDefinedRequestBody);
        }
        request = new Request.Builder()
                .url(substituteUrlParams(url, urlParams))
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/x-yaml")
                .post(requestBody)
                .build();
        return okHttpClient.newCall(request).execute();
    }

    public Response doPostRequestWithMediaType(OkHttpClient okHttpClient, String url, @Nullable String userDefinedRequestBody, @Nullable Map<String,String> urlParams, String mediaType) throws IOException {
        String body = "";
        RequestBody requestBody = null;
        Request request;
        if(userDefinedRequestBody == null){
            requestBody = RequestBody.create("", null);
        }
        else {
            requestBody = RequestBody.create(userDefinedRequestBody, MediaType.parse(mediaType));
        }
        request = new Request.Builder()
                .url(substituteUrlParams(url, urlParams))
                .addHeader("Content-Type", mediaType)
                .addHeader("accept", "application/json")
                .post(requestBody)
                .build();
        return okHttpClient.newCall(request).execute();
    }




    public Response doPutRequest(OkHttpClient okHttpClient, String url, String requestBody, @Nullable Map<String,String> urlParams) throws IOException {
        RequestBody body = RequestBody.create(JSON, requestBody);
        Request request = new Request.Builder()
                .url(substituteUrlParams(url, urlParams))
                .put(body)
                .build();
        return okHttpClient.newCall(request).execute();
    }

    public Response doPatchRequest(OkHttpClient okHttpClient, String url, String requestBody, @Nullable Map<String,String> urlParams) throws IOException {
        RequestBody body = RequestBody.create(JSON, requestBody);
        Request request = new Request.Builder()
                .url(substituteUrlParams(url, urlParams))
                .patch(body)
                .build();
        return okHttpClient.newCall(request).execute();
    }

    public Response doDeleteRequest(OkHttpClient okHttpClient, String url, @Nullable String requestBody, @Nullable Map<String,String> urlParams) throws IOException {
        Request request = new Request.Builder()
                .url(substituteUrlParams(url, urlParams))
                .delete(createRequestBody(requestBody))
                .build();
        return okHttpClient.newCall(request).execute();
    }

    private String substituteUrlParams(String url, @Nullable Map<String,String> urlParams){
        if(MapUtils.isNotEmpty(urlParams)){
            return GenericUtils.substituteVariables(url, urlParams);
        }
        return url;
    }

    private RequestBody createRequestBody(@Nullable String requestBody){
        if(StringUtils.isNotBlank(requestBody)){
            return RequestBody.create(JSON, requestBody);
        }
        return null;
    }

    public String addQueryParams(String url, Map<String,String>queryParam) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParam != null) {
            for (Map.Entry<String, String> param : queryParam.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }

        }
        return httpBuilder.build().toString();
    }

}
