package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.veggiesbox.util.Constants;

@XmlRootElement
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class StatusResult {
	
	private int id;
	private String message;

	//Default Status is NOK
	public StatusResult() {
		id = Constants.STATUS_NOK;
	}

	public StatusResult(int id) {
		this(id, "");
	}

	public StatusResult(int id, String message) {
		this.id = id;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean checkOK() {
		return this.id == Constants.STATUS_OK;
	}
	
	public void markOK() {
		id = Constants.STATUS_OK;
	}

}