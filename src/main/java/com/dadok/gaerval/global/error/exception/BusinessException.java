package com.dadok.gaerval.global.error.exception;

public class BusinessException extends RuntimeException{

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}
}
