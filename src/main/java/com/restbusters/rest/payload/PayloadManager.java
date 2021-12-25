package com.restbusters.rest.payload;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.payload.model.PayloadTemplate;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.json.JsonExtension;
import org.jtwig.json.configuration.JsonMapperProviderConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sasha Matsaylo on 10/15/18
 * @project qreasp
 */

public class PayloadManager {

    private static PayloadManager instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private EnvironmentConfiguration configuration;
    private String payloadsAsJson;
    private List<PayloadTemplate> payloadTemplates;
    private ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();
    private Map<String, Object> defaultMap;


    private PayloadManager(String jsonPayloads) {
        configuration = EnvironmentConfigurationBuilder
                .configuration()
                .extensions()
                .add(new JsonExtension(JsonMapperProviderConfigurationBuilder
                        .jsonConfiguration()
                        .build())
                )
                .and()
                .build();
        initPayloads(jsonPayloads);
    }

    private void initPayloads(String jsonPayloads) {
        try {
            this.payloadsAsJson = jsonPayloads;
            this.payloadTemplates = objectMapper.readValue(this.payloadsAsJson, new TypeReference<List<PayloadTemplate>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public static synchronized PayloadManager getInstance(String jsonPayloads) throws IOException {
        if (instance == null) {
            instance = new PayloadManager(jsonPayloads);
        }
        return instance;
    }

    public String renderPayload(String payLoadTemplate, Map<String, Object>... templateReplacementMap) {
        Map<String, Object> userSubstitutionPayloadParamsMap = null;
        if (templateReplacementMap.length == 1) {
            userSubstitutionPayloadParamsMap = templateReplacementMap[0];
        } else {
            setDefaultPayloadMap();
            userSubstitutionPayloadParamsMap = this.defaultMap;
        }
        JtwigTemplate template = JtwigTemplate.inlineTemplate(payLoadTemplate, configuration);
        JtwigModel model = JtwigModel.newModel(userSubstitutionPayloadParamsMap);
        return template.render(model);
    }

    private void setDefaultPayloadMap() {
        if (this.defaultMap == null) {
            this.defaultMap = new HashMap<>();
        }
    }

    public String renderPayload(Object model) {

        JtwigTemplate jtwigTemplate = JtwigTemplate.inlineTemplate("{{ json_encode(variable) }}", configuration);
        JtwigModel jtwigModel= JtwigModel.newModel().with("variable", model);
        return jtwigTemplate.render(jtwigModel);
    }

    public Map<String, Object> getPayloadMetaData(Map<String, String> filterMap) {

        Map<String, Object> result = findPayloadMetaData(filterMap);
        if (result != null) {
            return result;
        }
        return null;
    }

    public String getPayloadMetaDataAsString(Map<String, String> filterMap) {
        Map<String, Object> result = findPayloadMetaData(filterMap);
        if (result != null) {
            try {
                return objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Map<String,Object> findPayloadMetaData(Map<String, String> filterMap){
        Filter filter = buildFilter(filterMap);
        List<Map<String, Object>> result = JsonPath.parse(this.payloadsAsJson).read("$[?]", filter);
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    private Filter buildFilter(Map<String, String> filterMap) {
        Set<Map.Entry<String, String>> entrySet = filterMap.entrySet();
        int setStart = 1;
        Criteria criteria = null;
        for (Map.Entry<String, String> entry : entrySet) {
            if (setStart == 1) {
                criteria = Criteria.where(entry.getKey()).eq(entry.getValue());

            } else {
                criteria = criteria.and(entry.getKey()).eq(entry.getValue());
            }
            setStart++;
        }
        return Filter.filter(criteria);
    }

    public String getPayloadTemplateAsString(Map<String, Object> payloadMetaData){
        try {
            return objectMapper.writeValueAsString(payloadMetaData.get("payload"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}

