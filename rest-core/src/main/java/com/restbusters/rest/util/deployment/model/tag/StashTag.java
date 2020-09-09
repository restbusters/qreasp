package com.restbusters.rest.util.deployment.model.tag;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StashTag{

	@JsonProperty("size")
	private int size;

	@JsonProperty("isLastPage")
	private boolean isLastPage;

	@JsonProperty("values")
	private List<StashTagValueItem> values;

	@JsonProperty("limit")
	private int limit;

	@JsonProperty("start")
	private int start;

	@JsonProperty("nextPageStart")
	private int nextPageStart;

	public void setSize(int size){
		this.size = size;
	}

	public int getSize(){
		return size;
	}

	public void setIsLastPage(boolean isLastPage){
		this.isLastPage = isLastPage;
	}

	public boolean isIsLastPage(){
		return isLastPage;
	}

	public void setValues(List<StashTagValueItem> values){
		this.values = values;
	}

	public List<StashTagValueItem> getValues(){
		return values;
	}

	public void setLimit(int limit){
		this.limit = limit;
	}

	public int getLimit(){
		return limit;
	}

	public void setStart(int start){
		this.start = start;
	}

	public int getStart(){
		return start;
	}

	@Override
 	public String toString(){
		return 
			"StashTag{" + 
			"size = '" + size + '\'' + 
			",isLastPage = '" + isLastPage + '\'' + 
			",values = '" + values + '\'' + 
			",limit = '" + limit + '\'' + 
			",start = '" + start + '\'' + 
			"}";
		}
}