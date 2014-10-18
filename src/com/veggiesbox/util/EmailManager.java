package com.veggiesbox.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.appengine.labs.repackaged.com.google.common.base.Splitter;
import com.veggiesbox.model.db.Producer;

//Service responsible for Sending Emails
public class EmailManager {
		
	private static final Logger log = Logger.getLogger(Utils.class.getName());
	
	private static final List<Long> refThresholds = Arrays.asList(3L,5L,10L,25L,50L,100L);
	
	//Send a new Producer Registration Email
	public static void sendNewProducerEmail(String toEmail, Producer prd) {

		String content = Utils.getFileContents("WEB-INF/emails/newProducer.html");
		
		//Replace email template placeholders, with actual values
		content = content.replace("{FARM_NAME}", prd.getName());
		content = content.replace("{FARM_EMAIL}", prd.getEmail());
		
		//Email subject
		String subject = "Bemvindo à Veggiesbox";		
		
		sendEmail(prd.getEmail(), subject, content);
		
		//Get email template
		String internalContent = Utils.getFileContents("WEB-INF/emails/newProducerInternal.html");
		
		//Replace email template placeholders, with actual values
		internalContent = internalContent.replace("{FARM_NAME}", prd.getName());
		internalContent = internalContent.replace("{FARM_EMAIL}", prd.getEmail());
		
		//Email subject
		String internalSubject = "Registo de Novo Produtor";
				
		//Send email
		sendEmail(PropertyManager.getProperty("internalToEmail"), internalSubject, internalContent);
		
		
	}
	
	public static void sendActivationEmail(String toEmail, String activationId) {
		
		//Get email template
		String content = Utils.getFileContents("WEB-INF/emails/userActivation.html");
		
		//Replace email template placeholders, with actual values
		content = content.replace("{ACTIVATION_ID}", activationId);

		//Email subject
		String subject = "Bemvindo à Veggiesbox";
				
		//Send email
		sendEmail(toEmail, subject, content);
		
	}
	
	public static void sendInvitationEmail(String emailList, String content) {
		
		List<String> toList = Splitter.on(',').splitToList(emailList);
		
		String subject = "Convite Veggiesbox";
		
		sendEmail(toList, subject, content);
		
	}
	
	public static void sendReferralEmail(String toEmail, long count) {
		
		//Get email template
		String content = Utils.getFileContents("WEB-INF/emails/referralThreshold.html");
		
		//Replace email template placeholders, with actual values
		content = content.replace("{COUNT}", count+"");

		//Email subject
		String subject = "Parabéns";
				
		//Send email
		sendEmail(toEmail, subject, content);
	}
	
	public static void sendNewPasswordEmail(String toEmail, String password) {
		
		//Get email template
		String content = Utils.getFileContents("WEB-INF/emails/resetPassword.html");
		
		//Replace email template placeholders, with actual values
		content = content.replace("{PASSWORD}", password);

		//Email subject
		String subject = "Veggiesbox Recuperação da Password";
				
		//Send email
		sendEmail(toEmail, subject, content);
	}
	
	//Internal method to send email to one recipient, calls generic method
	private static void sendEmail(String toEmail, String subject, String content) {
		
		if(Utils.isNullOrBlank(toEmail)) {
			log.warning("Trying to send email to empty recipient");
		}
		
		List<String> emailList = new ArrayList<String>();
		emailList.add(toEmail);
		sendEmail(emailList,subject,content);
	}
	
	//Internal method to send email
    private static void sendEmail(List<String> toEmail, String subject, String content) {

        try {
        
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
        	
        	//Create the default message
            MimeMessage msg = new MimeMessage(session);
           
            //Sets who the email is from (from Properties file)
            msg.setFrom(new InternetAddress(PropertyManager.getProperty("fromEmail"), PropertyManager.getProperty("fromDesc")));
          
            //Adds recipient from List of Recipient String
            for(String to : toEmail) { 
            	if(Utils.isValidEmailAddress(to)) {
	            	msg.addRecipient(Message.RecipientType.TO,
	            			new InternetAddress(to, to));
            	}
            }
          
            //Set subject
            msg.setSubject(subject,"UTF-8");
            
            //Create Multipart to send HTML email
            Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html; charset=UTF-8");
            mp.addBodyPart(htmlPart);
            msg.setContent(mp);
            
            //Send email ??
            if(!Constants.IS_DEBUG)
            	Transport.send(msg);
            else
            	log.warning("Email " + content);

        } catch (Exception e) {
            log.severe("Could not send email " + e.getMessage());
        } 
    }
    
    public static boolean reachedThreshold(long count) {
    	return refThresholds.contains(count);
    }
    	
}
