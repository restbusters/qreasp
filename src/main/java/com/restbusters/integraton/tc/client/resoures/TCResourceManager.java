package com.restbusters.integraton.tc.client.resoures;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.integraton.tc.client.model.TCRequests;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.model.HttpRestRequest;
import com.restbusters.util.common.FileUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author smatsaylo on 8/28/20
 * @project rest-support
 */
public class TCResourceManager {

  private static TCResourceManager instance;

  private ObjectMapper objectMapper;
  private TCRequests tcRequests;
  private String jsonTcRequests;
  private final String requestsFile = "tc-http-requests.json";

  private TCResourceManager() throws Exception {
    this.objectMapper = GlobalResourceManager.getInstance().getObjectMapper();
    this.jsonTcRequests = FileUtils.getFileOnClassPathAsString(this.requestsFile);
    this.tcRequests = objectMapper.readValue(jsonTcRequests, TCRequests.class);
  }

  public static synchronized TCResourceManager getInstance() throws Exception {
    if (instance == null) {
      instance = new TCResourceManager();
    }
    return instance;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public TCRequests getTcRequests() {
    return tcRequests;
  }

  public void initServerUrl(String serverUrl) {
    Map<String, HttpRestRequest> restRequestMap;
    restRequestMap =
        objectMapper.convertValue(
            tcRequests, new TypeReference<Map<String, HttpRestRequest>>() {});
      for (Map.Entry<String, HttpRestRequest> entry : restRequestMap.entrySet()) {
          String original = entry.getValue().getUrl();
          entry.getValue().setUrl(serverUrl + original);
      }
      try {
          this.jsonTcRequests = this.objectMapper.writeValueAsString(restRequestMap);
      } catch (JsonProcessingException e) {
          e.printStackTrace();
      }
      try {
          this.tcRequests = objectMapper.readValue(jsonTcRequests, TCRequests.class);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
}
