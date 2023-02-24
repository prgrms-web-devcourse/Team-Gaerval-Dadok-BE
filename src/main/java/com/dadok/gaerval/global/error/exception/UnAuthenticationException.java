package com.dadok.gaerval.global.error.exception;

import org.springframework.security.core.AuthenticationException;

import com.dadok.gaerval.global.error.ErrorCode;

import lombok.Getter;

public class UnAuthenticationException extends AuthenticationException {

	@Getter
	private ErrorCode errorCode;

	public UnAuthenticationException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public UnAuthenticationException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

}
