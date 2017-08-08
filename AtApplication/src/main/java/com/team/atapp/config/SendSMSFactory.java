package com.team.atapp.config;

public class SendSMSFactory {

	public static String smsFlag ="atapp";
	
	private SendSMSFactory() {

	}

	public static SendSMS getSMSInstance() {
		if (smsFlag.equalsIgnoreCase("atapp")) {
			return (SendSMS) new SendSMSATAPP();
		}
		return null;

	}
}
