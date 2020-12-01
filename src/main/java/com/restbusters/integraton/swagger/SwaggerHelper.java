package com.restbusters.integraton.swagger;

import com.restbusters.integraton.swagger.model.OperationParameters;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.Operation;
import io.swagger.oas.models.PathItem;
import io.swagger.oas.models.Paths;
import io.swagger.oas.models.parameters.Parameter;
import io.swagger.parser.v3.OpenAPIV3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import v2.io.swagger.models.HttpMethod;
import v2.io.swagger.parser.SwaggerException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import io.swagger.oas.models.OpenAPI;
//import io.swagger.oas.models.Operation;
//import io.swagger.oas.models.PathItem;
//import io.swagger.oas.models.Paths;
//import io.swagger.oas.models.parameters.Parameter;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.Operation;
//import io.swagger.v3.oas.models.PathItem;
//import io.swagger.v3.oas.models.Paths;
//import io.swagger.v3.oas.models.parameters.Parameter;
//import io.swagger.v3.parser.OpenAPIV3Parser;


/**
 * @author Sasha matsaylo on 2020-09-10
 * @project qreasp
 */
public class SwaggerHelper {

    private static SwaggerHelper instance;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private SwaggerHelper() {
    }

    public static synchronized SwaggerHelper getInstance() {
        if (instance == null) {
            instance = new SwaggerHelper();
        }
        return instance;
    }


    private List<com.restbusters.integration.swagger.model.SwaggerApiResource> buildSwaggerResources(OpenAPI openAPI) {
        List<com.restbusters.integration.swagger.model.SwaggerApiResource> apiResourceList = new ArrayList<>();
        Paths path = openAPI.getPaths();
        String serverUrl = openAPI.getServers().get(0).getUrl();
        path.entrySet().forEach(entry -> {
            PathItem pathItem = entry.getValue();
            String resourcePath = serverUrl + entry.getKey();
            if (pathItem.getGet() != null) {
                apiResourceList.add(createSwaggerApiResource(pathItem.getGet(), resourcePath, HttpMethod.GET.name()));
            }
            if (pathItem.getPost() != null) {
                apiResourceList.add(createSwaggerApiResource(pathItem.getPost(), resourcePath, HttpMethod.POST.name()));
            }
            if (pathItem.getPut() != null) {
                apiResourceList.add(createSwaggerApiResource(pathItem.getPut(), resourcePath, HttpMethod.PUT.name()));
            }
            if (pathItem.getPatch() != null) {
                apiResourceList.add(createSwaggerApiResource(pathItem.getPatch(), resourcePath, HttpMethod.PATCH.name()));
            }
            if (pathItem.getDelete() != null) {
                apiResourceList.add(createSwaggerApiResource(pathItem.getDelete(), resourcePath, HttpMethod.DELETE.name()));
            }

        });
        return apiResourceList;
    }

    public SwaggerDescriptor getSwaggerDescriptor(String url) {
        OpenAPI openAPI = null;
        openAPI = new OpenAPIV3Parser().read(url);
        //openAPI = new OpenAPIV3Parser().readContents(body, null, null).getOpenAPI();
        if(openAPI == null){
            throw new SwaggerException("Failed to build OpenApi");
        }

        SwaggerDescriptor swaggerDescriptor = new SwaggerDescriptor();
        swaggerDescriptor.setApiTitle(openAPI.getInfo().getTitle());
        swaggerDescriptor.setServerUrl(openAPI.getServers().get(0).getUrl());
        swaggerDescriptor.setApiVersion(openAPI.getOpenapi());
        swaggerDescriptor.setSwaggerApiResources(buildSwaggerResources(openAPI));
        return swaggerDescriptor;


    }

    public List<SwaggerDescriptor> getSwaggerApiResources(List<String> swaggerUrls) {
        List<SwaggerDescriptor> swaggerDescriptors = new ArrayList<>();
        swaggerUrls.stream().parallel().forEach(url -> {
            SwaggerDescriptor swaggerDescriptor = null;
            try {
                swaggerDescriptors.add(getSwaggerDescriptor(url));
            } catch (Exception e) {
                logger.error("Failed to obtains swagger resource for url: {}", url);
                e.printStackTrace();
            }
        });
        return swaggerDescriptors;

    }


    private com.restbusters.integration.swagger.model.SwaggerApiResource createSwaggerApiResource(Operation operation, String resourcePath, String httpVerb) {
        com.restbusters.integration.swagger.model.SwaggerApiResource apiResource = new com.restbusters.integration.swagger.model.SwaggerApiResource();
        apiResource.setResourcePath(resourcePath);
        apiResource.setHttpMethod(httpVerb);
        if (operation.getOperationId() != null) {
            apiResource.setOperationId(operation.getOperationId());
        }
        if (operation.getParameters() != null) {
            apiResource.setOperationParameters(setOperationParameter(operation.getParameters()));
        }
        return apiResource;
    }

    private List<OperationParameters> setOperationParameter(List<Parameter> parameters) {
        List<OperationParameters> operationParametersList = new ArrayList<>();
        Collections.synchronizedList(parameters).stream().parallel().forEach(
                parameter -> {
                    OperationParameters operationParameters = new OperationParameters();
                    operationParameters.setDescription(parameter.getDescription());
                    operationParameters.setIn(parameter.getIn());
                    operationParameters.setName(parameter.getName());
                    if (!StringUtils.isEmpty(parameter.getRequired())) {
                        operationParameters.setRequired(parameter.getRequired());
                    }
                    operationParametersList.add(operationParameters);
                });
        return operationParametersList;
    }
}