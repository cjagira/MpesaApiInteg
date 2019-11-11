package ke.co.esuite.mpesa.service;

import ke.co.esuite.mpesa.util.ApiResponse;

public interface MpesaService {

	public void registerMpesaUrl(String paybillNo);
	
	public void mpesaConfirmation(String confirmationData, String paybillNo);
	
	public void mpesaValidation(String validationData, String paybillNo);
	
	public ApiResponse processPayByMpesa(String paymentdata);
	
	public void processMpesaCallback(String callbackdata);
	
	public void simulateMpesaC2B(String simulateData, String PaybillNo);
	
}
