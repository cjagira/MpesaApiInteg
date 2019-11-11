package ke.co.esuite.db.persistence.domain;

import java.io.Serializable;
import java.util.Date;

public class RegisterUrlData implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String paybillNo;	
	private String confirmationUrl;
	private String validationUrl;
	private Date registeredDate;
	
	
	public RegisterUrlData() {
		super();
	}
	
	public RegisterUrlData(String paybillNo, String confirmationUrl, String validationUrl, Date registeredDate) {
		super();
		this.paybillNo = paybillNo;
		this.confirmationUrl = confirmationUrl;
		this.validationUrl = validationUrl;
		this.registeredDate = registeredDate;
	}
	
	public String getPaybillNo() {
		return paybillNo;
	}
	public void setPaybillNo(String paybillNo) {
		this.paybillNo = paybillNo;
	}
	public String getConfirmationUrl() {
		return confirmationUrl;
	}
	public void setConfirmationUrl(String confirmationUrl) {
		this.confirmationUrl = confirmationUrl;
	}
	public String getValidationUrl() {
		return validationUrl;
	}
	public void setValidationUrl(String validationUrl) {
		this.validationUrl = validationUrl;
	}
	public Date getRegisteredDate() {
		return registeredDate;
	}
	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}
	
	
	

}
