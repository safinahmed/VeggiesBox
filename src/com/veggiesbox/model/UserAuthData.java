package com.veggiesbox.model;
import javax.xml.bind.annotation.XmlRootElement;

import com.veggiesbox.util.Utils;

@XmlRootElement
public class UserAuthData {

	private String token;
	
	public UserAuthData() {
		token = "";
	}
 	
	public UserAuthData(String userToken) {
		this.token = userToken;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public boolean validate() {
		if(Utils.isNullOrBlank(token))
			return false;
		return true;
	}	
	
}
