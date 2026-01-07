
package com.restbusters.rest.payload.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "description",
    "optional"
})
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {

    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("optional")
    private Boolean optional;
    @JsonIgnore
    @Builder.Default
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Parameter withName(String name) {
        this.name = name;
        return this;
    }

    public Parameter withDescription(String description) {
        this.description = description;
        return this;
    }

    public Parameter withOptional(Boolean optional) {
        this.optional = optional;
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

    public Parameter withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
