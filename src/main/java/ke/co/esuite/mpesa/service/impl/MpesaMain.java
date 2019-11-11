package ke.co.esuite.mpesa.service.impl;

public class MpesaMain {

	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String paybillNo = "603035";
		MpesaServiceImpl mp = new MpesaServiceImpl();
		mp.registerMpesaUrl(paybillNo);
	}

}
