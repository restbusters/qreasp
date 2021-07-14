package com.restbusters.util.common;


import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.util.*;


public class Context {
    private final Map<String, Object> context = new HashMap<String, Object>();

    public Context setValue(String key, List<SwaggerDescriptor> value) {
        context.put(key, value);

        return this;
    }

    public Context setValue(String key, HashMap value) {
        context.put(key, value);

        return this;
    }

    public Context setValue(String key, String value) {
        context.put(key, value);

        return this;
    }

    public Context setValue(String key, Map value) {
        context.put(key, value);

        return this;
    }

    public Context setValue(String key, Response value) {
        context.put(key, value);

        return this;
    }

    public Context setValue(String key, OkHttpClient value) {
        context.put(key, value);

        return this;
    }

    public Context setValue(String key, HashMap<String, Object>[] value) {
        context.put(key, value);

        return this;
    }


    public Object getValue(String key) {
        return context.get(key);
    }

}
