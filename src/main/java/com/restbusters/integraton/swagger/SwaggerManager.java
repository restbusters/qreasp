package com.restbusters.integraton.swagger;

import com.restbusters.integraton.swagger.model.OperationParameters;
import com.restbusters.integraton.swagger.model.SwaggerApiResource;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import com.restbusters.integraton.swagger.model.OpenApiParseException;
//import v2.io.swagger.models.HttpMethod;
//import v2.io.swagger.parser.SwaggerException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Sasha matsaylo on 2020-09-10
 * @project qreasp
 */
public class SwaggerManager {

    private static SwaggerManager instance;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private SwaggerManager() {
    }

    public static synchronized SwaggerManager getInstance() {
        if (instance == null) {
            instance = new SwaggerManager();
        }
        return instance;
    }


    private List<SwaggerApiResource> buildSwaggerResources(OpenAPI openAPI) {
        List<SwaggerApiResource> apiResourceList = new ArrayList<>();
        Paths path = openAPI.getPaths();
        String serverUrl = getServerUrl(openAPI);
        path.entrySet().forEach(entry -> {
            PathItem pathItem = entry.getValue();
            String resourcePath = serverUrl + entry.getKey();
            if (pathItem.getGet() != null) {
                Operation getOperation = pathItem.getGet();
                SwaggerApiResource getResource = createSwaggerApiResource(getOperation, resourcePath, HttpMethod.GET.name());
                getResource = SwaggerDescriptorHelper.normalizeSwaggerApiResource(getResource, serverUrl);
                apiResourceList.add(getResource);
            }
            if (pathItem.getPost() != null) {
                Operation postOperation = pathItem.getPost();
                SwaggerApiResource postResource = createSwaggerApiResource(postOperation, resourcePath, HttpMethod.POST.name());
                postResource = SwaggerDescriptorHelper.normalizeSwaggerApiResource(postResource, serverUrl);
                apiResourceList.add(postResource);
            }
            if (pathItem.getPut() != null) {
                Operation putOperation = pathItem.getPut();
                SwaggerApiResource putResource = createSwaggerApiResource(putOperation, resourcePath, HttpMethod.PUT.name());
                putResource = SwaggerDescriptorHelper.normalizeSwaggerApiResource(putResource, serverUrl);
                apiResourceList.add(putResource);
            }
            if (pathItem.getPatch() != null) {
                Operation patchOperation = pathItem.getPatch();
                SwaggerApiResource patchResource = createSwaggerApiResource(patchOperation, resourcePath, HttpMethod.PATCH.name());
                patchResource = SwaggerDescriptorHelper.normalizeSwaggerApiResource(patchResource, serverUrl);
                apiResourceList.add(patchResource);
            }
            if (pathItem.getDelete() != null) {
                Operation deleteOperation = pathItem.getDelete();
                SwaggerApiResource deleteResource = createSwaggerApiResource(deleteOperation, resourcePath, HttpMethod.DELETE.name());
                deleteResource = SwaggerDescriptorHelper.normalizeSwaggerApiResource(deleteResource, serverUrl);
                apiResourceList.add(deleteResource);
            }
        });
        return apiResourceList;
    }

    public SwaggerDescriptor getSwaggerDescriptor(String url) {
        OpenAPI openAPI = null;
        try {
            openAPI = initOpenApi(url, "HTTP");
        } catch (OpenApiParseException e) {
            logger.error("OpenApiParseException: {}", e.getMessage(), e);
        }
        return buildDescriptor(openAPI);
    }


    public SwaggerDescriptor getSwaggerDescriptorFromSwaggerContent(String swaggerContent) {
        OpenAPI openAPI = null;
        try {
            openAPI = initOpenApi(swaggerContent, "JSON");
        } catch (OpenApiParseException e) {
            logger.error("OpenApiParseException: {}", e.getMessage(), e);
        }
        return buildDescriptor(openAPI);
    }

    private String getServerUrl(OpenAPI openAPI) {
        if (openAPI.getServers() != null && !openAPI.getServers().isEmpty()) {
            return openAPI.getServers().get(0).getUrl();
        }
        logger.warn("No servers defined in OpenAPI spec, using empty string");
        return "";
    }

    private OpenAPI initOpenApi(String content, String type) throws OpenApiParseException {
        OpenAPI openAPI = null;
        if(type.equalsIgnoreCase("HTTP")){
            openAPI = new OpenAPIV3Parser().read(content);
        }
        if(type.equalsIgnoreCase("JSON")){
            openAPI = new OpenAPIV3Parser().readContents(content, null, null).getOpenAPI();
        }
        if(openAPI == null){
            throw new OpenApiParseException("Failed to build OpenApi");
        }
        return openAPI;
    }

    private SwaggerDescriptor buildDescriptor(OpenAPI openAPI) {
        if (openAPI == null) {
            logger.error("Cannot build descriptor from null OpenAPI");
            return null;
        }
        SwaggerDescriptor swaggerDescriptor = new SwaggerDescriptor();
        if (openAPI.getInfo() != null) {
            swaggerDescriptor.setApiTitle(openAPI.getInfo().getTitle());
        }
        swaggerDescriptor.setServerUrl(getServerUrl(openAPI));
        swaggerDescriptor.setApiVersion(openAPI.getOpenapi());
        swaggerDescriptor.setSwaggerApiResources(this.buildSwaggerResources(openAPI));
        return swaggerDescriptor;
    }


    public List<SwaggerDescriptor> getSwaggerApiResources(List<String> swaggerUrls) {
        List<SwaggerDescriptor> swaggerDescriptors = Collections.synchronizedList(new ArrayList<>());
        swaggerUrls.stream().parallel().forEach(url -> {
            try {
                SwaggerDescriptor descriptor = getSwaggerDescriptor(url);
                if (descriptor != null) {
                    swaggerDescriptors.add(descriptor);
                }
            } catch (Exception e) {
                logger.error("Failed to obtain swagger resource for url: {}", url, e);
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
            apiResource.setSummary(operation.getSummary());
        }
        if (!StringUtils.isEmpty(operation.getDescription())) {
            apiResource.setDescription(operation.getDescription());
        }
        return apiResource;
    }

    private List<OperationParameters> setOperationParameter(List<Parameter> parameters) {
        List<OperationParameters> operationParametersList = new ArrayList<>();
        parameters.forEach(parameter -> {
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
