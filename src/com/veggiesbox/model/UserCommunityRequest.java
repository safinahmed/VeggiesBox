package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.veggiesbox.model.db.UserCommunity;
import com.veggiesbox.util.Utils;

@XmlRootElement
public class UserCommunityRequest extends BaseAuthenticatedRequest {
	
	private long location;
	private String postalCode;
	private String extraInfo;

	public UserCommunityRequest() {
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

	public boolean validate() {
		if(location == UserCommunity.OTHER)
			if(Utils.isNullOrBlank(extraInfo))
				return false;
		return true;
	}
}
