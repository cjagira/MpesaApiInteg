package ke.co.esuite.db.persistence.domain;

import java.io.Serializable;

public class StkPushCallbackData implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String merchantRequestID;
    private String checkoutRequestID;
    private String resultDesc;
    private String resultCode;
    
    private String receiptNumber;
    private String transDate;
    private double amount;
    private String phoneNumber;
    
    
    
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
	public String getResultDesc() {
		return resultDesc;
	}
	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getReceiptNumber() {
		return receiptNumber;
	}
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
    
    
    
    

}
