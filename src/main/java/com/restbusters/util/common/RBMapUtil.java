package com.restbusters.util.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Splitter;
import com.restbusters.resource.GlobalResourceManager;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * @author evayn on 4/01/21
 * @project dart-extensions
 */
public class RBMapUtil {

    public static Map<String,String> replaceWithValues(Map<String,String> actualValues, Map<String,String> replacementValues){
        if(replacementValues == null || replacementValues.size() == 0)
            return actualValues;

        replacementValues.forEach((key, value) -> {
            if (actualValues.get(key) != null)
                actualValues.put(key, value);
        });
        return actualValues;
    }

    public static Map<String, String> stringify(Map<String , Object> map) {

        Map<String, String> stringifiedMap = new HashMap<>();

        map.forEach((key, value) -> {

            if (key != null) {
                try {
                    stringifiedMap.put(key, (String) value);
                }
                catch (ClassCastException e) {
                    throw new RuntimeException("MapUtil.stringify:  Failed to stringify map.  " +
                                               "Value for key '" + key +
                                               "' could not be cast to String.");
                }
            }
        });
        return stringifiedMap;
    }


    public static boolean isAllHeaderValuesPresent(Map<String, String> map) {
        return map.entrySet().stream().noneMatch(entry -> StringUtils.isBlank(entry.getValue()));
    }

    public static Map<String, String> splitToMap(String splitter, String keyValueSeparator, String keysAndValues) {
        return Splitter.on(splitter).withKeyValueSeparator(keyValueSeparator).split(keysAndValues);
    }

    public static Map<String, Object> readAsMap(String input) {
        try {
            Map<String, Object> objectMap = GlobalResourceManager.getInstance().getObjectMapper().readValue(input, new TypeReference<Map<String, Object>>() {});
            if(objectMap != null)
                return objectMap;
            else
                return new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}