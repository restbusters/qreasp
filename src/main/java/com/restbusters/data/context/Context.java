package com.restbusters.data.context;


import com.restbusters.exception.ContextKeyIsBlank;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;


public class Context {
    private final Map<String, Object> context = new HashMap<String, Object>();

    public Context setValue(String key, HashMap value) throws ContextKeyIsBlank {
        putIfKeyNotBlank(key, value);
        return this;
    }

    public Context setValue(String key, String value) throws ContextKeyIsBlank {
        putIfKeyNotBlank(key, value);
        return this;
    }

    public Context setValue(String key, Map value) throws ContextKeyIsBlank {
        putIfKeyNotBlank(key, value);
        return this;
    }

    public Context setValue(String key, Object value) throws ContextKeyIsBlank {
        putIfKeyNotBlank(key, value);
        return this;
    }

    public Context setValue(String key, HashMap<String, Object>[] value) throws ContextKeyIsBlank {
        putIfKeyNotBlank(key, value);
        return this;
    }

    public Object getValue(String key) {
        return context.get(key);
    }

    private void putIfKeyNotBlank(String key, Object value) throws ContextKeyIsBlank {
        if(StringUtils.isBlank(key)){
            throw new ContextKeyIsBlank("Context key must not be blank");
        }
        this.context.put(key, value);
    }

}
