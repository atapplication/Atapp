package com.team.atapp.notification;

public interface SendMail {

	public void send(String address, String subject, String mailBody);
	public void send(String destadds[], String sub, String msg[],String cc[]);
	public void setBccs(String[] bccs);
	public void setCcs(String[] cc);
	public void send_invokedByRun(String address, String subject, String mailBody) throws Exception;
	
}
