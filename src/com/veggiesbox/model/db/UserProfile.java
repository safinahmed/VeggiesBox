package com.veggiesbox.model.db;

import java.util.Date;

import com.google.appengine.api.datastore.Key;
import com.veggiesbox.model.UserProfileChangeRequest;

public class UserProfile {

	public final static String ENTITY = "UserProfile";
	
	private Key key;
	private String gender;
	private Date birthDate;
	private String postalCode;
	private String postalCodeSpec; 
	
	public UserProfile() {
		super();
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getPostalCodeSpec() {
		return postalCodeSpec;
	}


	public void setPostalCodeSpec(String postalCodeSpec) {
		this.postalCodeSpec = postalCodeSpec;
	}


	public void fromObject(UserProfileChangeRequest upcr) {
		setGender(upcr.getGender());
		setBirthDate(upcr.getBirthDate());
		setPostalCode(upcr.getPostalCode());
		setPostalCodeSpec(upcr.getPostalCodeSpec());
	}

	public void fromObject(com.restfb.types.User fbUser) {
		String fbGender = fbUser.getGender();
		if(fbGender != null) {
			if(fbGender.equalsIgnoreCase("male"))
				setGender("M");
			else if(fbGender.equalsIgnoreCase("female"))
				setGender("F");
		}
		
		setBirthDate(fbUser.getBirthdayAsDate());	
		
	}
}
