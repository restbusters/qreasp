package com.restbusters.data.dataprovider;

import org.testng.annotations.DataProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Sasha Matsaylo on 6/11/21
 * @project qreasp
 */
public class GenericDataProvider {


    @DataProvider(name = "genericDataProvider")
    public Iterator<Object[]> clientResource() {
        List<Object> dataSet = DataProviderHelper.getInstance().getDataSet();
        return getDataObject(dataSet);
    }

    private Iterator<Object[]>  getDataObject(List<Object> dataSet){
        Collection<Object[]> data = new ArrayList<Object[]>();
        for (int i = 0; i < dataSet.size(); i++) {
            data.add(new Object[]{dataSet.get(i)});
        }
        return data.iterator();
    }
}
