package com.team.atapp.notification;



public class SendMailFactory {
 
	public static String mailFlag = "wheelc";
 

	private SendMailFactory() {

	}

	public static SendMail getMailInstance() {
		if (mailFlag.equalsIgnoreCase("wheelc")) {
			return (SendMail) new SendGmail();
		}else{
			return (SendMail) new SendGmail();
		}
	}	
}
