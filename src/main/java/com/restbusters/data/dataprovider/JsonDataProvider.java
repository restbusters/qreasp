package com.restbusters.data.dataprovider;

import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Sasha Matsaylo on 6/11/21
 * @author Ed Vayn
 * @project qreasp
 */
public class JsonDataProvider {

    @DataProvider(name = "jsonDataProvider")
    public Iterator<Object[]> clientResource(Method m) {
        Map<String, String> jsonData = JsonDataProviderHelper.getInstance().getGlobalJson().get(m.getName());
        JsonDataProviderHelper.getInstance().initializeJsonData(jsonData.get("json"), jsonData.get("rootKey"));
        List<Object> dataSet = JsonDataProviderHelper.getInstance().getDataSet();
        return getDataObject(dataSet);
    }

    private Iterator<Object[]> getDataObject(List<Object> dataList){
        List<Object[]> elemList = new ArrayList<>();
        dataList.stream().forEach(e -> elemList.add(new Object[]{e}));
        return elemList.iterator();
    }
}
