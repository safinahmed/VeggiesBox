package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserResendActivationRequest {
	
	private String email;

	public UserResendActivationRequest() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}