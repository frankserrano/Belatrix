package com.belatrix.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {

	public void sendEmail(Properties prop) {
		Session session = Session.getInstance(prop, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(prop.getProperty("mail.username"), prop.getProperty("mail.password"));
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(prop.getProperty("mail.username")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("mail.to")));
			message.setSubject(prop.getProperty("mail.subject"));
			message.setText(prop.getProperty("mail.message"));
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
