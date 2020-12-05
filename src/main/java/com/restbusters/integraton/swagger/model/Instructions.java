package com.restbusters.integraton.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Instructions {

	@JsonProperty("instructions")
	private List<Instructions> instructions;

}