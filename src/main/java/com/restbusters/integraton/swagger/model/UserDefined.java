package com.restbusters.integraton.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class UserDefined{

	@JsonProperty("key")
	private String key;

	public void setKey(String key){
		this.key = key;
	}

	public String getKey(){
		return key;
	}

	@Override
 	public String toString(){
		return 
			"UserDefined{" + 
			"key = '" + key + '\'' + 
			"}";
		}
}