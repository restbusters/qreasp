package com.restbusters.integraton.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.annotation.Generated;
import java.util.Map;

@Data
public class Instruction{

	@JsonProperty("userDefined")
	private Map<String,String> userDefined;

	@JsonProperty("operationId")
	private String operationId;

	@JsonProperty("templateValue")
	private String templateValue;

	@JsonProperty("jsonPath")
	private String jsonPath;

}