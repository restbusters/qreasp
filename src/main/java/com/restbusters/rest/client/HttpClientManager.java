package com.restbusters.rest.client;

import com.jayway.jsonpath.JsonPath;
import com.restbusters.rest.model.HttpRequest;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP Client Manager for creating and managing OkHttpClient instances with various configurations.
 * This class manages multiple client instances and executes HttpRequest objects with any HTTP client.
 *
 * Usage:
 * <pre>
 * HttpClientManager manager = new HttpClientManager();
 * OkHttpClient client = manager.createBearerClient(token);
 * Response response = manager.executeRequest(client, httpRequest);
 * </pre>
 * @project qreasp
 * @author Sasha Matsaylo
 */
public class HttpClientManager {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient baseClient;
    private final long defaultConnectTimeout;
    private final long defaultReadTimeout;
    private final long defaultWriteTimeout;

    /**
     * Creates an HttpClientManager with default timeouts (180 seconds)
     */
    public HttpClientManager() {
        this(180L, 180L, 180L);
    }

    /**
     * Creates an HttpClientManager with custom timeouts
     *
     * @param connectTimeout Connection timeout in seconds
     * @param readTimeout Read timeout in seconds
     * @param writeTimeout Write timeout in seconds
     */
    public HttpClientManager(long connectTimeout, long readTimeout, long writeTimeout) {
        this.defaultConnectTimeout = connectTimeout;
        this.defaultReadTimeout = readTimeout;
        this.defaultWriteTimeout = writeTimeout;
        this.baseClient = createBaseClient();
    }

