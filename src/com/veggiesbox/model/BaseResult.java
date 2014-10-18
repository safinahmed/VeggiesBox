package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.veggiesbox.util.Constants;

@XmlRootElement
public class BaseResult {
	
	private StatusResult statusResult;

	public BaseResult() {
		statusResult = new StatusResult();
	}

	public StatusResult getStatus() {
		return statusResult;
	}

	public void setStatus(StatusResult status) {
		this.statusResult = status;
	}
	
	public void setStatus(int statusId) {
		this.statusResult.setId(statusId);
	}
	
	public void markOK() {
		setStatus(Constants.STATUS_OK);
	}
	
	public boolean checkOK() {
		return statusResult.checkOK();
	}
}