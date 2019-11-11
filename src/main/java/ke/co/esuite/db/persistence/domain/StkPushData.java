package ke.co.esuite.db.persistence.domain;

import java.io.Serializable;

public class StkPushData implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String phoneNumber;
	private String paybillNo;
    private double amount; 
    private String accountReference;
    private String description;
    private String merchantRequestID;
    private String checkoutRequestID;
    private String responseDescription;
    private String responseCode;
    private String customerMessage;
	
	
	public String getMerchantRequestID() {
		return merchantRequestID;
	}
	public void setMerchantRequestID(String merchantRequestID) {
		this.merchantRequestID = merchantRequestID;
	}
	public String getCheckoutRequestID() {
		return checkoutRequestID;
	}
	public void setCheckoutRequestID(String checkoutRequestID) {
		this.checkoutRequestID = checkoutRequestID;
	}
	public String getResponseDescription() {
		return responseDescription;
	}
	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getCustomerMessage() {
		return customerMessage;
	}
	public void setCustomerMessage(String customerMessage) {
		this.customerMessage = customerMessage;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPaybillNo() {
		return paybillNo;
	}
	public void setPaybillNo(String paybillNo) {
		this.paybillNo = paybillNo;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getAccountReference() {
		return accountReference;
	}
	public void setAccountReference(String accountReference) {
		this.accountReference = accountReference;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
