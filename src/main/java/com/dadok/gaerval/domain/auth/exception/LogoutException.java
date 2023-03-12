package com.dadok.gaerval.domain.auth.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.UnAuthenticationException;

public class LogoutException extends UnAuthenticationException {

	private ErrorCode errorCode;

	public LogoutException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

}
