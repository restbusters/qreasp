package com.restbusters.rest.util.deployment.model.tc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PropertyItem{

	@JsonProperty("name")
	private String name;

	@JsonProperty("value")
	private String value;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

	@Override
 	public String toString(){
		return 
			"PropertyItem{" + 
			"name = '" + name + '\'' + 
			",value = '" + value + '\'' + 
			"}";
		}
}