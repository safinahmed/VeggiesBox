package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.veggiesbox.util.Utils;

@XmlRootElement
public class UserLoginRequest {
	
	private String email;
	private String password;

	public UserLoginRequest() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
	
	public boolean validate() {
		if(Utils.isNullOrBlank(email))
			return false;
		if(Utils.isNullOrBlank(password))
			return false;
		return true;
	}
}