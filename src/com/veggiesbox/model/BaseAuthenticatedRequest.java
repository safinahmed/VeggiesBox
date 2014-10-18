package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BaseAuthenticatedRequest {

	private UserAuthData authData;

	public BaseAuthenticatedRequest() {
	}

	public UserAuthData getAuthData() {
		return authData;
	}

	public void setAuthData(UserAuthData authData) {
		this.authData = authData;
	}
	
	
}
