package com.veggiesbox.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProducerRegistrationRequest {

	private String name;
	private String postalAddress;
	private String phoneNumber;
	private String email;
	private String website;
	private String whichProducts;
	private long sellCabaz;
	private long sellMercado;
	private long sellGrosso;
	private long sellFarm;
	private String sellExtra;
	
	public ProducerRegistrationRequest() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPostalAddress() {
		return postalAddress;
	}
	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getWhichProducts() {
		return whichProducts;
	}
	public void setWhichProducts(String whichProducts) {
		this.whichProducts = whichProducts;
	}

	public long getSellCabaz() {
		return sellCabaz;
	}

	public void setSellCabaz(long sellCabaz) {
		this.sellCabaz = sellCabaz;
	}

	public long getSellMercado() {
		return sellMercado;
	}

	public void setSellMercado(long sellMercado) {
		this.sellMercado = sellMercado;
	}

	public long getSellGrosso() {
		return sellGrosso;
	}

	public void setSellGrosso(long sellGrosso) {
		this.sellGrosso = sellGrosso;
	}

	public long getSellFarm() {
		return sellFarm;
	}

	public void setSellFarm(long sellFarm) {
		this.sellFarm = sellFarm;
	}

	public String getSellExtra() {
		return sellExtra;
	}

	public void setSellExtra(String sellExtra) {
		this.sellExtra = sellExtra;
	}
	
}
