package com.dadok.gaerval.global.config.security.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.UnAuthenticationException;

import lombok.Getter;

public class JwtAuthenticationException extends UnAuthenticationException {

	@Getter
	private final ErrorCode errorCode;

	public JwtAuthenticationException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public JwtAuthenticationException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
		this.errorCode = errorCode;
	}

}
