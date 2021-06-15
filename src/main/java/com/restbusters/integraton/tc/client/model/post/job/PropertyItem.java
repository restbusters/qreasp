package com.restbusters.integraton.tc.client.model.post.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PropertyItem{

	@JsonProperty("name")
	private String name;

	@JsonProperty("value")
	private String value;

}