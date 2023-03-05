package com.dadok.gaerval.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	// GroupComment
	INVALID_COMMENT_NOT_PARENT(HttpStatus.BAD_REQUEST, "C001", "부모 댓글이 아닌 자식 댓글에는 댓글을 달 수 없습니다."),

	//User
	ALREADY_EXISTS_NICKNAME(HttpStatus.BAD_REQUEST, "U001", "이미 존재하는 닉네임입니다."),

	// Common
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "%s 리소스가 존재하지 않습니다."),
	INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "I001", "%s 의 입력값이 잘못되었습니다. value : %s"),

	// Auth
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "잘못된 접근입니다."),
	UNAUTHORIZED_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A001", "유저가 존재하지 않습니다."),

	// Bookshelf
	ALREADY_CONTAIN_BOOKSHELF_ITEM(HttpStatus.BAD_REQUEST, "S001", "이미 책장에 포함된 아아템입니다."),
	BOOKSHELF_USER_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "S002", "해당 책장에 올바르지 않은 사용자 접근입니다."),

	// Book
	BOOK_DATA_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "B001", "잘못된 도서 데이터 형식입니다."),

	// BookGroup
	EXCEED_MAXIMUM_NUMBER_OF_MEMBER(HttpStatus.BAD_REQUEST, "G001", "모임 최대 인원을 초과하였습니다."),
	ALREADY_CONTAIN_BOOK_GROUP_MEMBER(HttpStatus.BAD_REQUEST, "G002", "이미 모임에 참여한 사용자입니다.");

	private final HttpStatus status;

	private final String code;

	private final String message;
	// Cannot comment on child comments
	// You can't post a child comment if it's not a parent comment.
}
