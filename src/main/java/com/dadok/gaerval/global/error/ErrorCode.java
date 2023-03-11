package com.dadok.gaerval.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	// GroupComment
	INVALID_COMMENT_NOT_PARENT(HttpStatus.BAD_REQUEST, "BGC1", "부모 댓글이 아닌 자식 댓글에는 댓글을 달 수 없습니다."),

	//User
	ALREADY_EXISTS_NICKNAME(HttpStatus.BAD_REQUEST, "U1", "이미 존재하는 닉네임입니다."),

	// Common
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C1", "%s 리소스가 존재하지 않습니다."),
	INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "C2", "%s 의 입력값이 잘못되었습니다. value : %s"),

	// Auth
	UNAUTHORIZED_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A1", "유저가 존재하지 않습니다."),
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A2", "잘못된 접근입니다. 유효한 토큰이 아닙니다."),

	// Bookshelf
	ALREADY_CONTAIN_BOOKSHELF_ITEM(HttpStatus.BAD_REQUEST, "BS1", "이미 책장에 포함된 아아템입니다."),
	BOOKSHELF_USER_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "BS2", "해당 책장에 올바르지 않은 사용자 접근입니다."),

	// Book
	BOOK_DATA_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "B1", "잘못된 도서 데이터 형식입니다."),
	// Book Comment
	ALREADY_CONTAIN_BOOK_COMMENT_MEMBER(HttpStatus.BAD_REQUEST, "BC2", "이미 남긴 도서 코멘트에 있습니다"),

	// BookGroup
	EXCEED_LIMIT_MEMBER(HttpStatus.BAD_REQUEST, "BG1", "모임 최대 인원을 초과하였습니다."),
	ALREADY_CONTAIN_BOOK_GROUP_MEMBER(HttpStatus.BAD_REQUEST, "BG2", "이미 모임에 참여한 사용자입니다."),

	NOT_MATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "BG3", "모임 비밀번호가 틀렸습니다."),
	BOOK_GROUP_OWNER_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "BG4", "해당 모임의 모임장 권한이 없는 사용자입니다."),

	EXPIRED_JOIN_GROUP(HttpStatus.BAD_REQUEST, "BG5", "모임 가입 기간이 지났습니다."),
	CANNOT_DELETE_MEMBER_EXIST(HttpStatus.BAD_REQUEST, "BG6", "멤버가 존재하는 모임은 삭제할 수 없습니다."),
	LESS_THAN_CURRENT_MEMBERS(HttpStatus.BAD_REQUEST, "BG7", "모임 최대 인원은 현재 참여 인원(%s 명)보다 작을 수 없습니다"),
	NOT_CONTAIN_BOOK_GROUP_MEMBER(HttpStatus.UNAUTHORIZED, "BG8", "모임에 참여하지않은 사용자 입니다."),
	NOT_MATCHED_COMMENT_AUTHOR(HttpStatus.FORBIDDEN, "BGC9", "요청자가 작성한 코멘트가 아닙니다.")

	;

	private final HttpStatus status;

	private final String code;

	private final String message;
}
