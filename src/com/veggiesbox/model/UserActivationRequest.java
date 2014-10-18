package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserActivationRequest {
	
	private String activationId;

	public UserActivationRequest() {
	}

	public String getActivationId() {
		return activationId;
	}

	public void setActivationId(String activationId) {
		this.activationId = activationId;
	}
	
}