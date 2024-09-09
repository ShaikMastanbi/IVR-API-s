package com.spring.securityapplication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class UserDefinedException extends RuntimeException{
    private static final long SerialVersionUID=1L;
    public UserDefinedException(String message) {
        super(message);

    }
}
