package com.restbusters.integraton.stash.client.resoures;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.restbusters.integraton.stash.client.model.StashRequests;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.model.HttpRestRequest;
import com.restbusters.util.common.RBFileUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author smatsaylo on 6/6/21
 * @project stash-client
 */
public class StashResourceManager {

  private static StashResourceManager instance;

  private StashRequests stashRequests;
  private String jsonStashRequests;
  private final String requestsFile = "stash-http-requests.json";

  private StashResourceManager() throws Exception {
    this.jsonStashRequests = RBFileUtils.getFileOnClassPathAsString(this.requestsFile);
    this.stashRequests = GlobalResourceManager.getInstance().getObjectMapper().readValue(jsonStashRequests, StashRequests.class);
  }

  public static synchronized StashResourceManager getInstance() throws Exception {
    if (instance == null) {
      instance = new StashResourceManager();
    }
    return instance;
  }


  public StashRequests getStashRequests() {
    return stashRequests;
  }

  public void initServerUrl(String serverUrl) {
    Map<String, HttpRestRequest> restRequestMap;
    restRequestMap =
            GlobalResourceManager.getInstance().getObjectMapper().convertValue(
                stashRequests, new TypeReference<Map<String, HttpRestRequest>>() {});
      for (Map.Entry<String, HttpRestRequest> entry : restRequestMap.entrySet()) {
          String original = entry.getValue().getUrl();
          entry.getValue().setUrl(serverUrl + original);
      }
      try {
          this.jsonStashRequests = GlobalResourceManager.getInstance().getObjectMapper().writeValueAsString(restRequestMap);
      } catch (JsonProcessingException e) {
          e.printStackTrace();
      }
      try {
          this.stashRequests = GlobalResourceManager.getInstance().getObjectMapper().readValue(jsonStashRequests, StashRequests.class);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
}
