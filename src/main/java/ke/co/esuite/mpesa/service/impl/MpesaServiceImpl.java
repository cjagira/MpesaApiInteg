package ke.co.esuite.mpesa.service.impl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import ke.co.esuite.db.persistence.DbMapper;
import ke.co.esuite.db.persistence.domain.ConfirmationData;
import ke.co.esuite.db.persistence.domain.RegisterUrlData;
import ke.co.esuite.db.persistence.domain.StkPushCallbackData;
import ke.co.esuite.db.persistence.domain.StkPushData;
import ke.co.esuite.mpesa.service.MpesaService;
import ke.co.esuite.mpesa.util.ApiResponse;

@Service
public class MpesaServiceImpl implements MpesaService {

	@Autowired
	private Environment env;

	@Autowired
	private DbMapper dbMapper;

	@Override
	public void registerMpesaUrl(String paybillNo) {

		String registerUrl = env.getRequiredProperty("mpesa.register_url");
		String authUrl = env.getRequiredProperty("mpesa.auth_url");
		String consumerKey = env.getRequiredProperty("mpesa.consumer_key");
		String consumerSecret = env.getRequiredProperty("mpesa.consumer_secret");

		String token = this.getApiToken(consumerKey, consumerSecret, authUrl);
		ApiResponse res = new ApiResponse();

		String registerToken = env.getRequiredProperty("mpesa.confirm_token");
		String baseUrl = env.getRequiredProperty("mpesa.base_url");
		String confirmationUrl = String.format(baseUrl + "mpesa/" + paybillNo + "/confirmation?token=%s",
				registerToken);
		String validationUrl = String.format(baseUrl + "mpesa/" + paybillNo + "/validation?token=%s", registerToken);
		String responseType = "completed";

		String data = String.format(
				"{\"ShortCode\":\"%s\",\"ResponseType\":\"%s\",\"ConfirmationURL\":\"%s\",\"ValidationURL\":\"%s\"}",
				paybillNo, responseType, confirmationUrl, validationUrl);

		try {
			SSLContext sc = this.getSSLContexts();
			CloseableHttpClient client = HttpClients.custom().setSSLContext(sc)
					.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

			HttpPost post = new HttpPost(String.format(registerUrl));
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			post.setHeader("Authorization", "Bearer " + token);
			post.setEntity(new StringEntity(data));

			CloseableHttpResponse response = client.execute(post);

			res.setStatus(response.getStatusLine().getStatusCode());
			res.setMessage(response.toString());
			res.setData(EntityUtils.toString(response.getEntity()));

			System.out.println("Register url api" + res.getData());

			System.out.println("Confirmation url : " + confirmationUrl + "\n VAlidation url : " + validationUrl);

			if (res.getStatus() == 200) {
				// Save info in db
				RegisterUrlData regData = new RegisterUrlData();
				regData.setPaybillNo(paybillNo);
				regData.setConfirmationUrl(confirmationUrl);
				regData.setValidationUrl(validationUrl);
				regData.setRegisteredDate(new Date());

				dbMapper.saveMpesaRegistrationUrls(regData);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void mpesaConfirmation(String confirmationData, String paybillNo) {
		/*For daraja
		 * { "TransactionType": "Pay Bill", "TransID": "MFJ41H5B9Q",
		 * "TransTime": "20180619161541", "TransAmount": "1.00",
		 * "BusinessShortCode": "603035", "BillRefNumber": "test",
		 * "InvoiceNumber": "", "OrgAccountBalance": "105111.00",
		 * "ThirdPartyTransID": "0", "MSISDN": "254708374149", "FirstName":
		 * "John", "MiddleName": "J.", "LastName": "Doe" }
		 */

		/* For kopokopo
		 * { "service_name" :"MPESA", "business_number" :"888555",
		 * "transaction_reference" :"DE45GK45", "internal_transaction_id" :3222,
		 * "transaction_timestamp" :"2013-03-18T13:57:00Z", "transaction_type"
		 * :"Paybill", "account_number" :"445534", "sender_phone"
		 * :"+254903119111", "first_name" :"John", "middle_name" :"K",
		 * "last_name" :"Doe", "amount" :4000, "currency" :"KES" "signature"
		 * :"opopopopop" }
		 */

		ConfirmationData data = new ConfirmationData();
		try {
			JSONObject obj = new JSONObject(confirmationData);

			if (paybillNo.equalsIgnoreCase("kopokopo")) {
				data.setTransactionType(obj.getString("transaction_type"));
				data.setTransID(obj.getString("transaction_reference"));
				data.setTransTime(
						new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
						.parse(obj.getString("transaction_timestamp")));
				data.setTransAmount(obj.getDouble("amount"));
				data.setBusinessShortCode(obj.getString("business_number"));
				data.setBillRefNumber(obj.getString("internal_transaction_id"));
				data.setInvoiceNumber(obj.getString("transaction_reference"));
				data.setThirdPartyTransID("kopokopo");
				data.setMsisdn(obj.getString("sender_phone").replace("+", ""));
				data.setFirstName(obj.getString("first_name"));
				data.setMiddleName(obj.getString("middle_name"));
				data.setLastName(obj.getString("last_name"));
			} else {
				data.setTransactionType(obj.getString("TransactionType"));
				data.setTransID(obj.getString("TransID"));
				data.setTransTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(obj.getString("TransTime")));
				data.setTransAmount(Double.parseDouble(obj.getString("TransAmount")));
				data.setBusinessShortCode(obj.getString("BusinessShortCode"));
				data.setBillRefNumber(obj.getString("BillRefNumber"));
				data.setInvoiceNumber(obj.getString("InvoiceNumber"));
				data.setOrgAccountBalance(Double.parseDouble(obj.getString("OrgAccountBalance")));
				data.setThirdPartyTransID(obj.getString("ThirdPartyTransID"));
				data.setMsisdn(obj.getString("MSISDN"));
				data.setFirstName(obj.getString("FirstName"));
				data.setMiddleName(obj.getString("MiddleName"));
				data.setLastName(obj.getString("LastName"));
			}

			// insert to db
			dbMapper.saveMpesaConfirmation(data);

			if (env.getRequiredProperty("mpesa.trigger_ext_app").equals("true")) {
				this.triggerExternalApplication(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void mpesaValidation(String validationData, String paybillNo) {
		// TODO Auto-generated method stub

	}

	public String getApiToken(String consumerKey, String consumerSecret, String authUrl) {
		String accessToken = "";
		ApiResponse res = new ApiResponse();
		try {
			SSLContext sc = this.getSSLContexts();
			CloseableHttpClient client = HttpClients.custom().setSSLContext(sc)
					.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

			String appKeySecret = consumerKey + ":" + consumerSecret;
			byte[] bytes = Base64.encodeBase64(appKeySecret.getBytes("ISO-8859-1"));
			String auth = new String(bytes).replace("\n", "").replace("\r", "");

			System.out.println("auth = " + auth);

			HttpGet get = new HttpGet(String.format(authUrl));
			get.setHeader("Authorization", "Basic " + auth);
			CloseableHttpResponse response = client.execute(get);

			res.setStatus(response.getStatusLine().getStatusCode());
			res.setMessage(response.toString());
			res.setData(EntityUtils.toString(response.getEntity()));

			if (res.getStatus() == 200) {
				/*
				 * { "access_token": "rE5Olfyz1kQsycpUYjKeQQkTsAGS",
				 * "expires_in": "3599" }
				 */

				System.out.println(res.getData());
				JSONObject jsonObject = new JSONObject(res.getData());
				accessToken = (String) jsonObject.get("access_token");

				System.out.println("accessToken=" + accessToken);
			} else {
				System.out.println("fail \n" + response.toString());
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return accessToken;
	}

	private SSLContext getSSLContexts() {
		SSLContext s = null;
		try {
			s = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return s;
	}

	private void triggerExternalApplication(ConfirmationData data) {

		try {
			ApiResponse res = new ApiResponse();
			String externalAppToken = env.getRequiredProperty("mpesa.ext_app_token");
			String externalUrl = env.getRequiredProperty("mpesa.ext_app_url") + "?token=" + externalAppToken;

			/*
			 * {"phoneNumber":"254724008507", "amount" : "1000",
			 * "transId":"MPER99838KF"}
			 */
			String json = String.format("{\"phoneNumber\":\"%s\",\"amount\":\"%f\",\"transId\":\"%s\"}",
					data.getMsisdn(), data.getTransAmount(), data.getTransID());

			// invoke the post
			CloseableHttpClient client = HttpClients.createDefault();

			HttpPost post = new HttpPost(String.format(externalUrl));
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(json));

			CloseableHttpResponse response = client.execute(post);

			res.setStatus(response.getStatusLine().getStatusCode());
			res.setMessage(response.toString());
			res.setData(EntityUtils.toString(response.getEntity()));

			System.out.println("response" + res.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void simulateMpesaC2B(String simulateData, String PaybillNo) {
		/*
		 * { "ShortCode": "601504", "Amount": "123", "Phone": "254708374149",
		 * "RefNumber": "Test1234" }
		 */

		String simulateC2Burl = env.getRequiredProperty("mpesa.simulate_c2b_url");
		String authUrl = env.getRequiredProperty("mpesa.auth_url");
		String consumerKey = env.getRequiredProperty("mpesa.consumer_key");
		String consumerSecret = env.getRequiredProperty("mpesa.consumer_secret");

		String token = this.getApiToken(consumerKey, consumerSecret, authUrl);
		ApiResponse res = new ApiResponse();
		try {
			JSONObject obj = new JSONObject(simulateData);
			String data = String.format(
					"{\"ShortCode\":\"%s\",\"CommandID\":\"CustomerPayBillOnline\",\"Amount\":\"%s\",\"Msisdn\":\"%s\",\"BillRefNumber\":\"%s\"}",
					obj.getString("ShortCode"), obj.getString("Amount"), obj.getString("Phone"),
					obj.getString("RefNumber"));

			SSLContext sc = this.getSSLContexts();
			CloseableHttpClient client = HttpClients.custom().setSSLContext(sc)
					.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

			HttpPost post = new HttpPost(String.format(simulateC2Burl));
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			post.setHeader("Authorization", "Bearer " + token);
			post.setEntity(new StringEntity(data));

			CloseableHttpResponse response = client.execute(post);

			res.setStatus(response.getStatusLine().getStatusCode());
			res.setMessage(response.toString());
			res.setData(EntityUtils.toString(response.getEntity()));

			System.out.println("Simulate url api" + res.getData());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* MPESA STK Push */
	@Override
	public ApiResponse processPayByMpesa(String paymentdata) {
		ApiResponse res = new ApiResponse();

		/*
		 * { "PaybillNo": "174379", "Amount": "1", "PhoneNumber":
		 * "254726752478", "AccountReference": "2699909", "Description":"Test" }
		 */
		String stkPushUrl = env.getRequiredProperty("mpesa.stkpush.stkpush_url");
		String callbackUrl = env.getRequiredProperty("mpesa.stkpush.callback_url");
		String authUrl = env.getRequiredProperty("mpesa.auth_url");
		String consumerKey = env.getRequiredProperty("mpesa.stkpush.consumer_key");
		String consumerSecret = env.getRequiredProperty("mpesa.stkpush.consumer_secret");
		String passKey = env.getRequiredProperty("mpesa.stkpush.pass_key");

		String token = this.getApiToken(consumerKey, consumerSecret, authUrl);

		try {
			JSONObject obj = new JSONObject(paymentdata);

			String timestamp = this.getStringTimeStamp();
			String password = this.getEncodePasswordString(obj.getString("PaybillNo"), passKey, timestamp);
			String phone = obj.getString("PhoneNumber").replaceAll("[^0-9]", "");
			String data = String.format(
					"{\"BusinessShortCode\": \"%s\", \"Password\": \"%s\",\"Timestamp\": \"%s\", "
							+ "\"TransactionType\": \"CustomerPayBillOnline\",\"Amount\": \"%d\","
							+ "\"PartyA\": \"%s\",\"PartyB\": \"%s\",\"PhoneNumber\": \"%s\","
							+ "\"CallBackURL\": \"%s\",\"AccountReference\": \"%s\",\"TransactionDesc\": \"%s\"}",
					obj.getString("PaybillNo"), password, timestamp, obj.getInt("Amount"), phone,
					obj.getString("PaybillNo"), phone, callbackUrl, obj.getString("AccountReference"),
					obj.getString("Description"));

			System.out.println("To mpesa: " + data);

			SSLContext sc = this.getSSLContexts();
			CloseableHttpClient client = HttpClients.custom().setSSLContext(sc)
					.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

			HttpPost post = new HttpPost(String.format(stkPushUrl));
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			post.setHeader("Authorization", "Bearer " + token);
			post.setEntity(new StringEntity(data));

			CloseableHttpResponse response = client.execute(post);

			res.setStatus(response.getStatusLine().getStatusCode());
			res.setMessage(response.toString());
			res.setData(EntityUtils.toString(response.getEntity()));

			System.out.println("STK Pust api" + res.getData());

			if (res.getStatus() == 200) {
				/*
				 * { "MerchantRequestID": "14067-3209998-2",
				 * "CheckoutRequestID": "ws_CO_DMZ_44778157_28062018130520976",
				 * "ResponseCode": "0", "ResponseDescription":
				 * "Success. Request accepted for processing",
				 * "CustomerMessage": "Success. Request accepted for processing"
				 * }
				 */

				// Save info in db
				JSONObject resObj = new JSONObject(res.getData());

				StkPushData stkPushData = new StkPushData();
				stkPushData.setAccountReference(obj.getString("AccountReference"));
				stkPushData.setAmount(obj.getDouble("Amount"));
				stkPushData.setDescription(obj.getString("Description"));
				stkPushData.setPaybillNo(obj.getString("PaybillNo"));
				stkPushData.setPhoneNumber(obj.getString("PhoneNumber"));

				stkPushData.setMerchantRequestID(resObj.getString("MerchantRequestID"));
				stkPushData.setCheckoutRequestID(resObj.getString("CheckoutRequestID"));
				stkPushData.setResponseCode(resObj.getString("ResponseCode"));
				stkPushData.setResponseDescription(resObj.getString("ResponseDescription"));
				stkPushData.setCustomerMessage(resObj.getString("CustomerMessage"));
				dbMapper.saveStkPush(stkPushData);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	@Override
	public void processMpesaCallback(String callbackdata) {

		// Get callback into object
		try {
			// A cancelled request
			/*
			 * { "Body":{ "stkCallback":{ "MerchantRequestID":"8555-67195-1",
			 * "CheckoutRequestID":"ws_CO_27072017151044001", "ResultCode":1032,
			 * "ResultDesc":"[STK_CB - ]Request cancelled by user" } } }
			 */
			StkPushCallbackData callbackData = new StkPushCallbackData();
			JSONObject resObj = new JSONObject(callbackdata);
			JSONObject stkCallback = resObj.getJSONObject("Body").getJSONObject("stkCallback");

			callbackData.setCheckoutRequestID(stkCallback.getString("CheckoutRequestID"));
			callbackData.setMerchantRequestID(stkCallback.getString("MerchantRequestID"));
			callbackData.setResultCode(stkCallback.getString("ResultCode"));
			callbackData.setResultDesc(stkCallback.getString("ResultDesc"));

			if (stkCallback.getInt("ResultCode") == 0) {
				JSONArray callbackItems = stkCallback.getJSONObject("CallbackMetadata").getJSONArray("Item");

				for (int i = 0; i < callbackItems.length(); i++) {
					JSONObject obj = callbackItems.getJSONObject(i);
					String name = obj.getString("Name");

					if (name.equals("Amount")) {
						callbackData.setAmount(obj.getDouble("Value"));
					} else if (name.equals("MpesaReceiptNumber")) {
						callbackData.setReceiptNumber(obj.getString("Value"));
					} else if (name.equals("TransactionDate")) {
						callbackData.setTransDate(String.valueOf(obj.getLong("Value")));
					} else if (name.equals("PhoneNumber")) {
						callbackData.setPhoneNumber(String.valueOf(obj.getLong("Value")));
					}
				}

			}
			dbMapper.updateStkPushCallback(callbackData);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getStringTimeStamp() {
		String timeStampString = null;

		Calendar now = Calendar.getInstance();
		String dd = String.valueOf(now.get(Calendar.DATE));
		String mm = String.valueOf(now.get(Calendar.MONTH) + 1);
		String yyyy = String.valueOf(now.get(Calendar.YEAR));
		String hh = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
		String mi = String.valueOf(now.get(Calendar.MINUTE));
		String ss = String.valueOf(now.get(Calendar.SECOND));

		if (dd.length() == 1) {
			dd = "0" + dd;
		}

		if (mm.length() == 1) {
			mm = "0" + mm;
		}

		if (hh.length() == 1) {
			hh = "0" + hh;
		}

		if (mi.length() == 1) {
			mi = "0" + mi;
		}

		if (ss.length() == 1) {
			ss = "0" + ss;
		}

		timeStampString = yyyy + mm + dd + hh + mi + ss;

		return timeStampString;
	}

	private String getEncodePasswordString(String shortcode, String passkey, String timestamp) {

		String encodePasswordString = null;
		try {
			String password = shortcode + passkey + timestamp;
			System.out.println(password);
			byte[] bytes = Base64.encodeBase64(password.getBytes("ISO-8859-1"));
			encodePasswordString = new String(bytes).replace("\n", "").replace("\r", "");

			System.out.println(encodePasswordString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return encodePasswordString;
	}
}
