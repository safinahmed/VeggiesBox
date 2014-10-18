package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.veggiesbox.util.Utils;

@XmlRootElement 
public class UserRegistrationRequest {

	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String referrerId;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getReferrerId() {
		return referrerId;
	}

	public void setReferrerId(String referrerId) {
		this.referrerId = referrerId;
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
		if(Utils.isNullOrBlank(firstName))
			return false;
		if(Utils.isNullOrBlank(lastName))
			return false;
		if(Utils.isNullOrBlank(email))
			return false;
		if(!Utils.isValidEmailAddress(email))
			return false;
		if(Utils.isNullOrBlank(password))
			return false;
		if(password.length() < 6)
			return false;
		return true;
	}	
}
