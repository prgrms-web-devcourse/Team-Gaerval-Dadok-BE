package com.dadok.gaerval.global.config.security.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

import com.dadok.gaerval.global.error.ErrorCode;

import lombok.Getter;

public class ExpiredAccessTokenException extends JwtAuthenticationException {

	@Getter
	private final ErrorCode errorCode = EXPIRED_ACCESS_TOKEN;

	public ExpiredAccessTokenException(Throwable cause) {
		super(EXPIRED_ACCESS_TOKEN, cause);
	}

}
