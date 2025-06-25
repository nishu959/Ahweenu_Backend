package com.ecom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsConfig {
	
	@Value("${aws.access.key}")
	private String accessKey;
	
	@Value("${aws.secret.key}")
	private String secretKey;
	
	@Value("${aws.region}")
	private String region;
	
	@Bean
	public AmazonS3 amazonS3() {
		
		
		
		String cleanRegion = region.trim().replace("\"", "");
		String cleanAccessKey = accessKey.trim().replace("\"", "");
		String cleanSecretKey = secretKey.trim().replace("\"", "");
		
		String s3Endpoint = String.format("https://s3.%s.amazonaws.com", cleanRegion);
		BasicAWSCredentials credentials =new BasicAWSCredentials(cleanAccessKey, cleanSecretKey);
		
//		String s3Endpoint = String.format("https://s3.%s.amazonaws.com", region);
		
		System.out.println(s3Endpoint);
		
		
		return AmazonS3ClientBuilder.standard().
		withEndpointConfiguration(new EndpointConfiguration(s3Endpoint, cleanRegion))
	    .withPathStyleAccessEnabled(true).
		withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
	}
	
	
	
	
	
}
