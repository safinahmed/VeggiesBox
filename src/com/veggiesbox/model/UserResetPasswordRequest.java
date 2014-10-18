package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.veggiesbox.util.Utils;

@XmlRootElement
public class UserResetPasswordRequest {
	
	private String email;

	public UserResetPasswordRequest() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean validate() {
		if(Utils.isNullOrBlank(email))
			return false;
		return true;
	}
	
}
