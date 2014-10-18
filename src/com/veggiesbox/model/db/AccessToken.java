package com.veggiesbox.model.db;

import java.util.Date;

import com.google.appengine.api.datastore.Key;

public class AccessToken {
	
	public final static String ENTITY = "AccessToken";
	
	public Key key;
	public Key userKey;

	public Date createdDate;
	public Date lastUsageDate;
	
	public AccessToken() {
		
	}
	
	public AccessToken(Key userKey) {
		this.userKey = userKey;
		createdDate = new Date(System.currentTimeMillis());
		lastUsageDate = createdDate;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastUsageDate() {
		return lastUsageDate;
	}

	public void setLastUsageDate(Date lastUsageDate) {
		this.lastUsageDate = lastUsageDate;
	}
	
	

}
