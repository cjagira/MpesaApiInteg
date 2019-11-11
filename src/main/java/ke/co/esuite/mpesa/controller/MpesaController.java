package ke.co.esuite.mpesa.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ke.co.esuite.mpesa.service.MpesaService;

@RestController
@RequestMapping("/service")
public class MpesaController {

	@Autowired
	private MpesaService mpesaService;

	@Autowired
	private Environment env;

	@RequestMapping(value = "mpesa/registerUrl", method = RequestMethod.POST)
	public ResponseEntity<String> mpesaUrlRegistration(@RequestBody String request) {

		String paybillNo;
		String resJson;
		ResponseEntity<String> res;
		try {
			System.out.println("Register url: " + request);

			JSONObject jsonObject = new JSONObject(request); // "{\"paybillNo\":\"603035\"}"
			paybillNo = (String) jsonObject.get("paybillNo");

			mpesaService.registerMpesaUrl(paybillNo);

			resJson = String.format("{\"message\":\"paybill %s registered successfully\"}", paybillNo);

			res = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(resJson);
		} catch (Exception e) {
			e.printStackTrace();
			resJson = String.format("{\"message\":\"Error: %s\"}", e.getMessage());
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(resJson);
		}

		return res;
	}

	@RequestMapping(value = "mpesa/{paybill}/confirmation", method = RequestMethod.POST)
	public ResponseEntity<String> mpesaConfirmation(@PathVariable String paybill, @RequestParam String token,
			@RequestBody String request) {
		/*
		 * { "TransactionType": "Pay Bill", "TransID": "MFJ41H5B9Q",
		 * "TransTime": "20180619161541", "TransAmount": "1.00",
		 * "BusinessShortCode": "603035", "BillRefNumber": "test",
		 * "InvoiceNumber": "", "OrgAccountBalance": "105111.00",
		 * "ThirdPartyTransID": "0", "MSISDN": "254708374149", "FirstName":
		 * "John", "MiddleName": "J.", "LastName": "Doe" }
		 */
		ResponseEntity<String> res;
		try {
			System.out.println(request);
			if (token.equals(env.getRequiredProperty("mpesa.confirm_token"))) {
				mpesaService.mpesaConfirmation(request, paybill);
			} else {
				System.out.println("Error: Wrong token used!!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			String json = "{\"ResultCode\":0,\"ResultDesc\":\"Confirmation received successfully\"}";

			res = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(json);
		}
		return res;
	}

	@RequestMapping(value = "kopokopo/confirmation", method = RequestMethod.POST)
	public ResponseEntity<String> mpesaConfirmationKopoKopo(@RequestBody String request) {
		/*
		 * { "service_name" => "MPESA", "business_number" => "888555",
		 * "transaction_reference" => "DE45GK45", "internal_transaction_id" =>
		 * 3222, "transaction_timestamp" => "2013-03-18T13:57:00Z",
		 * "transaction_type" => "Paybill", "account_number" => "445534",
		 * "sender_phone" => "+254903119111", "first_name" => "John",
		 * "middle_name" => "K", "last_name" => "Doe", "amount" => 4000,
		 * "currency" => "KES" "signature" => "opopopopop" }
		 */
		ResponseEntity<String> res;
		try {
			System.out.println(request);
			mpesaService.mpesaConfirmation(request, "kopokopo");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * { "status" : "01", "description" : "Accepted",
			 * "subscriber_message" : "Message to send to subscriber" }
			 */
			String json = "{\"status\":\"01\",\"description\":\"Accepted\"}";

			res = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(json);
		}
		return res;
	}

	@RequestMapping(value = "mpesa/{paybill}/validation", method = RequestMethod.POST)
	public ResponseEntity<String> mpesaValidation(@PathVariable String paybill, @RequestParam String token,
			@RequestBody String request) {
		/*
		 * { "TransactionType": "", "TransID": "MFJ41H5B9Q", "TransTime":
		 * "20180619161541", "TransAmount": "1.00", "BusinessShortCode":
		 * "603035", "BillRefNumber": "test", "InvoiceNumber": "",
		 * "OrgAccountBalance": "", "ThirdPartyTransID": "", "MSISDN":
		 * "254708374149", "FirstName": "John", "MiddleName": "J.", "LastName":
		 * "Doe" }
		 */
		ResponseEntity<String> res;
		try {
			System.out.println("Mpesa Validate: " + request);
			mpesaService.mpesaValidation(request, paybill);

			String json = "{\"ResultCode\":0, \"ResultDesc\":\"Success\", \"ThirdPartyTransID\": 0}";
			res = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(json);
		} catch (Exception e) {
			e.printStackTrace();
			String json = "{\"ResultCode\":1, \"ResultDesc\":\"Failed\", \"ThirdPartyTransID\": 0}";
			res = ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(json);
		}

		return res;
	}

	@RequestMapping(value = "mpesa/{paybill}/c2bsimulate", method = RequestMethod.POST)
	public ResponseEntity<String> mpesaValidation(@PathVariable String paybill, @RequestBody String request) {

		ResponseEntity<String> res;
		try {
			System.out.println("Mpesa Validate: " + request);
			mpesaService.simulateMpesaC2B(request, paybill);

			String json = "{\"ResultCode\":0, \"ResultDesc\":\"Success\", \"ThirdPartyTransID\": 0}";
			res = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(json);
		} catch (Exception e) {
			e.printStackTrace();
			String json = "{\"ResultCode\":1, \"ResultDesc\":\"Failed\", \"ThirdPartyTransID\": 0}";
			res = ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(json);
		}

		return res;
	}

	@RequestMapping(value = "mpesa/payonline", method = RequestMethod.POST)
	public ResponseEntity<String> mpesaPayOnline(@RequestBody String request) {
		/*
		 * { "PaybillNo": "174379", "Amount": "1", "PhoneNumber":
		 * "254726752478", "AccountReference": "2699909", "Description":"Test" }
		 */
		ResponseEntity<String> res;
		try {
			System.out.println("Mpesa Stk push: " + request);

			// TODO Validate request
			if (this.validateMpesaPayOnline(request)) {
				mpesaService.processPayByMpesa(request);

				String json = "{\"ResultCode\":0, \"ResultDesc\":\"Success: Complete process on your phone\", \"ThirdPartyTransID\": 0}";
				res = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(json);
			} else {
				String json = "{\"ResultCode\":0, \"ResultDesc\":\"Error: Invalid request. (Check Account Reference < 200, Transaction Description < 200 , Amount between 1/= and 70,000/= and Phone number (+254XXXXXXXXX))\", \"ThirdPartyTransID\": 0}";
				res = ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String json = "{\"ResultCode\":1, \"ResultDesc\":\"Failed: " + e.getMessage()
					+ "\", \"ThirdPartyTransID\": 0}";
			res = ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(json);
		}

		return res;
	}

	@RequestMapping(value = "mpesa/payonline/callback", method = RequestMethod.POST)
	public ResponseEntity<String> mpesaPayOnlineCallback(@RequestBody String request) {
		/*
		 * // A cancelled request { "Body":{ "stkCallback":{
		 * "MerchantRequestID":"8555-67195-1",
		 * "CheckoutRequestID":"ws_CO_27072017151044001", "ResultCode":1032,
		 * "ResultDesc":"[STK_CB - ]Request cancelled by user" } } }
		 * 
		 * // An accepted request { "Body":{ "stkCallback":{
		 * "MerchantRequestID":"19465-780693-1",
		 * "CheckoutRequestID":"ws_CO_27072017154747416", "ResultCode":0,
		 * "ResultDesc":"The service request is processed successfully.",
		 * "CallbackMetadata":{ "Item":[ { "Name":"Amount", "Value":1 }, {
		 * "Name":"MpesaReceiptNumber", "Value":"LGR7OWQX0R" }, {
		 * "Name":"Balance" }, { "Name":"TransactionDate",
		 * "Value":20170727154800 }, { "Name":"PhoneNumber",
		 * "Value":254721566839 } ] } } } }
		 */
		ResponseEntity<String> res;
		try {
			System.out.println("Mpesa Stk Callback: " + request);
			mpesaService.processMpesaCallback(request);

			String json = "{\"ResultCode\":0, \"ResultDesc\":\"Success\", \"ThirdPartyTransID\": 0}";
			res = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(json);
		} catch (Exception e) {
			e.printStackTrace();
			String json = "{\"ResultCode\":1, \"ResultDesc\":\"Failed\", \"ThirdPartyTransID\": 0}";
			res = ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(json);
		}

		return res;
	}

	@RequestMapping(value = "mpesa/update", method = RequestMethod.POST)
	public ResponseEntity<String> updateAction(@RequestBody String request) {
		ResponseEntity<String> res;
		String resJson;
		try {
			// String validToken =
			// env.getRequiredProperty("mpesa.access_token");
			System.out.println("Data: " + request);

			resJson = String.format("{\"message\":\"Success\"}");
			res = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(resJson);

		} catch (Exception e) {
			e.printStackTrace();
			resJson = String.format("{\"message\":\"Error: %s\"}", e.getMessage());
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(resJson);
		}

		return res;
	}

	private boolean validateMpesaPayOnline(String request) {
		boolean ret = false;
		try {
			JSONObject obj = new JSONObject(request);
			int amount = obj.getInt("Amount");
			String phone = obj.getString("PhoneNumber").replaceAll("[^0-9]", "");
			String account = obj.getString("AccountReference");
			String desc = obj.getString("Description");

			if (amount < 0 || amount > 70000) {
				return false;
			} else if (phone.length() != 12) {
				return false;
			} else if (account.length() > 50) {
				return false;
			} else if (desc.length() > 200) {
				return false;
			} else {
				ret = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

}
