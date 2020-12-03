package com.restbusters.integraton.swagger;

import com.restbusters.exception.ScenarioExecutionException;
import com.restbusters.integraton.swagger.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

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
                swaggerManager.getSwaggerDescriptor().size() < 1){
            throw new ScenarioExecutionException("Swagger Descriptors are not set in Swagger Manager");
        }
            this.swaggerManager = swaggerManager;
    }

    public List<ApiScenario> scenarioExecutor(List<ApiScenario> apiScenariosList) throws ScenarioExecutionException{

        if (apiScenariosList == null || apiScenariosList.size() < 1) {
            throw new ScenarioExecutionException("List of scenarios is not set or empty");
        }

        Collections.synchronizedList(apiScenariosList).stream().parallel().forEach(apiScenarios -> {

            for (ApiScenarioStep ass : apiScenarios.getApiScenarioSteps()) {
                //set request Body
                String requestBody;
                if (ass.getSubstitutionRules().getValueType().equalsIgnoreCase(InstructionType.USER_DEFINED.name())) {
                    if (ass.getSubstitutionRules().getInstruction().getUserDefined() != null) {
                        requestBody = swaggerManager.getPayload(ass.getHttpRestRequest().getApiTitle(), ass.getHttpRestRequest().getOperationId(),
                                ass.getSubstitutionRules().getPayLoadType(), ass.getSubstitutionRules().getInstruction().getUserDefined());
                        ass.getHttpRestRequest().setRequestBody(requestBody);
                        HttpRestResponse httpRestResponse = swaggerManager.executeSwaggerEndPoint(ass.getHttpRestRequest());
                        ass.setHttpRestResponse(httpRestResponse);
                    }

                }
            }

        });
        return apiScenariosList;


    }
}