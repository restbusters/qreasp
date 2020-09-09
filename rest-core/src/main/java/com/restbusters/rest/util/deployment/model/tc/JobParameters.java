package com.restbusters.rest.util.deployment.model.tc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobParameters{

	@JsonProperty("buildType")
	private BuildType buildType;

	@JsonProperty("properties")
	private Properties properties;

	public void setBuildType(BuildType buildType){
		this.buildType = buildType;
	}

	public BuildType getBuildType(){
		return buildType;
	}

	public void setProperties(Properties properties){
		this.properties = properties;
	}

	public Properties getProperties(){
		return properties;
	}

	@Override
 	public String toString(){
		return 
			"JobParameters{" + 
			"buildType = '" + buildType + '\'' + 
			",properties = '" + properties + '\'' + 
			"}";
		}
}