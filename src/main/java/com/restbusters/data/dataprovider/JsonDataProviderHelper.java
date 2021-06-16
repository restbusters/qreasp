package com.restbusters.data.dataprovider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.resource.GlobalResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * @author Sasha Matsaylo on 6/11/21
 * @author Ed Vayn
 * @project qreasp
 */

public class JsonDataProviderHelper {

    private static JsonDataProviderHelper instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private List<Object> dataSet;
    private String jsonData;

    public Map<String, Map<String, String>> getGlobalJson() {
        return globalJson;
    }

    private Map<String, Map<String, String>> globalJson = new HashMap();

    private JsonDataProviderHelper() {
    }

    public static synchronized JsonDataProviderHelper getInstance() {
        if (instance == null) {
            instance = new JsonDataProviderHelper();
        }
        return instance;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String json) {
        this.jsonData = json;
    }

    public void addJsonDataForMethod(String method, Map<String, String> jsonWithKeys){
        instance.globalJson.put(method, jsonWithKeys);
    }
    public void initializeJsonData(String json, String rootKey){
        List nodeList = null;
        ObjectMapper gm = GlobalResourceManager.getInstance().getObjectMapper();
        try {
            if (rootKey != null) {
                TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
                Map<String, Object> dataMap = gm.readValue(json, typeRef);
                nodeList = (List) dataMap.get(rootKey);
            } else {
                TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
                nodeList = gm.readValue(json, typeRef);
            }
        }
        catch(JsonProcessingException e){
            logger.error("Failed to parse json: {}", e.getLocalizedMessage());
            throw new NullPointerException("Parameter Type cannot be null");
        }
        this.dataSet = nodeList;
    }

    public void initializeSetOfData(List<Object> dataSet){
        if(dataSet == null){
            throw new NullPointerException("Parameter Type cannot be null");
        }
        this.dataSet = dataSet;
    }

    public List<Object> getDataSet(){
        if(this.dataSet == null){
            throw new NullPointerException("Data set has not been set, please use initializeSetOfData() method");
        }
        return this.dataSet;
    }

    public boolean isDataSet(){
        if(this.dataSet == null){
            return false;
        }
        return true;
    }


}

