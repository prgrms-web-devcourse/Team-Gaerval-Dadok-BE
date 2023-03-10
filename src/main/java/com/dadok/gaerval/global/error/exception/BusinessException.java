package com.dadok.gaerval.global.error.exception;

import com.dadok.gaerval.global.error.ErrorCode;

import lombok.Getter;

public class BusinessException extends RuntimeException {

	@Getter
	private ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}


	public BusinessException(ErrorCode errorCode, String message) {
		this.errorCode = errorCode;
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(ErrorCode errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

}
