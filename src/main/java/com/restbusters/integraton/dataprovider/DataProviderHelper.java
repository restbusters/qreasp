package com.restbusters.integraton.dataprovider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.resource.GlobalResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Sasha Matsaylo on 6/11/21
 * @project qreasp
 */

public class DataProviderHelper {

    private static DataProviderHelper instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private List<Object> dataSet;

    private DataProviderHelper() {
    }

    public static synchronized DataProviderHelper getInstance() {
        if (instance == null) {
            instance = new DataProviderHelper();
        }
        return instance;
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



