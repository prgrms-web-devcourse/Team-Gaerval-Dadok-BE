package com.dadok.gaerval.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	// Common
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "%s 리소스가 존재하지 않습니다."),

	// Auth
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효한 형식의 JWT 토큰이 아닙니다."),
	UNAUTHORIZED_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A001", "유저가 존재하지 않습니다."),

	// Bookshelf
	ALREADY_CONTAIN_BOOKSHELF_ITEM(HttpStatus.BAD_REQUEST, "S001", "이미 책장에 포함된 아아템입니다."),
	BOOKSHELF_USER_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "S002", "해당 책장에 올바르지 않은 사용자 접근입니다.");

	private final HttpStatus status;

	private final String code;

	private final String message;

}
