package com.spring.securityapplication.exceptions;

public class ApiException extends RuntimeException {

	public ApiException(String message) {
		super(message);

	}
	
	public ApiException() {
	
	}
	
}
