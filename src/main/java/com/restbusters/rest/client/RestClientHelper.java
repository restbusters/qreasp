package com.restbusters.rest.client;

import com.jayway.jsonpath.JsonPath;
import com.restbusters.rest.model.HttpRestRequest;
import com.restbusters.util.common.GenericUtils;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.net.ssl.*;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.cert.CertificateException;
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
        this.sharedOkHttpClient.newBuilder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build();
    }

    private OkHttpClient buildClientFromShared(Object auth, Map<String, String> headers) {
        return sharedOkHttpClient.newBuilder()
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

    private OkHttpClient buildClientFromSharedWithUserInterceptor(Object auth, Object userDefinedInterceptor) {
        return sharedOkHttpClient.newBuilder()
                .addNetworkInterceptor(new LoggingInterceptor())
                .addInterceptor((Interceptor) auth)
                .addInterceptor((Interceptor) userDefinedInterceptor)
                .build();
    }

    public OkHttpClient buildNoAuthClient() {
        return sharedOkHttpClient.newBuilder()
                .addNetworkInterceptor(new LoggingInterceptor())
                .build();
    }

    public OkHttpClient buildNoAuthClientNoLogging() {
        return sharedOkHttpClient.newBuilder()
                .build();
    }

    public OkHttpClient buildClientWithHeaders(Map<String, String> headers, Long connectTimeout, Long readTimeout, Long writeTimeout) {
//        String hostname = "127.0.0.1";
//        int port = 1080;
//        Proxy proxy = new Proxy(Proxy.Type.HTTP,
//                new InetSocketAddress(hostname, port));
        OkHttpClient.Builder httpClient = sharedOkHttpClient.newBuilder();
        httpClient
                //.proxy(proxy)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .headers(Headers.of(headers));

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
        return httpClient.build();
    }

    public OkHttpClient buildClientWithHeaders(Map<String, String> headers) {
        OkHttpClient.Builder httpClient = sharedOkHttpClient.newBuilder();
        httpClient
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .headers(Headers.of(headers));

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });

        return httpClient.build();
    }


    private OkHttpClient buildClientFromSharedWithBearerInterceptor(Object auth, @Nullable Map<String, String> headers) {
        OkHttpClient.Builder builder = sharedOkHttpClient.newBuilder();
        builder.addNetworkInterceptor(new LoggingInterceptor());
        if(headers != null){
            builder.addInterceptor(
                    chain -> {
                        Request request = chain.request().newBuilder()
                                .headers(Headers.of(headers))
                                .build();
                        return chain.proceed(request);
                    });
        }
        builder.addInterceptor((Interceptor) auth);
        return builder.build();
    }

    private OkHttpClient buildClientFromSharedUserDefinedInterceptor(Object userInterceptor, @Nullable Map<String, String> headers) {
        OkHttpClient.Builder builder = sharedOkHttpClient.newBuilder();
        builder.addNetworkInterceptor(new LoggingInterceptor());
        if(headers != null){
            builder.addInterceptor(
                    chain -> {
                        Request request = chain.request().newBuilder()
                                .headers(Headers.of(headers))
                                .build();
                        return chain.proceed(request);
                    });
        }
        builder.addInterceptor((Interceptor) userInterceptor);
        return builder.build();
    }


    //we can pass headers
    public OkHttpClient buildBasicAuthClient(String userName, String password, Map<String, String> headers) {
        return buildClientFromShared(new BasicAuthInterceptor(userName, password), headers);
    }

    public OkHttpClient buildBasicAuthClientWithCustomInterceptor(String userName, String password, Object userDefinedInterceptor) {
        return buildClientFromSharedWithUserInterceptor(new BasicAuthInterceptor(userName, password), userDefinedInterceptor);
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

    public OkHttpClient buildBearerClient(String token, Map<String, String> headers) throws Exception {
        return buildClientFromSharedWithBearerInterceptor(new BearerAuthInterceptor(token), headers);
    }


    public OkHttpClient buildTrustedHttpClient(Map<String, String> headers) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = sharedOkHttpClient.newBuilder();
            builder.followRedirects(true);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public void addHeaders(OkHttpClient okHttpClient, Map<String, String> headers) {
        okHttpClient.newBuilder()
                .addNetworkInterceptor(new Interceptor() {

                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();

                        Request request = original.newBuilder()
                                .headers(Headers.of(headers))
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

    public OkHttpClient registerLoggerInterceptor(OkHttpClient okHttpClient) {
        return okHttpClient.newBuilder()
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    public OkHttpClient registerBasicAuthInterceptor(OkHttpClient okHttpClient, String userName, String password) {
        return okHttpClient.newBuilder()
                .addInterceptor(new BasicAuthInterceptor(userName, password))
                .build();
    }

    public void registerLoggerInterceptorForSharedClient() {
        this.sharedOkHttpClient = this.sharedOkHttpClient.newBuilder()
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    public OkHttpClient buildOkHttpClient(Map<String, String> headers) {
        OkHttpClient.Builder httpClient = sharedOkHttpClient.newBuilder();
        httpClient
                .connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .headers(Headers.of(headers));

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });

        return httpClient.build();
    }


    public Response executeRequest(OkHttpClient okHttpClient, HttpRestRequest httpRestRequest) throws IOException {
        if(MapUtils.isNotEmpty(httpRestRequest.getHeaders())){
            addHeaders(okHttpClient, httpRestRequest.getHeaders());
        }

        return okHttpClient.newCall(convertToOkHttpRequest(httpRestRequest)).execute();
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

    private RequestBody createRequestBody(String requestBody, @Nullable String contentType) {
        MediaType mediaType = null;
        if (contentType == null) {
            mediaType = JSON;
        } else {
            mediaType = MediaType.get(contentType);
        }
        if (requestBody == null) {
            return RequestBody.create("", mediaType);
        } else {
            return RequestBody.create(requestBody, mediaType);
        }
    }


    private Request convertToOkHttpRequest(HttpRestRequest httpRestRequest) {
        String url = httpRestRequest.getUrl();
        String contentType = httpRestRequest.getContentType();
        if (contentType == null) {
            contentType = "application/json";
        }
        if (StringUtils.isAllBlank(url)) {
            throw new RuntimeException(ConstantsErrors.INVALID_URL);
        }
        if (StringUtils.isAllBlank(httpRestRequest.getHttpMethod())) {
            throw new RuntimeException(ConstantsErrors.HTTP_METHOD_BLANK);
        }
        if (HttpMethods.findByValue(httpRestRequest.getHttpMethod()) == null) {
            throw new RuntimeException(ConstantsErrors.HTTP_METHOD_INVALID);
        }
        if (MapUtils.isNotEmpty(httpRestRequest.getUrlParams())) {
            url = substituteUrlParams(httpRestRequest.getUrl(), httpRestRequest.getUrlParams());
        }
        if (MapUtils.isNotEmpty(httpRestRequest.getQueryParams())) {
            url = addQueryParams(url, httpRestRequest.getQueryParams());
        }
        return buildRequest(url, httpRestRequest.getRequestBody(), httpRestRequest.getHttpMethod(), httpRestRequest.getHeaders(), contentType);

    }

    private Request buildRequest(String url, @Nullable String requestBody, String httpMethod, @Nullable Map<String,String> headers, String... contentTypeParam) {
        String contentType = null;
        if (!httpMethod.equalsIgnoreCase(HttpMethods.GET.getValue())) {
            if (contentTypeParam.length == 1) {
                contentType = contentTypeParam[0];
            } else {
                contentType = "application/json";
            }
        }
        Request.Builder builder = new Request.Builder();
        builder
                .url(url);
        if(!httpMethod.equalsIgnoreCase(HttpMethods.GET.getValue())){
            builder
                    .addHeader("Content-Type", contentType);
        }
        if(headers != null){
            builder.headers(Headers.of(headers));
        }
        if (httpMethod.equalsIgnoreCase(HttpMethods.POST.getValue())) {
            builder.post(this.createRequestBody(requestBody, contentType));
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.PUT.getValue())) {
            builder.put(this.createRequestBody(requestBody, contentType));
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.PATCH.getValue())) {
            builder.patch(this.createRequestBody(requestBody, contentType));
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.DELETE.getValue())) {
            builder.patch(this.createRequestBody(requestBody, contentType));
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.GET.getValue())) {
            builder.get();
        }
        return builder.build();

    }

    public String getOAuth2Token(String url, Map<String, String> formBodyPairs, String jsonPathExtractor) {

        if (MapUtils.isEmpty(formBodyPairs)) {
            return null;
        }
        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : formBodyPairs.entrySet()) {
            requestBodyBuilder.add(entry.getKey(), entry.getValue());
        }

        Request request = new Request.Builder()
                .url(url)
                .method(HttpMethods.POST.getValue(), requestBodyBuilder.build())
                .build();
        Response response = null;
        try {
            response = this.sharedOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                try {
                    String body = response.body().string();
                    String token = JsonPath.read(body, jsonPathExtractor);
                    return token;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
