package com.veggiesbox.model;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserPurchaseRequest extends BaseAuthenticatedRequest {
	

	private HashMap<String,Long> purchaseValues;

	public UserPurchaseRequest() {
	}

	public HashMap<String, Long> getPurchaseValues() {
		return purchaseValues;
	}

	public void setPurchaseValues(HashMap<String, Long> values) {
		this.purchaseValues = values;
	}

	public boolean validate() {
		if(purchaseValues == null)
			return false;
		return true;
	}
}