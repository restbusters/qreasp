package com.restbusters.rest.util.deployment.model.tc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Properties{

	@JsonProperty("property")
	private List<PropertyItem> property;

	public void setProperty(List<PropertyItem> property){
		this.property = property;
	}

	public List<PropertyItem> getProperty(){
		return property;
	}

	@Override
 	public String toString(){
		return 
			"Properties{" + 
			"property = '" + property + '\'' + 
			"}";
		}
}