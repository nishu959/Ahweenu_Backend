package com.ecom.exceptions;

public class AccountLockedBadRequestException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	String resourceName;
	String fieldName;
	public AccountLockedBadRequestException(String resourceName , String fieldName) {
		super(String.format("Account is Locked with %s = %s", resourceName, fieldName));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
	}

}
