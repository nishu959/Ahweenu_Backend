package com.ecom;

import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		        mailSender.setHost("smtp.gmail.com");
		        mailSender.setPort(587);
		        mailSender.setUsername("nishantkumarsingh098@gmail.com");
		        mailSender.setPassword("oyuqugalqgxnkcad");

		        Properties props = mailSender.getJavaMailProperties();
		        props.put("mail.transport.protocol", "smtp");
		        props.put("mail.smtp.auth", "true");
		        props.put("mail.smtp.starttls.enable", "true");
		        props.put("mail.debug", "true");

		        SimpleMailMessage message = new SimpleMailMessage();
		        message.setFrom("nishantkumarsingh098@gmail.com");
		        message.setTo("NISHANTKUMARSINGH098@GMAIL.COM");
		        message.setSubject("Test Mail");
		        message.setText("This is a test email from Java");

		        try {
		            mailSender.send(message);
		            System.out.println("Email sent successfully.");
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }


}




