package com.restbusters.integraton.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.integraton.swagger.model.OperationParameters;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.restclient.RestClientHelper;
import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.Operation;
import io.swagger.oas.models.PathItem;
import io.swagger.oas.models.Paths;
import io.swagger.oas.models.parameters.Parameter;
import io.swagger.parser.v3.OpenAPIV3Parser;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import v2.io.swagger.models.HttpMethod;
import v2.io.swagger.parser.SwaggerException;

import java.lang.invoke.MethodHandles;
import java.util.*;


/**
 * @author Sasha matsaylo on 2020-09-10
 * @project qreasp
 */
public class SwaggerHelper {

    private static SwaggerHelper instance;
    private OkHttpClient okHttpClient;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();


    private SwaggerHelper() {
    }

    public static synchronized SwaggerHelper getInstance() {
        if (instance == null) {
            instance = new SwaggerHelper();
        }
        return instance;
    }

    public void init() {
        this.okHttpClient = RestClientHelper.getInstance().buildNoAuthClient();
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
        OpenAPI openAPI = new OpenAPIV3Parser().read(url);
        if (StringUtils.isEmpty(openAPI.getInfo().getTitle())) {
            throw new SwaggerException("Title for Swagger has not been set for url: " + url);
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
            swaggerDescriptors.add(getSwaggerDescriptor(url));
        });
        return swaggerDescriptors;

    }

//    public void swaggerDiff2() throws Exception {
//        TaskExecResult taskExecResult = setInitialTaskStatus("GENERATE_MANIFEST_DIFF");
//        Response responseSourceOlder =
//                RestClientHelper.getInstance().doGetRequest(okHttpClient, "https://tsm-leba.staging.ccs.guidewire.net/swagger-ui-v2/openapi.json", null, null);
//        Response responseTargetNewer =
//                RestClientHelper.getInstance().doGetRequest(okHttpClient, "https://tsm-leba.dev.ccs.guidewire.net/swagger-ui-v2/openapi.json", null, null);
//
//        if (responseSourceOlder.code() != 200 && responseTargetNewer.code() != 200) {
//            logger.error("Failed to obtain swagger metadata, source code: {}, target  code {}: ", responseSourceOlder.code(), responseTargetNewer.code());
//            Assert.fail("Failed to obtain swagger metadata, source or target swagger");
//        }
//
//        Optional<String> jsonSource = convertYamlToJson(responseSourceOlder.body().string());
//        Optional<String> jsonTarget = convertYamlToJson(responseTargetNewer.body().string());
//        ObjectMapper jackson = new ObjectMapper();
//        JsonNode beforeNode = jackson.readTree(jsonSource.get());
//        JsonNode afterNode = jackson.readTree(jsonTarget.get());
//        JsonNode patchNode = JsonDiff.asJson(beforeNode, afterNode);
//        String diff = patchNode.toString();
//        List<Map<String, Object>> diffObject = objectMapper.readValue(diff, new TypeReference<List<Map<String, Object>>>() {
//        });
//        String header = "| Where | Change | Value |\n";
//        String subHeader = "| :--- | :--- | :--- |\n";
//        StringBuilder diffBuilder
//                = new StringBuilder(header);
//        diffBuilder.append(subHeader);
//        for (Map<String, Object> map : diffObject) {
//
//            if(!map.get("path").toString().contains("/description")){
//                diffBuilder.append(String.format("| %s | %s | %s |\n", map.get("path"), map.get("op"), map.get("value")));
//            }
////            if(map.get("op").toString().equalsIgnoreCase("replace")){
////                //buildJsonPath2(path);
////                String jsonPath = buildJsonPath2(path);
////                String versionOld = JsonPath.read(jsonSource.get() , jsonPath);
////                String serviceName = JsonPath.read(jsonTarget.get(), jsonPath.replaceAll("version", "name"));
////                logger.info("Service: {} previous version {} new version {}", serviceName, versionOld, map.get("value"));
////                diffBuilder.append(String.format("| %s | %s | %s | %s |\n", serviceName, "updated", versionOld, map.get("value")));
////            }
//
//        }
//        logger.info(diffBuilder.toString());
//    }

//    public Map<String, List<com.restbusters.integration.swagger.model.SwaggerApiResource>> collectMultipleSwaggers(List<String> swaggerUrls){
//        Map<String, >
//        swaggerUrls.stream().parallel().forEach(object -> {
//            //Your work on each object goes here, using object
//        })
//
//
//    }

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
        parameters.stream().parallel().forEach(
                parameter -> {
                    OperationParameters operationParameters = new OperationParameters();
                    operationParameters.setDescription(parameter.getDescription());
                    operationParameters.setIn(parameter.getIn());
                    operationParameters.setName(parameter.getName());
                    if(!StringUtils.isEmpty(parameter.getRequired())){
                        operationParameters.setRequired(parameter.getRequired());
                    }
                    operationParametersList.add(operationParameters);
                });
        return operationParametersList;
    }
}