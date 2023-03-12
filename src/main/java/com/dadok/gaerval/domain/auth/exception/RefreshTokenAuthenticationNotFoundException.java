package com.dadok.gaerval.domain.auth.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.UnAuthenticationException;

public class RefreshTokenAuthenticationNotFoundException extends UnAuthenticationException {

	private ErrorCode errorCode = UNAUTHORIZED_REFRESH_TOKEN_NOT_FOUND;

	public RefreshTokenAuthenticationNotFoundException() {
		super(UNAUTHORIZED_REFRESH_TOKEN_NOT_FOUND);
		this.errorCode = UNAUTHORIZED_REFRESH_TOKEN_NOT_FOUND;
	}

	public RefreshTokenAuthenticationNotFoundException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

}
