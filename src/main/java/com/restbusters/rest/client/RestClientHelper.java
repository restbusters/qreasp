package com.restbusters.rest.client;

import com.restbusters.integraton.swagger.model.HttpRestRequest;
import com.restbusters.util.common.GenericUtils;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
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
    private MediaType JSON = MediaType.get("application/json; charset=utf-8");


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

    public OkHttpClient buildNoAuthClient(Map<String, String> headers) {
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
    public OkHttpClient buildBasicAuthClient(String userName, String password, Map<String, String> headers) {
        return buildClientFromShared(new BasicAuthInterceptor(userName, password), headers);
    }

    //we can pass headers
    public OkHttpClient buildBasicAuthClient(String userName, String password) {
        Map<String, String> headers = new HashMap<>();
        return buildClientFromShared(new BasicAuthInterceptor(userName, password), headers);
    }

    public OkHttpClient buildBearerClient(String token) throws Exception {
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


    public Response doGetRequest(OkHttpClient okHttpClient, String url, @Nullable Map<String, String> urlParams, @Nullable Map<String, String> queryParams) throws IOException {

        return okHttpClient.newCall(buildGetRequest(url, urlParams, queryParams)).execute();

    }

    public Response doGetRequest(OkHttpClient okHttpClient, HttpRestRequest httpRestRequest) throws IOException {

        return okHttpClient.newCall(buildGetRequest(httpRestRequest.getUrl(), httpRestRequest.getUrlParams(), httpRestRequest.getQueryParams())).execute();
    }

    public Response doPostRequest(OkHttpClient okHttpClient, String url, @Nullable String userDefinedRequestBody, @Nullable Map<String, String> urlParams) throws IOException {

        return okHttpClient.newCall(buildPostRequest(substituteUrlParams(url, urlParams), userDefinedRequestBody)).execute();
    }

    public Response doPostRequest(OkHttpClient okHttpClient, HttpRestRequest httpRestRequest, @Nullable Map<String, String> urlParams) throws IOException {

        return okHttpClient.newCall(buildPostRequest(substituteUrlParams(httpRestRequest.getUrl(), urlParams), httpRestRequest.getRequestBody())).execute();
    }

    public Response doPostRequestWithMediaType(OkHttpClient okHttpClient, String url, @Nullable String userDefinedRequestBody, @Nullable Map<String, String> urlParams, String mediaType) throws IOException {

        return okHttpClient.newCall(buildPostRequest(substituteUrlParams(url, urlParams), userDefinedRequestBody, mediaType)).execute();
    }


    public Response doPutRequest(OkHttpClient okHttpClient, String url, String requestBody, @Nullable Map<String, String> urlParams) throws IOException {

        return okHttpClient.newCall(buildPutRequest(substituteUrlParams(url, urlParams), requestBody)).execute();
    }

    public Response doPutRequest(OkHttpClient okHttpClient, HttpRestRequest httpRestRequest, @Nullable Map<String, String> urlParams) throws IOException {

        return okHttpClient.newCall(buildPutRequest(substituteUrlParams(httpRestRequest.getUrl(), urlParams), httpRestRequest.getRequestBody())).execute();
    }

    public Response doPatchRequest(OkHttpClient okHttpClient, String url, String requestBody, @Nullable Map<String, String> urlParams) throws IOException {

        return okHttpClient.newCall(buildPatchRequest(substituteUrlParams(url, urlParams), requestBody)).execute();
    }

    public Response doPatchRequest(OkHttpClient okHttpClient, HttpRestRequest httpRestRequest, @Nullable Map<String, String> urlParams) throws IOException {

        return okHttpClient.newCall(buildPatchRequest(substituteUrlParams(httpRestRequest.getUrl(), urlParams), httpRestRequest.getRequestBody())).execute();
    }

    public Response doDeleteRequest(OkHttpClient okHttpClient, String url, @Nullable String requestBody, @Nullable Map<String, String> urlParams) throws IOException {

        return okHttpClient.newCall(buildPatchRequest(substituteUrlParams(url, urlParams), requestBody)).execute();
    }

    public Response doDeleteRequest(OkHttpClient okHttpClient, HttpRestRequest httpRestRequest, @Nullable Map<String, String> urlParams) throws IOException {

        return okHttpClient.newCall(buildDeleteRequest(substituteUrlParams(httpRestRequest.getUrl(), urlParams), httpRestRequest.getRequestBody())).execute();
    }

    private String substituteUrlParams(String url, @Nullable Map<String, String> urlParams) {
        if (MapUtils.isNotEmpty(urlParams)) {
            return GenericUtils.substituteVariables(url, urlParams);
        }
        return url;
    }


    public String addQueryParams(String url, Map<String, String> queryParam) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParam != null) {
            for (Map.Entry<String, String> param : queryParam.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }

        }
        return httpBuilder.build().toString();
    }

    private RequestBody createJsonRequestBody(String requestBody) {
        if (requestBody == null) {
            return RequestBody.create("", null);
        } else {
            return RequestBody.create(JSON, requestBody);
        }
    }

    private Request buildPostRequest(String url, String requestBody) {
        return new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(this.createJsonRequestBody(requestBody))
                .build();
    }

    private Request buildPutRequest(String url, String requestBody) {
        return new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .put(this.createJsonRequestBody(requestBody))
                .build();
    }

    private Request buildPostRequest(String url, String requestBody, String mediaType) {
        return new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", mediaType)
                .post(this.createJsonRequestBody(requestBody))
                .build();
    }

    private Request buildPatchRequest(String url, String requestBody) {
        return new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .patch(this.createJsonRequestBody(requestBody))
                .build();
    }

    private Request buildDeleteRequest(String url, String requestBody) {
        return new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .delete(this.createJsonRequestBody(requestBody))
                .build();
    }

    private Request buildGetRequest(String url, Map<String, String> urlParams, Map<String, String> queryParams ){
        if (MapUtils.isNotEmpty(queryParams)) {
            url = addQueryParams(url, queryParams);
        }
        return new Request.Builder()
                .url(substituteUrlParams(url, urlParams))
                .build();
    }

}
