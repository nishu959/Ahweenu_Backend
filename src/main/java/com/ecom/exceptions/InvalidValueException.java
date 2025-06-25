package com.ecom.exceptions;

public class InvalidValueException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String resourceName;
	String fieldName;
	Integer value;
	
	public InvalidValueException(String resourceName, String fieldName, Integer value) {
		super(String.format("%s %s can't be %d", resourceName, fieldName, value));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.value = value;
	}
	
	

}
