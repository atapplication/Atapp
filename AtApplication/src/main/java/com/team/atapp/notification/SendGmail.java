/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team.atapp.notification;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.team.atapp.logger.AtLogger;
import com.team.atapp.service.impl.ConsumerInstrumentServiceImpl;

public class SendGmail implements Runnable, SendMail {
	
	private final AtLogger logger = AtLogger.getLogger(ConsumerInstrumentServiceImpl.class);
	public static String sourceMail = "wheelcare001@gmail.com";
	public static String password = "wheelcare@001";
	
	/*public static String sourceMail1 = "mightynotification@gmail.com";
	public static String password1 = "mighty@123";*/

	private String protocol = "smtp";
	private String host = "smtp.gmail.com";
	private String auth = "true";
	private String smtpPort = "465";
	private String socketFactoryPort = "465";
	private String socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
	private String socketFactoryFallback = "false";
	private String storeProtocol = "imaps";
	private String quitwait = "false";
	private String destadd = "";
	private String destadds[];
	private String sub = "";
	private String dat = "";
	private String bccs[] = null;
	private String ccs[]=null;
	private String msg[];

	private String sendingType = "single";

	private Exception exception = null;

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String pwd) {
		password = pwd;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getQuitwait() {
		return quitwait;
	}

	public void setQuitwait(String quitwait) {
		this.quitwait = quitwait;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSocketFactoryClass() {
		return socketFactoryClass;
	}

	public void setSocketFactoryClass(String socketFactoryClass) {
		this.socketFactoryClass = socketFactoryClass;
	}

	public String getSocketFactoryFallback() {
		return socketFactoryFallback;
	}

	public void setSocketFactoryFallback(String socketFactoryFallback) {
		this.socketFactoryFallback = socketFactoryFallback;
	}

	public String getSocketFactoryPort() {
		return socketFactoryPort;
	}

	public void setSocketFactoryPort(String socketFactoryPort) {
		this.socketFactoryPort = socketFactoryPort;
	}

	public static String getSourceMail() {
		return sourceMail;
	}

	public static void setSourceMail(String src) {
		  sourceMail =  src;
	}

	public String getStoreProtocol() {
		return storeProtocol;
	}

	public void setStoreProtocol(String storeProtocol) {
		this.storeProtocol = storeProtocol;
	}

 

	// ------- send to single
	public void send(String destadd, String sub, String dat) {

 //printMail(destadd, sub, dat);
		this.destadd = destadd;
		this.sendingType = "single";
		this.sub = sub;
		this.dat = dat;
		Thread t = new Thread(this);
		t.start(); 


	}
	
	public void send(String destadds[], String sub, String msg[],String cc[]) {

		 //printMail(destadd, sub, dat);
				this.destadds = destadds;
				this.sendingType = "multiple";
				this.sub = "Wheelcare: "+sub;
				this.msg = msg;
				this.ccs=cc;
				Thread t = new Thread(this);
				t.start(); 


			}

	public void run() {

		try {


			if(sendingType.equalsIgnoreCase("multiple"))
			{
				sendBulkMails(destadds, sub, msg, ccs);
			}
			else
			{

				send_invokedByRun(destadd, sub, dat);
			}
			 

		} catch (Exception e) {
			exception = e;
			System.out.println("exception : " + exception);
		}
	}

	// -----------Private methods invoked by run ..........
	// ------- send to single
	public void send_invokedByRun(String destadd, String sub, String dat)
			throws Exception {		
		logger.debug("Hiiiii"+destadd+sub+dat);
		
		Properties props = new Properties();
		props.put("mail.smtp.user", sourceMail);
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.auth", "true");
		//props.put("mail.smtp.debug", "true");		
		props.put("mail.smtp.socketFactory.port", smtpPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		SecurityManager security = System.getSecurityManager();
		
	
			Authenticator auth = new SMTPAuthenticator();
		Session session = Session.getInstance(props, auth);

		MimeMessage message = new MimeMessage(session);
		message.setSubject(sub);
		message.setFrom(new InternetAddress(sourceMail));

		Multipart multipart = new MimeMultipart("alternative");

		MimeBodyPart htmlPart = new MimeBodyPart();
		String htmlContent = "<html>" + dat + "</html>";
		htmlPart.setContent(htmlContent, "text/html");
				
		multipart.addBodyPart(htmlPart);
		
		/*MimeBodyPart imagePart = new MimeBodyPart();

		imagePart.attachFile("src/main/webapp/images/atom_logo.png");

		multipart.addBodyPart(imagePart);*/

        
		message.setContent(multipart);

		message.setRecipient(Message.RecipientType.TO, new InternetAddress(
				destadd));

		String bccString[] = getBccs();
		InternetAddress bcc[] = null;
		if (bccString != null && bccString.length > 0) {
			bcc = new InternetAddress[bccString.length ];
			for (int i = 0; i < bccString.length; i++) {
				bcc[i] = new InternetAddress(bccString[i]);
			}

			message.setRecipients(Message.RecipientType.BCC, bcc);
		} 
		
		
		String ccString[] = getCcs();
		InternetAddress cc[] = null;
		if (ccString != null && ccString.length > 0) {
			cc = new InternetAddress[ccString.length];
			for (int i = 0; i <ccString.length; i++) {
				cc[i] = new InternetAddress(ccString[i]);
			}
			message.setRecipients(Message.RecipientType.CC, cc);
		}

		Transport.send(message);

	}

		 

	private void printMail(String destadd, String sub, String dat) {
		System.out.println("address : " + destadd);
		String bccs[]=getBccs();
		String ccs[]=getCcs();
		if(bccs!=null&&bccs.length>0)
		{
			System.out.println("BCCs : ");
			for(String bcc: bccs)
			{
				System.out.print(bcc + "; " );
			}
		}
		if(ccs!=null&&ccs.length>0)
		{
			System.out.println("CCs : ");
			for(String cc: ccs)
			{
				System.out.print(cc + "; " );
			}
		}
		System.out.println("Subject : " + sub);
		System.out.println("Message : " + dat);
	}

	public void sendBulkMails(String destadd[], String sub, String dat[],String cc[])
			throws Exception {		
		Properties props = new Properties();
		props.put("mail.smtp.user", sourceMail);
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.auth", "true");
		//props.put("mail.smtp.debug", "true");		
		props.put("mail.smtp.socketFactory.port", smtpPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		SecurityManager security = System.getSecurityManager();
		
	
		Authenticator auth = new SMTPAuthenticator();
		Session session = Session.getInstance(props, auth);

		MimeMessage message; 
		Multipart multipart;
		MimeBodyPart htmlPart;
		String htmlContent ="";
		 Transport t = session.getTransport("smtp");
		    t.connect();
		    for (int m = 0; m<=destadd.length-1;m++) {
		    	htmlPart = new MimeBodyPart();
		    	 multipart= new MimeMultipart("alternative");
		    	htmlContent= "<html>" + dat[m] + "</html>";
		    	htmlPart.setContent(htmlContent, "text/html");
		    	multipart.addBodyPart(htmlPart);
		    	message= new MimeMessage(session);
				message.setSubject(sub);
				message.setFrom(new InternetAddress(sourceMail));
		    	message.setContent(multipart);

				message.setRecipient(Message.RecipientType.TO, new InternetAddress(
						destadd[m]));

				
				
				
				
					message.setRecipient(Message.RecipientType.CC, new InternetAddress(
							cc[m]));
				t.sendMessage(message, message.getAllRecipients());
			    }

			    t.close();
		
		

		 

	}


	public String[] getBccs() {
		return bccs;
	}

	public void setBccs(String bccs[]) {
		this.bccs = bccs;
	}

	public String[] getCcs() {
		return ccs;
	}

	public void setCcs(String ccs[]) {
		this.ccs = ccs;
	}

	
	
	private class SMTPAuthenticator extends javax.mail.Authenticator
	{
	public PasswordAuthentication getPasswordAuthentication()
	{
	return new PasswordAuthentication(sourceMail, password);
	}
	}
}

/*
 * String destadds[] = destadd.split(";"); InternetAddress to[] = new
 * InternetAddress[destadds.length]; int i = 0; for (String ss : destadds) {
 * to[i++] = new InternetAddress(ss.toString()); }
 */
