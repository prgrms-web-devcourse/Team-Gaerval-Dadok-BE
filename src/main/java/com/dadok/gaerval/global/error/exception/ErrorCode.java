package com.dadok.gaerval.global.error.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	UNAUTHORIZED_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A001", "유저가 존재하지 않습니다.");

	private final HttpStatus status;

	private final String code;

	private final String message;


}
