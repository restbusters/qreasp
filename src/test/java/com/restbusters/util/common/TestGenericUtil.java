package com.restbusters.util.common;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Test
    public void convert_yaml_to_json() {
        String yaml = "---\n" +
                "string: test\n" +
                "int: 1\n" +
                "map: {}\n" +
                "list: []";
        String expectedJson = "{\"string\":\"test\",\"int\":1,\"map\":{},\"list\":[]}";
        Optional<String> actualJson = GenericUtils.convertYamlToJson(objectMapper, yamlObjectMapper, yaml);
        Assert.assertTrue(actualJson.get() != null);
        logger.info("Expected json: {}", expectedJson);
        logger.info("Actual   json: {}", actualJson.get());
        Assert.assertEquals(actualJson.get(), expectedJson, "Expected to be the same");
    }

}
