package com.restbusters.core.integration.stash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * @author Sasha matsaylo on 2020-09-10
 * @project qreasp
 */
public class StashHelper {

    private static StashHelper instance;
    private String url;
    private String user;
    private String password;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());



    private StashHelper() throws Exception {
    }

    public static synchronized StashHelper getInstance() throws Exception {
        if (instance == null) {
            instance = new StashHelper();
        }
        return instance;
    }

    public void init(String url, String user, String password) throws Exception {
        this.url = url;
        this.user = user;
        this.password = password;

    }

}