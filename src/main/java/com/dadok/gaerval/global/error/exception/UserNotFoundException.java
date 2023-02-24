package com.dadok.gaerval.global.error.exception;

import org.springframework.security.core.AuthenticationException;

import lombok.Getter;

public class UserNotFoundException extends AuthenticationException {

	@Getter
	private ErrorCode errorCode = ErrorCode.UNAUTHORIZED_USER_NOT_FOUND;

	public UserNotFoundException() {
		super(ErrorCode.UNAUTHORIZED_USER_NOT_FOUND.getMessage());
	}

	public UserNotFoundException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}


}
