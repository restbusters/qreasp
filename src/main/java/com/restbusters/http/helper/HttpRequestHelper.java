package com.restbusters.http.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.data.templating.TemplateManager;
import com.restbusters.exception.RecordNotFound;
import com.restbusters.http.helper.model.HttpExecutionResult;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRequest;
import freemarker.template.TemplateException;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * @author alexander matsaylo on 4/14/22
 * @project backend-platform-test-automation
 */

public final class HttpRequestHelper {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static ObjectMapper mapper = GlobalResourceManager.getInstance().getObjectMapper();


    public static HttpExecutionResult executeHttpRequest(OkHttpClient okClient, HttpRequest httpRestRequest) {
        HttpExecutionResult execResult = new HttpExecutionResult();
        Response response;
        try {
            if(httpRestRequest.getRequestBody() != null && !httpRestRequest.getRequestBody().isEmpty()) {
                httpLogger("Request body", httpRestRequest.getRequestBody());
            }
            response = RestClientHelper.getInstance().executeRequest(okClient, httpRestRequest);
        } catch (IOException e) {
            execResult.setError(e.getLocalizedMessage());
            execResult.setStatus(ExecutionState.SKIPPED);
            execResult.setStatus(ExecutionState.SKIPPED);
            return execResult;
        }
        execResult.setStatus(ExecutionState.COMPLETED);
        execResult.setStatusCode(response.code());
        execResult.setSuccessful(response.isSuccessful());
        execResult.setExecutionTime(getDurationMillis(response)); // Store raw milliseconds
        execResult.setConvertedExecutionTime(longToReadableTime(response)); // Store formatted time
        try {
            execResult.setResponseBody(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
            execResult.setError(e.getLocalizedMessage());
            execResult.setStatus(ExecutionState.SKIPPED);
            return execResult;
        }
        return execResult;
    }

    private static String longToReadableTime(Response response){
        long duration = response.receivedResponseAtMillis() - response.sentRequestAtMillis();
        long seconds = duration / 1000;
        long millis = duration % 1000;
        return seconds + " second" + (seconds != 1 ? "s" : "") + " " + millis + " milliseconds";
    }

    /**
     * Returns the duration of the request in milliseconds as a long value for calculations
     * @param response The HTTP response
     * @return The duration in milliseconds as a long
     */
    private static long getDurationMillis(Response response) {
        return response.receivedResponseAtMillis() - response.sentRequestAtMillis();
    }

    private static void httpLogger(String prefix, String body) {
        try {
            logger.info(prefix + " : \n{}", GlobalResourceManager.getInstance().getObjectMapper().readTree( StringEscapeUtils.unescapeJson(body)).toPrettyString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static HttpRequest buildHttpRequestFromTemplate(TemplateManager templateManager, String templateName, String templateVersion, String jsonPayload) throws TemplateException, RecordNotFound, IOException {

        String payload = templateManager.processTemplateWithJsonInput(templateName, templateVersion, jsonPayload);
        return mapper.readValue(payload, HttpRequest.class);
    }

}