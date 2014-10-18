package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.veggiesbox.model.db.User;
import com.veggiesbox.util.Constants;
import com.veggiesbox.util.PropertyManager;

@XmlRootElement
public class UserBarResult extends BaseResult {

	private String photo;
	private String firstName;
	private String lastName;
	private boolean profileComplete;
	
	public UserBarResult() {
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

	public boolean isProfileComplete() {
		return profileComplete;
	}

	public void setProfileComplete(boolean profileComplete) {
		this.profileComplete = profileComplete;
	}

	public void fromObject(User user) {
		setFirstName(user.getFirstName());
		setPhoto(user.getPhoto() == null ? "" : user.getPhoto()); //Avoid null
		setLastName(user.getLastName());	
		setProfileComplete(user.getStatus() < Constants.USER_STATUS_PROFILE_COMPLETE ? false : true); //false for not complete, true for complete 
	}	
}
