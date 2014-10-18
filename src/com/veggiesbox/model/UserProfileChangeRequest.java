package com.veggiesbox.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserProfileChangeRequest extends BaseAuthenticatedRequest {

	private String firstName;
	private String lastName;
	private String gender;
	private Date birthDate;
	private String postalCode;
	private String postalCodeSpec;
	
	public UserProfileChangeRequest() {
		super();
	}

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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPostalCodeSpec() {
		return postalCodeSpec;
	}

	public void setPostalCodeSpec(String postalCodeSpec) {
		this.postalCodeSpec = postalCodeSpec;
	}
	
}
