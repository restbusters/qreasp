package com.restbusters.integraton.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.restbusters.integraton.swagger.model.OperationParameters;
import com.restbusters.integraton.swagger.model.SwaggerApiResource;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import io.swagger.oas.inflector.examples.ExampleBuilder;
import io.swagger.oas.inflector.examples.models.Example;
import io.swagger.oas.inflector.processors.JsonNodeExampleSerializer;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import v2.io.swagger.models.HttpMethod;
import v2.io.swagger.parser.SwaggerException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author Sasha matsaylo on 2020-09-10
 * @project qreasp
 */
public class OpenApiHelper {

    private static OpenApiHelper instance;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private OpenApiHelper() {
    }

    public static synchronized OpenApiHelper getInstance() {
        if (instance == null) {
            instance = new OpenApiHelper();
        }
        return instance;
    }


    private List<SwaggerApiResource> buildSwaggerResources(OpenAPI openAPI) {
        List<SwaggerApiResource> apiResourceList = new ArrayList<>();
        Paths path = openAPI.getPaths();
        Map<String, Schema> schemas = openAPI.getComponents().getSchemas();
        String serverUrl = openAPI.getServers().get(0).getUrl();
        path.entrySet().forEach(entry -> {
            PathItem pathItem = entry.getValue();
            String resourcePath = serverUrl + entry.getKey();
            if (pathItem.getGet() != null) {
                apiResourceList.add(createSwaggerApiResource(pathItem.getGet(), resourcePath, HttpMethod.GET.name()));
            }
            if (pathItem.getPost() != null) {
                Operation postOperation = pathItem.getPost();
                SwaggerApiResource postSwaggerApiResource = createSwaggerApiResource(postOperation, resourcePath, HttpMethod.POST.name());
                apiResourceList.add(setSwaggerApiResource(schemas, postSwaggerApiResource, postOperation));
            }
            if (pathItem.getPut() != null) {
                Operation putOperation = pathItem.getPut();
                SwaggerApiResource putSwaggerApiResource = createSwaggerApiResource(putOperation, resourcePath, HttpMethod.PUT.name());
                apiResourceList.add(setSwaggerApiResource(schemas, putSwaggerApiResource, putOperation));
            }
            if (pathItem.getPatch() != null) {
                Operation patchOperation = pathItem.getPatch();
                SwaggerApiResource patchSwaggerApiResource = createSwaggerApiResource(patchOperation, resourcePath, HttpMethod.PATCH.name());
                apiResourceList.add(setSwaggerApiResource(schemas, patchSwaggerApiResource, patchOperation));
            }
            if (pathItem.getDelete() != null) {
                Operation deleteOperation = pathItem.getDelete();
                SwaggerApiResource deleteSwaggerApiResource = createSwaggerApiResource(deleteOperation, resourcePath, HttpMethod.DELETE.name());
                apiResourceList.add(setSwaggerApiResource(schemas, deleteSwaggerApiResource, deleteOperation));
            }

        });
        return apiResourceList;
    }

    private SwaggerApiResource setSwaggerApiResource(Map<String, Schema> schemas, SwaggerApiResource swaggerApiResource, Operation operation){
        if(schemas != null){
            if(operation.getRequestBody() != null){
                String requestBody = buildRequestBody(schemas, operation);
                if(requestBody != null){
                    swaggerApiResource.setBody(requestBody);
                }
            }
            else {
                logger.warn("Request body not set");
                swaggerApiResource.setBody("Body has not been set");
            }
        }
        return swaggerApiResource;
    }

    public SwaggerDescriptor getSwaggerDescriptorFromUrl(String swaggerUrl) {
        io.swagger.v3.oas.models.OpenAPI openAPI = new OpenAPIV3Parser().read(swaggerUrl);
        if(openAPI == null){
            throw new SwaggerException("Failed to build openapi");
        }

        SwaggerDescriptor swaggerDescriptor = new SwaggerDescriptor();
        swaggerDescriptor.setApiTitle(openAPI.getInfo().getTitle());
        swaggerDescriptor.setServerUrl(openAPI.getServers().get(0).getUrl());
        swaggerDescriptor.setSwaggerApiResources(buildSwaggerResources(openAPI));
        return swaggerDescriptor;


    }

    public SwaggerDescriptor getSwaggerDescriptor(String swaggerContent) {
        io.swagger.v3.oas.models.OpenAPI openAPI = new OpenAPIV3Parser().readContents(swaggerContent, null, null).getOpenAPI();
        if(openAPI == null){
            throw new SwaggerException("Failed to build openapi");
        }

        SwaggerDescriptor swaggerDescriptor = new SwaggerDescriptor();
        swaggerDescriptor.setApiTitle(openAPI.getInfo().getTitle());
        swaggerDescriptor.setServerUrl(openAPI.getServers().get(0).getUrl());
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


    private SwaggerApiResource createSwaggerApiResource(Operation operation, String resourcePath, String httpVerb) {
        SwaggerApiResource apiResource = new SwaggerApiResource();
        apiResource.setResourcePath(resourcePath);
        apiResource.setHttpMethod(httpVerb);
        if (operation.getOperationId() != null) {
            apiResource.setOperationId(operation.getOperationId());
        }
        if (operation.getParameters() != null) {
            apiResource.setOperationParameters(setOperationParameter(operation.getParameters()));
        }
        if (!StringUtils.isEmpty(operation.getSummary())) {
            apiResource.setSummary(operation.getOperationId());
        }
        if (!StringUtils.isEmpty(operation.getDescription())) {
            apiResource.setDescription(operation.getDescription());
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

    private String buildRequestBody(Map<String, Schema> schemas, Operation operation){
        if(operation.getRequestBody() != null){
            Schema model = buildSchema(schemas, operation);
            if(model != null){
                Example example = ExampleBuilder.fromSchema(model, schemas);
                SimpleModule simpleModule = new SimpleModule().addSerializer(new JsonNodeExampleSerializer());
                Json.mapper().registerModule(simpleModule);
                try {
                    return Json.mapper().writeValueAsString(example);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            else {
                //here we can provide predefined schema
                return "our predined schema";
            }
        }
        else {
            return null;
        }
    }

    private Schema buildSchema(Map<String, Schema> schemas, Operation operation){
        if(operation.getRequestBody().get$ref() != null){
            return schemas.get(operation.getRequestBody().get$ref().split("/")[operation.getRequestBody().get$ref().split("/").length-1]);
        }
        if(operation.getRequestBody().getContent() != null){
            String contentKey = operation.getRequestBody().getContent().keySet().toArray()[0].toString();
            if(contentKey != null){
                if(operation.getRequestBody().getContent().get(contentKey).getSchema().get$ref() != null){
                    return schemas.get(operation.getRequestBody().getContent().get(contentKey).getSchema().get$ref().split("/")[3]);
                }
            }
        }
        return null;
    }
}