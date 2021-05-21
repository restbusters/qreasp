
package com.restbusters.rest.payload.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "operationId",
    "payloadName",
    "description",
    "parameters",
    "payload"
})
@Setter
@Getter
public class PayloadTemplate {

    @JsonProperty("operationId")
    public String operationId;
    @JsonProperty("payloadName")
    public String payloadName;
    @JsonProperty("description")
    public String description;
    @JsonProperty("relativePath")
    public String relativePath;
    @JsonProperty("parameters")
    public List<Parameter> parameters = null;
    @JsonProperty("payload")
    public Payload payload;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public PayloadTemplate withOperationId(String operationId) {
        this.operationId = operationId;
        return this;
    }

    public PayloadTemplate withPayloadName(String payloadName) {
        this.payloadName = payloadName;
        return this;
    }

    public PayloadTemplate withDescription(String description) {
        this.description = description;
        return this;
    }

    public PayloadTemplate withParameters(List<Parameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public PayloadTemplate withPayload(Payload payload) {
        this.payload = payload;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public PayloadTemplate withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
