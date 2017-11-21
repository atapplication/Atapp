package com.team.atapp.notification;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.team.atapp.logger.AtLogger;
import com.team.atapp.service.impl.ConsumerInstrumentServiceImpl;

@Service
public class MailMail {

@Autowired	
private JavaMailSender  mailSender; 

private final AtLogger logger = AtLogger.getLogger(ConsumerInstrumentServiceImpl.class);

public void sendMail(final String from,final String to, final String subject,final String msg) {  
    //creating message  
	try{
			MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {  
		        
		        public void prepare(MimeMessage mimeMessage) throws Exception {
		        	
		        	Multipart multipart = new MimeMultipart("alternative");
		    		MimeBodyPart htmlPart = new MimeBodyPart();
		    		String htmlContent = "<html>" + msg + "</html>";
		    		htmlPart.setContent(htmlContent, "text/html");
		    		multipart.addBodyPart(htmlPart);
		    		
		                         		
			           mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(to));  
			           mimeMessage.setFrom(new InternetAddress(from));  
			           mimeMessage.setSubject(subject); 
			           mimeMessage.setContent(multipart);
			           //mimeMessage.setText(msg);  
		        }  
			}; 
			
			mailSender.send(messagePreparator);  
			
	}catch(Exception e){
		logger.error("/MailError",e);
	}
			
	
	
   /* SimpleMailMessage message = new SimpleMailMessage();  
    message.setFrom(from);  
    message.setTo(to);  
    message.setSubject(subject);  
    message.setText(msg);  
    //sending message  
    mailSender.send(message);     */
}  

}