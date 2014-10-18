package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserSendInvitationRequest extends BaseAuthenticatedRequest {

	private String content;
	private String emailList;	
	
	public UserSendInvitationRequest() {
		super();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEmailList() {
		return emailList;
	}

	public void setEmailList(String emailList) {
		this.emailList = emailList;
	}


}
