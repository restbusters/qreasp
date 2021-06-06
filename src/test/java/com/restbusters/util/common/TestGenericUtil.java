package com.restbusters.util.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.restbusters.resource.GlobalResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

/**
 * @author Sasha Matsaylo on 2020-10-03
 * @project qreasp
 */
public class TestGenericUtil {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ObjectMapper yamlObjectMapper = GlobalResourceManager.getInstance().getYamlObjectMapper();
    private ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();
    String yaml = "---\n" +
            "string: test\n" +
            "int: 1\n" +
            "map: {}\n" +
            "list: []";
    String expectedJson = "{\"string\":\"test\",\"int\":1,\"map\":{},\"list\":[]}";

    @Test
    public void convert_yaml_to_json() {
        Optional<String> actualJson = GenericUtils.convertYamlToJson(objectMapper, yamlObjectMapper, yaml);
        Assert.assertTrue(actualJson.get() != null);
        String convertedFromYaml = actualJson.get().replaceAll("\\s", "");
        logger.info("Expected json: {}", expectedJson);
        logger.info("Actual   json: {}", actualJson.get());
        Assert.assertEquals(expectedJson, convertedFromYaml, "Expected to be the same");
    }

    @Test( expectedExceptions = PathNotFoundException.class)
    public void deleteKeyAndItsValueFromJson() {
        String jsonString =  GenericUtils.removeKeyWithValueFromJson(expectedJson, "$.list");
        logger.info(jsonString);
        JsonPath.read(jsonString, "$.list");
    }

    @Test( expectedExceptions = PathNotFoundException.class)
    public void setJsonKeyAndValue() {
        String jsonString =  GenericUtils.removeKeyWithValueFromJson(expectedJson, "$.list");
        logger.info(jsonString);
        JsonPath.read(jsonString, "$.list");
    }

}
