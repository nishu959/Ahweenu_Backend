package com.ecom.exceptions;

public class DataDuplicateException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String resourceName;
	String fieldName;
	public DataDuplicateException(String resourceName , String fieldName) {
		super(String.format("%s is already available for: %s", resourceName, fieldName));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
	}
	
	

}
