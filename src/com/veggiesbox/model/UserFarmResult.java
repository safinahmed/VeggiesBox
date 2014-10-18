package com.veggiesbox.model;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserFarmResult extends BaseResult {

	private String referralCount;
	private String referralId;
	
	public UserFarmResult() {
		super();
	}

	public String getReferralCount() {
		return referralCount;
	}
	
	public void setReferralCount(String referralCount) {
		this.referralCount = referralCount;
	}

	public void setReferralCount(long referralCount) {
		this.referralCount = referralCount+"";
	}

	public String getReferralId() {
		return referralId;
	}

	public void setReferralId(String referralId) {
		this.referralId = referralId;
	}

	
	
}
