package com.ecom.payloads;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.entity.OrderAddress;
import com.ecom.entity.Orders;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class CommonUtil {
	
	
	@Value("${aws.s3.bucket.category}")
	private String categoryBucket;
	
	@Value("${aws.s3.bucket.product}")
	private String productBucket;
	
	@Value("${aws.s3.bucket.profile}")
	private String profileBucket;
	
	
	@Autowired
	private JavaMailSender mailSender;
	
	
	@PostConstruct
	public void sanitizeBuckets() {
	    categoryBucket = categoryBucket.trim().replace("\"", "");
	    productBucket  = productBucket.trim().replace("\"", "");
	    profileBucket  = profileBucket.trim().replace("\"", "");
	}
	

	
	public Boolean sendMail(String mail , String url) throws UnsupportedEncodingException, MessagingException{
		
		System.out.println(mail + " " +url);
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		helper.setFrom("nishantkumarsingh098@gmail.com", "Ahweenu");
		helper.setTo(mail);
		
		String content = "<p>Reset Password Url - </p>" + url;
		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);

		return true;
	}
	
	public Boolean sendMailProductOrderStatus(Orders orders) throws MessagingException, UnsupportedEncodingException {
		
		String mailContent = "<p>Order status updated : </p>"
				+ "<p>Product Details : </p>"
				+ "<p>Name : " + orders.getProduct().getTitle() + "</p>" 
				+ "<p>Category : " + orders.getProduct().getCategoryName() + "</p>" 
				+ "<p>Quantity : " + orders.getQuantity() +"</p>" 
				+ "<p>Price : "+ orders.getPrice() +"</p>" 
				+ "<p>Payment Type : "+ orders.getPaymentType() +"</p>";
		
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
	
		
		helper.setFrom("nishantkumarsingh098@gmail.com", "Ahweenu");
		helper.setTo(orders.getOrderAddress().getEmail());
		
		
		
		helper.setSubject("Order Status Updated.");
		helper.setText(mailContent, true);
		mailSender.send(message);

		return true;
	}
	
	
public Boolean sendMailOrderPlaced(OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException {
		
		String mailContent = "<p> Order placed Successfully : </p>"
				+ "<p>Order Details : </p>"
				+ "<p>Name : " + orderRequest.getFirstName() + " " + orderRequest.getLastName()  + "</p>" 
				+ "<p>Mobile Number : " + orderRequest.getMobileNumber() +"</p>"
				+ "<p>Address : " + orderRequest.getAddress() + " "+orderRequest.getCity() + " "+ orderRequest.getPinCode() + "</p>" 		 
				+ "<p>State : "+ orderRequest.getState() +"</p>";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		helper.setFrom("nishantkumarsingh098@gmail.com", "Ahweenu");
		helper.setTo(orderRequest.getEmail());
		
		
		helper.setSubject("Order Placed Successfully!");
		helper.setText(mailContent, true);
		mailSender.send(message);

		return true;
	}

	public String getImageUrl(MultipartFile file , Integer bucketType) {
		String bucketName = null;
		if(bucketType == 1) {
			bucketName = categoryBucket;
		} else if(bucketType == 2) {
			bucketName = productBucket;
		} else {
			bucketName = profileBucket;
		}
		
		String imageName = file!=null ? file.getOriginalFilename(): "default.png";
		
		String url = "https://" + bucketName + ".s3.eu-north-1.amazonaws.com/" + imageName;
		return url;
	}



}
