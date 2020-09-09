package com.restbusters.core.util.common;

import com.google.common.base.Splitter;
import org.json.JSONException;
import org.skyscreamer.jsonassert.FieldComparisonFailure;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author restbusters on 10/15/18
 * @project qreasp
 */

public class GenericUtils {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Map<String, String> splitToMap(String splitter, String keyValueSeparator, String keysAndValues) {
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
        if(matcher.groupCount() > 0 ){
            return processMatcher(matcher,variables);
        }
        else {
            Pattern pattern2 = Pattern.compile("%7B(.+?)%7D");
            Matcher matcher2 = pattern2.matcher(template);
            return processMatcher(matcher2, variables);
        }
    }

    private static String processMatcher(Matcher matcher, Map<String, String> variables){
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

    public static Map<String, List<FieldComparisonFailure>> compareTwoJsonString(String expectedJson, String actualJson, JSONCompareMode compareMode) {
        Map<String, List<FieldComparisonFailure>> result = new HashMap<>();
        JSONCompareResult comparedResult = null;
        try {
            comparedResult = JSONCompare.compareJSON(expectedJson, actualJson, compareMode);
        } catch (JSONException e) {
            logger.error("ERROR GETTING comparedResult", e);
        }
        if (comparedResult.getFieldFailures().size() > 0) {
            result.put("failures", buildListOfFailures(comparedResult.getFieldFailures()));
        }
        if (comparedResult.getFieldMissing().size() > 0) {
            result.put("missing", buildListOfFailures(comparedResult.getFieldMissing()));
        }
        if (comparedResult.getFieldUnexpected().size() > 0) {
            result.put("unexpected", buildListOfFailures(comparedResult.getFieldUnexpected()));
        }
        return result;
    }

    private static List<FieldComparisonFailure> buildListOfFailures(List<FieldComparisonFailure> fieldComparisonFailures) {
        List<FieldComparisonFailure> fieldComparisonFailureList = new ArrayList<FieldComparisonFailure>();

        Iterator<FieldComparisonFailure> itr = fieldComparisonFailures.iterator();
        while (itr.hasNext()) {
            FieldComparisonFailure fieldComparisonFailure = (FieldComparisonFailure) itr.next();
            fieldComparisonFailureList.add(fieldComparisonFailure);
        }
        return fieldComparisonFailureList;
    }

}