    private OkHttpClient createBaseClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(defaultConnectTimeout, TimeUnit.SECONDS)
                .writeTimeout(defaultWriteTimeout, TimeUnit.SECONDS)
                .readTimeout(defaultReadTimeout, TimeUnit.SECONDS)
                .build();
    }

    // ==================== Client Creation Methods ====================

    /**
     * Creates a basic client with no authentication
     */
    public OkHttpClient createClient() {
        return baseClient.newBuilder()
                .addNetworkInterceptor(new LoggingInterceptor())
                .build();
    }

    /**
     * Creates a client without logging interceptor
     */
    public OkHttpClient createClientNoLogging() {
        return baseClient.newBuilder().build();
    }

    /**
     * Creates a client with custom headers
     */
    public OkHttpClient createClientWithHeaders(Map<String, String> headers) {
        if (MapUtils.isEmpty(headers)) {
            return createClient();
        }

        return baseClient.newBuilder()
                .addNetworkInterceptor(new LoggingInterceptor())
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        requestBuilder.addHeader(header.getKey(), header.getValue());
                    }
                    return chain.proceed(requestBuilder.build());
                })
                .build();
    }

    /**
     * Creates a client with custom headers and timeouts
     */
    public OkHttpClient createClientWithHeaders(Map<String, String> headers,
                                                long connectTimeout,
                                                long readTimeout,
                                                long writeTimeout) {
        if (MapUtils.isEmpty(headers)) {
            return createClient();
        }

        return baseClient.newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .addNetworkInterceptor(new LoggingInterceptor())
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        requestBuilder.addHeader(header.getKey(), header.getValue());
                    }
                    return chain.proceed(requestBuilder.build());
                })
                .build();
    }

    /**
     * Creates a client with Basic Authentication
     */
    public OkHttpClient createBasicAuthClient(String username, String password) {
        return createBasicAuthClient(username, password, null);
    }

    /**
     * Creates a client with Basic Authentication and custom headers
     */
    public OkHttpClient createBasicAuthClient(String username, String password,
                                              @Nullable Map<String, String> headers) {
        OkHttpClient.Builder builder = baseClient.newBuilder()
                .addNetworkInterceptor(new LoggingInterceptor());

        // Add basic auth interceptor first
        builder.addInterceptor(new BasicAuthInterceptor(username, password));

        // Add headers interceptor that preserves existing headers
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

    /**
     * Creates a client with Bearer token authentication
     */
    public OkHttpClient createBearerClient(String token) {
        return createBearerClient(token, null);
    }

    /**
     * Creates a client with Bearer token authentication and custom headers
     */
    public OkHttpClient createBearerClient(String token, @Nullable Map<String, String> headers) {
        OkHttpClient.Builder builder = baseClient.newBuilder()
                .addNetworkInterceptor(new LoggingInterceptor());

        // Add bearer auth interceptor first
        builder.addInterceptor(new BearerAuthInterceptor(token));

        // Add headers interceptor that preserves existing headers
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

    /**
     * Creates a client with custom interceptor
     */
    public OkHttpClient createClientWithInterceptor(Interceptor interceptor) {
        return createClientWithInterceptor(interceptor, null);
    }

    /**
     * Creates a client with custom interceptor and headers
     */
    public OkHttpClient createClientWithInterceptor(Interceptor interceptor,
                                                    @Nullable Map<String, String> headers) {
        OkHttpClient.Builder builder = baseClient.newBuilder()
                .addNetworkInterceptor(new LoggingInterceptor())
                .addInterceptor(interceptor);

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

    /**
     * Creates a client that trusts all SSL certificates (use with caution!)
     */
    public OkHttpClient createTrustedClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
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

            return baseClient.newBuilder()
                    .followRedirects(true)
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create trusted SSL client", e);
        }
    }

    /**
     * Adds an interceptor to an existing client and returns a new client
     */
    public OkHttpClient addInterceptor(OkHttpClient client, Interceptor interceptor) {
        return client.newBuilder()
                .addInterceptor(interceptor)
                .build();
    }

    /**
     * Adds a logging interceptor to an existing client and returns a new client
     */
    public OkHttpClient addLoggingInterceptor(OkHttpClient client) {
        return client.newBuilder()
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    // ==================== Request Execution Methods ====================

    /**
     * Executes an HttpRequest using the provided client
     *
     * @param client The OkHttpClient to use for execution
     * @param httpRequest The request metadata
     * @return The HTTP response
     * @throws IOException if the request fails
     */
    public Response executeRequest(OkHttpClient client, HttpRequest httpRequest) throws IOException {
        Request okHttpRequest = buildOkHttpRequest(httpRequest);
        return client.newCall(okHttpRequest).execute();
    }

    /**
     * Builds an OkHttp Request from HttpRequest metadata
     */
    private Request buildOkHttpRequest(HttpRequest httpRequest) {
        validateHttpRequest(httpRequest);

        String url = buildUrl(httpRequest);
        String method = httpRequest.getHttpMethod();
        String requestBody = httpRequest.getRequestBody();
        String contentType = httpRequest.getContentType();
        Map<String, String> headers = httpRequest.getHeaders();

        return buildRequest(url, requestBody, method, headers, contentType);
    }

    private void validateHttpRequest(HttpRequest httpRequest) {
        if (StringUtils.isBlank(httpRequest.getUrl())) {
            throw new IllegalArgumentException("URL cannot be blank");
        }
        if (StringUtils.isBlank(httpRequest.getHttpMethod())) {
            throw new IllegalArgumentException("HTTP method cannot be blank");
        }
        if (HttpMethods.findByValue(httpRequest.getHttpMethod()) == null) {
            throw new IllegalArgumentException("Invalid HTTP method: " + httpRequest.getHttpMethod());
        }
    }

    private String buildUrl(HttpRequest httpRequest) {
        String url = httpRequest.getUrl();

        // Substitute URL parameters (e.g., /users/{id})
        if (MapUtils.isNotEmpty(httpRequest.getUrlParams())) {
            url = GenericUtils.substituteVariables(url, httpRequest.getUrlParams());
        }

        // Add query parameters
        if (MapUtils.isNotEmpty(httpRequest.getQueryParams())) {
            url = addQueryParams(url, httpRequest.getQueryParams());
        }

        return url;
    }

    private String addQueryParams(String url, Map<String, String> queryParams) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            httpBuilder.addQueryParameter(param.getKey(), param.getValue());
        }
        return httpBuilder.build().toString();
    }

    private Request buildRequest(String url, @Nullable String requestBody, String httpMethod,
                                 @Nullable Map<String, String> headers, @Nullable String contentType) {
        // Auto-detect JSON content type if not specified
        if (!httpMethod.equalsIgnoreCase(HttpMethods.GET.getValue())) {
            if (contentType == null && requestBody != null && GenericUtils.isJSONValid(requestBody)) {
                contentType = "application/json";
            }
        }

        Request.Builder builder = new Request.Builder().url(url);

        // Add headers individually to avoid overwriting interceptor headers
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }

        // Add Content-Type header for non-GET requests
        if (!httpMethod.equalsIgnoreCase(HttpMethods.GET.getValue()) && contentType != null) {
            builder.addHeader("Content-Type", contentType);
        }

        // Set HTTP method and body
        RequestBody body = createRequestBody(requestBody, contentType);

        if (httpMethod.equalsIgnoreCase(HttpMethods.POST.getValue())) {
            builder.post(body);
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.PUT.getValue())) {
            builder.put(body);
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.PATCH.getValue())) {
            builder.patch(body);
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.DELETE.getValue())) {
            if (requestBody == null) {
                builder.delete();
            } else {
                builder.delete(body);
            }
        } else if (httpMethod.equalsIgnoreCase(HttpMethods.GET.getValue())) {
            builder.get();
        }

        return builder.build();
    }

    private RequestBody createRequestBody(@Nullable String requestBody, @Nullable String contentType) {
        MediaType mediaType = (contentType == null) ? JSON : MediaType.get(contentType);
        String body = (requestBody == null) ? "" : requestBody;
        return RequestBody.create(body, mediaType);
    }

    // ==================== OAuth2 Helper ====================

    /**
     * Retrieves an OAuth2 token using form body authentication
     *
     * @param url The token endpoint URL
     * @param formBodyPairs Form body key-value pairs (e.g., grant_type, client_id, etc.)
     * @param jsonPathExtractor JsonPath expression to extract the token (e.g., "$.access_token")
     * @return The extracted token, or null if failed
     */
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
                .post(requestBodyBuilder.build())
                .build();

        try (Response response = baseClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                try (ResponseBody responseBody = response.body()) {
                    String body = responseBody.string();
                    return JsonPath.read(body, jsonPathExtractor);
                }
            } else {
                logger.error("OAuth2 token request failed with status: {}", response.code());
            }
        } catch (IOException e) {
            logger.error("Failed to retrieve OAuth2 token", e);
        }

        return null;
    }

    // ==================== Getters for Base Configuration ====================

    public OkHttpClient getBaseClient() {
        return baseClient;
    }

    public long getDefaultConnectTimeout() {
        return defaultConnectTimeout;
    }

    public long getDefaultReadTimeout() {
        return defaultReadTimeout;
    }

    public long getDefaultWriteTimeout() {
        return defaultWriteTimeout;
    }
}