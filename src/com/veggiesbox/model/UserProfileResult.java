package com.veggiesbox.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.veggiesbox.model.db.User;
import com.veggiesbox.model.db.UserProfile;

@XmlRootElement
public class UserProfileResult extends BaseResult {

	private String photo;
	private String firstName;
	private String lastName;
	private String email;
	private String gender;
	private Date birthDate;
	private String postalCode;
	private String postalCodeSpec;
	
	public UserProfileResult() {
		super();
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	//Mapping service from UserProfile
	public void fromObject(UserProfile userProfile) {
		
		setGender(userProfile.getGender());
		setBirthDate(userProfile.getBirthDate());
		setPostalCode(userProfile.getPostalCode());
		setPostalCodeSpec(userProfile.getPostalCodeSpec());
		
	}

	public void fromObject(User user) {

		setEmail(user.getEmail());
		setFirstName(user.getFirstName());
		setLastName(user.getLastName());
		setPhoto(user.getPhoto());
		
	}
}
