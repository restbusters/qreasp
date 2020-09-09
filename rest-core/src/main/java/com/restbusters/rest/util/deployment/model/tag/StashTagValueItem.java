package com.restbusters.rest.util.deployment.model.tag;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StashTagValueItem {

	@JsonProperty("latestCommit")
	private String latestCommit;

	@JsonProperty("latestChangeset")
	private String latestChangeset;

	@JsonProperty("id")
	private String id;

	@JsonProperty("displayId")
	private String displayId;

	@JsonProperty("type")
	private String type;

	@JsonProperty("hash")
	private Object hash;

	public void setLatestCommit(String latestCommit){
		this.latestCommit = latestCommit;
	}

	public String getLatestCommit(){
		return latestCommit;
	}

	public void setLatestChangeset(String latestChangeset){
		this.latestChangeset = latestChangeset;
	}

	public String getLatestChangeset(){
		return latestChangeset;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setDisplayId(String displayId){
		this.displayId = displayId;
	}

	public String getDisplayId(){
		return displayId;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setHash(Object hash){
		this.hash = hash;
	}

	public Object getHash(){
		return hash;
	}

	@Override
 	public String toString(){
		return 
			"ValuesItem{" + 
			"latestCommit = '" + latestCommit + '\'' + 
			",latestChangeset = '" + latestChangeset + '\'' + 
			",id = '" + id + '\'' + 
			",displayId = '" + displayId + '\'' + 
			",type = '" + type + '\'' + 
			",hash = '" + hash + '\'' + 
			"}";
		}
}