package com.restbusters.rest.client;

import com.jayway.jsonpath.JsonPath;
import com.restbusters.rest.model.HttpRequest;
import com.restbusters.util.common.GenericUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.*;
import javax.annotation.Nullable;
import javax.net.ssl.*;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Sasha Matsaylo on 9/17/18
 * @project qreasp
 */
public class RestClientHelper {

    private static RestClientHelper instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private OkHttpClient sharedOkHttpClient;
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private RestClientHelper() {
        createSharedOkHttpClient();
    }

    public static synchronized RestClientHelper getInstance() {
        if (instance == null) {
            instance = new RestClientHelper();
        }
        return instance;
    }

    // BUG FIX: Actually assign the result of the builder
    private void createSharedOkHttpClient() {
        this.sharedOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build();
    }

    // FIX: Use addHeader instead of headers() to preserve existing headers
    private OkHttpClient buildClientFromShared(Object auth, Map<String, String> headers) {
        OkHttpClient.Builder builder = sharedOkHttpClient.newBuilder()
                .addNetworkInterceptor(new LoggingInterceptor())
                .addInterceptor((Interceptor) auth);

        if (MapUtils.isNotEmpty(headers)) {
            builder.addInterceptor(chain -> {
                Request.Builder requestBuilder = chain.request().newBuilder();
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
                return chain.proceed(requestBuilder.build());
            });
        }

        return builder.build();
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
        OkHttpClient.Builder httpClient = sharedOkHttpClient.newBuilder();
        httpClient
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        requestBuilder.addHeader(header.getKey(), header.getValue());
                    }
                    return chain.proceed(requestBuilder.build());
                });
        return httpClient.build();
    }

    public OkHttpClient buildClientWithHeaders(Map<String, String> headers) {
        OkHttpClient.Builder httpClient = sharedOkHttpClient.newBuilder();
        httpClient.addInterceptor(chain -> {
            Request.Builder requestBuilder = chain.request().newBuilder();
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
            return chain.proceed(requestBuilder.build());
        });
        return httpClient.build();
    }

    // FIX: Use addHeader instead of headers() to preserve auth headers
    private OkHttpClient buildClientFromSharedWithBearerInterceptor(Object auth, @Nullable Map<String, String> headers) {
        OkHttpClient.Builder builder = sharedOkHttpClient.newBuilder();
        builder.addNetworkInterceptor(new LoggingInterceptor());
        builder.addInterceptor((Interceptor) auth);

        if (MapUtils.isNotEmpty(headers)) {
            builder.addInterceptor(chain -> {
                Request.Builder requestBuilder = chain.request().newBuilder();
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
                return chain.proceed(requestBuilder.build());
            });
        }

        return builder.build();
    }

    private OkHttpClient buildClientFromSharedUserDefinedInterceptor(Object userInterceptor, @Nullable Map<String, String> headers) {
        OkHttpClient.Builder builder = sharedOkHttpClient.newBuilder();
        builder.addNetworkInterceptor(new LoggingInterceptor());
        builder.addInterceptor((Interceptor) userInterceptor);

        if (MapUtils.isNotEmpty(headers)) {
            builder.addInterceptor(chain -> {
                Request.Builder requestBuilder = chain.request().newBuilder();
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
                return chain.proceed(requestBuilder.build());
            });
        }

        return builder.build();
    }

    public OkHttpClient buildBasicAuthClient(String userName, String password, Map<String, String> headers) {
        return buildClientFromShared(new BasicAuthInterceptor(userName, password), headers);
    }

    public OkHttpClient buildBasicAuthClientWithCustomInterceptor(String userName, String password, Object userDefinedInterceptor) {
        return buildClientFromSharedWithUserInterceptor(new BasicAuthInterceptor(userName, password), userDefinedInterceptor);
    }

    public OkHttpClient buildBearerClientWithCustomInterceptor(String token, Object userDefinedInterceptor) {
        return buildClientFromSharedWithUserInterceptor(new BearerAuthInterceptor(token), userDefinedInterceptor);
    }

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

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = sharedOkHttpClient.newBuilder();
            builder.followRedirects(true);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // NOTE: These methods create a new client but don't return it - keeping for backward compatibility
    // but they don't actually modify the passed client
    public void addHeader(OkHttpClient okHttpClient, String headerName, String headerValue) {
        okHttpClient.newBuilder()
                .addNetworkInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header(headerName, headerValue)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }).build();
    }

    public void addHeaders(OkHttpClient okHttpClient, Map<String, String> headers) {
        okHttpClient.newBuilder()
                .addNetworkInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .headers(Headers.of(headers))
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }).build();
    }

    public void removeHeader(OkHttpClient okHttpClient, String headerName, String headerValue) {
        okHttpClient.newBuilder()
                .addInterceptor(chain -> {
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
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        requestBuilder.addHeader(header.getKey(), header.getValue());
                    }
                    return chain.proceed(requestBuilder.build());
                });
        return httpClient.build();
    }

    public Response executeRequest(OkHttpClient okHttpClient, HttpRequest httpRequest) throws IOException {
        if (MapUtils.isNotEmpty(httpRequest.getHeaders())) {
            addHeaders(okHttpClient, httpRequest.getHeaders());
        }
        return okHttpClient.newCall(convertToOkHttpRequest(httpRequest)).execute();
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
        MediaType mediaType;
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

    private Request convertToOkHttpRequest(HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        if (StringUtils.isAllBlank(url)) {
            throw new RuntimeException(ConstantsErrors.INVALID_URL);
        }
        if (StringUtils.isAllBlank(httpRequest.getHttpMethod())) {
            throw new RuntimeException(ConstantsErrors.HTTP_METHOD_BLANK);
        }
        if (HttpMethods.findByValue(httpRequest.getHttpMethod()) == null) {
            throw new RuntimeException(ConstantsErrors.HTTP_METHOD_INVALID);
        }

        if (MapUtils.isNotEmpty(httpRequest.getUrlParams())) {
            url = substituteUrlParams(httpRequest.getUrl(), httpRequest.getUrlParams());
        }
        if (MapUtils.isNotEmpty(httpRequest.getQueryParams())) {
            url = addQueryParams(url, httpRequest.getQueryParams());
        }

        return buildRequest(url, httpRequest.getRequestBody(), httpRequest.getHttpMethod(),
                httpRequest.getHeaders(), httpRequest.getContentType());
    }

    // FIX: Use addHeader instead of headers() to preserve interceptor headers
    private Request buildRequest(String url, @Nullable String requestBody, String httpMethod,
                                 @Nullable Map<String, String> headers, @Nullable String contentType) {
        if (!httpMethod.equalsIgnoreCase(HttpMethods.GET.getValue())) {
            if (contentType == null && requestBody != null) {
                if (GenericUtils.isJSONValid(requestBody)) {
                    contentType = "application/json";
                }
            }
        }

        Request.Builder builder = new Request.Builder();
        builder.url(url);

        // FIX: Use addHeader instead of headers() to preserve existing headers
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }

        if (!httpMethod.equalsIgnoreCase(HttpMethods.GET.getValue())) {
            if (contentType != null) {
                builder.addHeader("Content-Type", contentType);
            }
        }

        if (httpMethod.equalsIgnoreCase(HttpMethods.POST.getValue())) {
            builder.post(this.createRequestBody(requestBody, contentType));
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.PUT.getValue())) {
            builder.put(this.createRequestBody(requestBody, contentType));
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.PATCH.getValue())) {
            builder.patch(this.createRequestBody(requestBody, contentType));
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.DELETE.getValue())) {
            if (requestBody == null) {
                builder.delete();
            } else {
                builder.delete(this.createRequestBody(requestBody, contentType));
            }
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.GET.getValue())) {
            builder.get();
        }

        return builder.build();
    }

    public String getOAuth2Token(String url, Map<String, String> formBodyPairs, String jsonPathExtractor) {
        if (MapUtils.isEmpty(formBodyPairs)) {
            logger.warn("Form body pairs are empty for OAuth2 token request");
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

        try (Response response = this.sharedOkHttpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                try (ResponseBody responseBody = response.body()) {
                    String body = responseBody.string();
                    return JsonPath.read(body, jsonPathExtractor);
                }
            } else {
                logger.error("OAuth2 token request failed with status: {}", response != null ? response.code() : "null response");
            }
        } catch (IOException e) {
            logger.error("Failed to retrieve OAuth2 token from URL: {}", url, e);
        }

        return null;
    }

    /**
     * Registers any OkHttp interceptor to an existing client
     * @param okHttpClient The existing client
     * @param interceptor The interceptor to register
     * @return A new OkHttpClient with the interceptor added
     */
    public OkHttpClient registerInterceptor(OkHttpClient okHttpClient, Interceptor interceptor) {
        return okHttpClient.newBuilder()
                .addInterceptor(interceptor)
                .build();
    }
}