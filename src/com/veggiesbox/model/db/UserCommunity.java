package com.veggiesbox.model.db;

import com.google.appengine.api.datastore.Key;
import com.veggiesbox.model.UserCommunityRequest;

public class UserCommunity {
	
	public final static long WORK = 0;
	public final static long HOME = 1;
	public final static long SCHOOL = 2;
	public final static long GYM = 3;
	public final static long OTHER = 4;
	
	public final static String ENTITY = "UserCommunity";
	
	public Key key;
	public Key userKey;
	
	public long location;
	public String extraInfo;
	public String postalCode;
	
	public UserCommunity() {
		
	}
	
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getUserKey() {
		return userKey;
	}

	public void setUserKey(Key userKey) {
		this.userKey = userKey;
	}

	public long getLocation() {
		return location;
	}

	public void setLocation(long location) {
		this.location = location;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public void fromObject(UserCommunityRequest ucr) {
		setLocation(ucr.getLocation());
		setPostalCode(ucr.getPostalCode());
		setExtraInfo(ucr.getExtraInfo());
	}

}
