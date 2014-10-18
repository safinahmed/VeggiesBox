package com.veggiesbox.model.db;

import java.util.Date;
import java.util.HashMap;

import com.google.appengine.api.datastore.Key;
import com.veggiesbox.model.UserPurchaseRequest;

public class UserPurchase {

	public final static String ENTITY = "UserPurchase";
	
	private Key key;
	private Key userKey;
	private HashMap<String,Long> purchaseValues;
	private Date createdDate;
	
	public UserPurchase() {
		super();
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

	public HashMap<String, Long> getPurchaseValues() {
		return purchaseValues;
	}

	public void setPurchaseValues(HashMap<String, Long> purchaseValues) {
		this.purchaseValues = purchaseValues;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void fromObject(UserPurchaseRequest urr) {
		this.purchaseValues = urr.getPurchaseValues();
		setCreatedDate(new Date(System.currentTimeMillis()));
	}


}
