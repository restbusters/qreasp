package com.restbusters.rest.util.deployment.model.tc;

import com.fasterxml.jackson.annotation.JsonProperty;


public class BuildType{

	@JsonProperty("id")
	private String id;

	@JsonProperty("projectId")
	private String projectId;

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setProjectId(String projectId){
		this.projectId = projectId;
	}

	public String getProjectId(){
		return projectId;
	}

	@Override
 	public String toString(){
		return 
			"BuildType{" + 
			"id = '" + id + '\'' + 
			",projectId = '" + projectId + '\'' + 
			"}";
		}
}