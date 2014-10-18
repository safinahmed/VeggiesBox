package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserLoginResult extends BaseResult {
	
	private UserAuthData authData;

	public UserLoginResult() {
		super();
		authData = new UserAuthData();
	}


	public UserAuthData getAuthData() {
		return authData;
	}

	public void setAuthData(UserAuthData authData) {
		this.authData = authData;
	}


}