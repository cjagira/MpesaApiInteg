package ke.co.esuite.db.persistence.domain;

import java.io.Serializable;
import java.util.Date;

public class ConfirmationData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String transactionType;
	private String transID;
	private Date transTime;
	private double transAmount;
	private String businessShortCode;
	private String billRefNumber;
	private String invoiceNumber;
	private double orgAccountBalance;
	private String thirdPartyTransID;
	private String msisdn;
	private String firstName;
	private String middleName;
	private String lastName;
	
	
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getTransID() {
		return transID;
	}
	public void setTransID(String transID) {
		this.transID = transID;
	}
	
	public String getBusinessShortCode() {
		return businessShortCode;
	}
	public void setBusinessShortCode(String businessShortCode) {
		this.businessShortCode = businessShortCode;
	}
	public String getBillRefNumber() {
		return billRefNumber;
	}
	public void setBillRefNumber(String billRefNumber) {
		this.billRefNumber = billRefNumber;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	
	public String getThirdPartyTransID() {
		return thirdPartyTransID;
	}
	public void setThirdPartyTransID(String thirdPartyTransID) {
		this.thirdPartyTransID = thirdPartyTransID;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Date getTransTime() {
		return transTime;
	}
	public void setTransTime(Date transTime) {
		this.transTime = transTime;
	}
	public double getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(double transAmount) {
		this.transAmount = transAmount;
	}
	public double getOrgAccountBalance() {
		return orgAccountBalance;
	}
	public void setOrgAccountBalance(double orgAccountBalance) {
		this.orgAccountBalance = orgAccountBalance;
	}
	
	
}
