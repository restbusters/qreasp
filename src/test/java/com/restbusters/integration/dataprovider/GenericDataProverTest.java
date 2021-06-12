package com.restbusters.integration.dataprovider;

import com.restbusters.integraton.dataprovider.DataProviderHelper;
import com.restbusters.integraton.dataprovider.GenericDataProvider;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author Sasha Matsaylo on 6/11/21
 * @project qreasp
 */
public class GenericDataProverTest {

    private DataProviderHelper dataProviderHelper;
    private List<Object> dataSet = new ArrayList<>();
    private Map<String,String> dataMap1 = new HashMap<>();
    private Map<String,String> dataMap2 = new HashMap<>();

    //if data provider is used it's needs to initialized before test start in setUp method.
    @BeforeClass(alwaysRun = true)
    private void setUp(){
        this.dataProviderHelper = DataProviderHelper.getInstance();
        this.dataMap1.put("key", "dataMap1");
        this.dataMap2.put("key", "dataMap2");
        this.dataSet.add(dataMap1);
        this.dataSet.add(dataMap2);
        this.dataProviderHelper.initializeSetOfData(dataSet);
    }

    @Test(dataProvider = "genericDataProvider", dataProviderClass = GenericDataProvider.class)
    private void testDataProviderWithMap(Map<String,String> dataMap){
        List<String> expectedValues = Arrays.asList("dataMap1", "dataMap2");
        Assert.assertTrue(this.dataProviderHelper.isDataSet());
        Assert.assertTrue(dataMap instanceof Map);
        Assert.assertTrue(expectedValues.contains(dataMap.get("key")));
    }
}
