package com.restbusters.integraton.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class SubstitutionRule {


	@JsonProperty("operationId")
	private String operationId;

	@JsonProperty("templateValue")
	private String templateValue;

	@JsonProperty("jsonPath")
	private String jsonPath;

	@JsonProperty("userProvided")
	private Map<String,String> userProvided;

	@JsonProperty("valueType")
	private String valueType;

	@JsonProperty("targetType")
	private String targetType;

	@JsonProperty("queryParam")
	private String queryParam;

}