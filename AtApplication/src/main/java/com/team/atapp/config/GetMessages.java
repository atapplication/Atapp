package com.team.atapp.config;

public class GetMessages {

	public static String getLoginOtpMsg(String otp)
	{
		String message="";
			message="OTP for your login is "+otp+". Please do not share OTP, as it is confidential.";
				return message;
	}	
}
