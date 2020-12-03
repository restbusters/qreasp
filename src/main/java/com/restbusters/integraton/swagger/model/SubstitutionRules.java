package com.restbusters.integraton.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.annotation.Generated;

@Data
public class SubstitutionRules{

	@JsonProperty("instruction")
	private Instruction instruction;

	@JsonProperty("valueType")
	private String valueType;

	@JsonProperty("payLoadType")
	private String payLoadType;



}