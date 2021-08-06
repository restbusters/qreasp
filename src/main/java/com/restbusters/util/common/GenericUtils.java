package com.restbusters.util.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.resource.GlobalResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author restbusters on 10/15/18
 * @project qreasp
 */

public class GenericUtils {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Configuration jacksonConfiguration = GlobalResourceManager.getInstance().getConfiguration();

    public static Map<String, String> splitToMap(String splitter, String keyValueSeparator, String keysAndValues) {
        if (!RBPattern.SPLIT_TO_MAP_VALIDATOR.matcher(keysAndValues).matches()) {
            throw new IllegalArgumentException("Invalid String for splitting, Example: key=value if more then one key=value;key=value");
        }
        return Splitter.on(splitter).withKeyValueSeparator(keyValueSeparator).split(keysAndValues);
    }

    public static String substituteVariables(String template, Map<String, String> variables) {
        if (template.equalsIgnoreCase("")) {
            throw new NullPointerException("String template SHOULD NOT BE NULL");
        }
        if (variables == null) {
            throw new NullPointerException("Map variables SHOULD NOT BE NULL");
        }
        Pattern pattern = Pattern.compile("\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(template);
        if (matcher.groupCount() > 0) {
            return processMatcher(matcher, variables);
        } else {
            Pattern pattern2 = Pattern.compile("%7B(.+?)%7D");
            Matcher matcher2 = pattern2.matcher(template);
            return processMatcher(matcher2, variables);
        }
    }

    private static String processMatcher(Matcher matcher, Map<String, String> variables) {
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            if (variables.containsKey(matcher.group(1))) {
                String replacement = variables.get(matcher.group(1));
                matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }


    public static Optional<String> convertYamlToJson(ObjectMapper objectMapper, ObjectMapper yamlObjectMapper, String yaml) {
        try {
            Object obj = yamlObjectMapper.readValue(yaml, Object.class);
            return Optional.of(objectMapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static String removeKeyWithValueFromJson(String json, String pathSpec) {
        return JsonPath.using(jacksonConfiguration).parse(json).delete(pathSpec).jsonString();
    }

    public static String setKeyWithValueFromJson(String json, String pathSpec, Object value) {
        return JsonPath.using(jacksonConfiguration).parse(json).set(pathSpec, value).jsonString();
    }

    public static boolean isJSONValid(String jsonInString ) {
        try {
            GlobalResourceManager.getInstance().getObjectMapper().readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String RegexMatcher(String text, String pattern, int expectedGroup) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(text);
        if (m.find()) {
            logger.info("Expected group {} return value {} ", expectedGroup, m.group(expectedGroup));
            return m.group(expectedGroup);
        }
        logger.warn("NO MATCH FOR FOR YOUR PATTERN");
        return null;
    }

}
