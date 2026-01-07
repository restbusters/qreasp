
package com.restbusters.rest.payload.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayloadTemplate {

    @JsonProperty("operationId")
    private String operationId;
    @JsonProperty("payloadName")
    private String payloadName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("relativePath")
    private String relativePath;
    @JsonProperty("parameters")
    @Builder.Default
    private List<Parameter> parameters = null;
    @JsonProperty("payload")
    private Payload payload;
    @JsonIgnore
    @Builder.Default
    private Map<String, Object> additionalProperties = new HashMap<>();

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
