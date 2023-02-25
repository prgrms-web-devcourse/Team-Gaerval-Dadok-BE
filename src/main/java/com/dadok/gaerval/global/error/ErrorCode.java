package com.dadok.gaerval.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "%s 리소스가 존재하지 않습니다."),
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효한 형식의 JWT 토큰이 아닙니다."),
	UNAUTHORIZED_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A001", "유저가 존재하지 않습니다.");

	private final HttpStatus status;

	private final String code;

	private final String message;


}
