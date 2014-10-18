package com.veggiesbox.model.db;

import java.util.Date;

import com.google.appengine.api.datastore.Key;
import com.veggiesbox.model.UserProfileChangeRequest;
import com.veggiesbox.model.UserRegistrationRequest;
import com.veggiesbox.util.Constants;
import com.veggiesbox.util.PropertyManager;
import com.veggiesbox.util.Utils;

public class User {
	
	public final static String ENTITY = "User";
	
	private Key key;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String photo;
	private long referralCount;
	private long status;
	private long userSource;
	private boolean emailOptIn;
	private Key referrerKey;
	private Date createdDate;
	private Date lastAccessDate;

	public User() {
	}
			
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public long getReferralCount() {
		return referralCount;
	}

	public void setReferralCount(long referralCount) {
		this.referralCount = referralCount;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public Key getReferrerKey() {
		return referrerKey;
	}

	public void setReferrerKey(Key referrerKey) {
		this.referrerKey = referrerKey;
	}

	public long getUserSource() {
		return userSource;
	}

	public void setUserSource(long userSource) {
		this.userSource = userSource;
	}

	public boolean isEmailOptIn() {
		return emailOptIn;
	}
	
	public int getEmailOptIn() {
		if(isEmailOptIn())
			return 1;
		return 0;
	}

	public void setEmailOptIn(boolean emailOptIn) {
		this.emailOptIn = emailOptIn;
	}
	
	public void setEmailOptIn(long emailOptIn) {
		setEmailOptIn(emailOptIn == 1 ? true : false);
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastAccessDate() {
		return lastAccessDate;
	}

	public void setLastAccessDate(Date lastAccessnDate) {
		this.lastAccessDate = lastAccessnDate;
	}

	public void fromObject(UserRegistrationRequest urr) {

		//Set User values
		setEmail(urr.getEmail());
		setPassword(Utils.hashString(urr.getPassword()));
		setFirstName(urr.getFirstName());
		setLastName(urr.getLastName());
		//Referral Count starts with 0
		setReferralCount(0);
		setUserSource(Constants.USER_SOURCE_DIRECT);
		//User starts as not active
		setStatus(Constants.USER_STATUS_NEW);
		//User starts with Opt In
		setEmailOptIn(true);
		//Set internal fields for date
		setCreatedDate(new Date(System.currentTimeMillis()));
		setLastAccessDate(getCreatedDate());

	}
	
	public void fromObject(UserProfileChangeRequest upcr) {
		
		setFirstName(upcr.getFirstName());
		setLastName(upcr.getLastName());
		
		//If everything is set, profile is complete
		if(upcr.getBirthDate() == null)
			return;
		if(Utils.isNullOrBlank(upcr.getGender()))
			return;
		if(Utils.isNullOrBlank(upcr.getPostalCode()))
			return;
		
		setStatus(Constants.USER_STATUS_PROFILE_COMPLETE);
		
	}

	public void fromObject(com.restfb.types.User fbUser) {
		
		String photoUrl = PropertyManager.getProperty("fbPhotoUrl");
		photoUrl = photoUrl.replace("%1",fbUser.getUsername()); 
		setPhoto(photoUrl);
		
		//Set User values
		setEmail(fbUser.getEmail());
		setFirstName(fbUser.getFirstName());
		setLastName(fbUser.getLastName());
		//Referral Count starts with 0
		setReferralCount(0);
		setUserSource(Constants.USER_SOURCE_FACEBOOK);
		//User starts as  active
		setStatus(Constants.USER_STATUS_ACTIVE);
		//User starts with Opt In
		setEmailOptIn(true);
		//Set internal fields for date
		setCreatedDate(new Date(System.currentTimeMillis()));
		setLastAccessDate(getCreatedDate());
	}

}
