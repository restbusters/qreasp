package com.restbusters.integraton.swagger;

import com.jayway.jsonpath.JsonPath;
import com.restbusters.exception.ScenarioExecutionException;
import com.restbusters.integraton.swagger.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sasha Matsaylo on 2020-11-26
 * @project qreasp
 */
public class ApiScenarioManager {

    private SwaggerManager swaggerManager;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public ApiScenarioManager(SwaggerManager swaggerManager) throws ScenarioExecutionException {
        if (swaggerManager == null || swaggerManager.getSwaggerDescriptor() == null ||
                swaggerManager.getSwaggerDescriptor().size() < 1) {
            throw new ScenarioExecutionException("Swagger Descriptors are not set in Swagger Manager");
        }
        this.swaggerManager = swaggerManager;
    }

    public List<ApiScenario> scenarioExecutor(List<ApiScenario> apiScenariosList) throws ScenarioExecutionException {

        if (apiScenariosList == null || apiScenariosList.size() < 1) {
            throw new ScenarioExecutionException("List of scenarios is not set or empty");
        }

        Collections.synchronizedList(apiScenariosList).stream().parallel().forEach(apiScenario -> {

            apiScenario.setState(ApiScenarioState.STARTED.name());
            for (ApiStep apiStep : apiScenario.getApiSteps()) {
                //set request Body
                String requestBody;
                Map<String, Object> payloadForTemplate = new HashMap<>();
                if (CollectionUtils.isNotEmpty(apiStep.getSubstitutionRules()) || apiStep.getSubstitutionRules().size() > 0) {
                    logger.info("Starting processing sub list for step {}", apiStep.getOperationId());
                    for (SubstitutionRule sr : apiStep.getSubstitutionRules()) {
                        if (sr.getValueType().equalsIgnoreCase(InstructionType.USER_PROVIDED.name())) {
                            if (MapUtils.isNotEmpty(sr.getUserProvided())) {
                                logger.info("Found User Defined map for apiStep {}", apiStep.getOperationId());
                                sr.getUserProvided().forEach(payloadForTemplate::putIfAbsent);
                            } else {
                                logger.warn("User Defined map is not provided");
                            }
                        } else if (sr.getValueType().equalsIgnoreCase(InstructionType.FROM_RESPONSE.name())) {
                            String responseBody = findResponseBody(apiScenario, apiStep.getApiTitle(), sr.getOperationId());
                            if (responseBody == null) {
                                apiScenario.setState(ApiScenarioState.ABORTED.name());
                                apiScenario.setErrorCode("Failed to build request body for step:" + apiStep.getOperationId());
                                break;
                            }
                            Object result = JsonPath.read(responseBody, sr.getJsonPath());
                            payloadForTemplate.putIfAbsent(sr.getTemplateValue(), result);
                        }
                    }
                    //now after payload map is filed out execute step
                    requestBody = swaggerManager.getPayload(apiStep.getApiTitle(), apiStep.getOperationId(),
                            apiStep.getPayLoadType(), payloadForTemplate);
                    if (requestBody == null) {
                        apiScenario.setState(ApiScenarioState.ABORTED.name());
                        apiScenario.setErrorCode("Failed to build request body for step:" + apiStep.getOperationId());
                        break;
                    }
                    apiStep.getHttpRestRequest().setRequestBody(requestBody);
                    HttpRestResponse httpRestResponse = swaggerManager.executeApiStep(apiStep);
                    apiStep.setHttpRestResponse(httpRestResponse);
                    apiScenario.setState(ApiScenarioState.FINISHED.name());
                } else {
                    apiScenario.setState(ApiScenarioState.ABORTED.name());
                    logger.info("Substitution list is null");
                }
            }

        });
        return apiScenariosList;


    }


    public String findResponseBody(ApiScenario apiScenario, String apiTitle, String operationId) {
        ApiStep apiStep = apiScenario.getApiSteps().stream()
                .filter(astep -> astep.getApiTitle().equalsIgnoreCase(apiTitle) && astep.getOperationId().equalsIgnoreCase(operationId))
                .findFirst()
                .orElse(null);
        if (apiStep == null) {
            return null;
        }
        String responseBody = apiStep.getHttpRestResponse().getResponseBody();
        if (responseBody == null) {
            return null;
        }
        return responseBody;
    }
}