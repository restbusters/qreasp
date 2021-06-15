package com.restbusters.integraton.tc.client.model.post.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Properties{

	@JsonProperty("property")
	private List<PropertyItem> property;

}