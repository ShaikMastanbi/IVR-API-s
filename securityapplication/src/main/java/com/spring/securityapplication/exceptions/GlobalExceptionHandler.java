package com.spring.securityapplication.exceptions;



import com.spring.securityapplication.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;


@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
	
	
	//
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse> APiExceptionHandler(ApiException ex){
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message,true);
		//ApiResponse apiResponse = new ApiResponse();
		
		return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ApiResponse> NoSuchElementExceptionHandler(NoSuchElementException ex){
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message,true);
		//ApiResponse apiResponse = new ApiResponse();
		
		return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(io.jsonwebtoken.SignatureException.class)
	public ResponseEntity<ApiResponse> NoSignatureInvalidException(io.jsonwebtoken.SignatureException ex){
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message,true);
		//ApiResponse apiResponse = new ApiResponse();

		return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(UserDefinedException.class)
	public ResponseEntity<ApiResponse> UserDefainedExceptionHandler(UserDefinedException ex){
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message,true);
		//ApiResponse apiResponse = new ApiResponse();

		return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.BAD_REQUEST);
	}

	//UserDefinedException
	
	
}
