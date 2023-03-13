package com.dadok.gaerval.global.error;

import java.util.Arrays;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	// Comment
	INVALID_COMMENT_NOT_PARENT(HttpStatus.BAD_REQUEST, "C1", "부모 댓글이 아닌 자식 댓글에는 댓글을 달 수 없습니다."),
	NOT_MATCHED_COMMENT_AUTHOR(HttpStatus.FORBIDDEN, "C2", "해당 요청자가 작성한 코멘트가 아닙니다."),

	//User
	ALREADY_EXISTS_NICKNAME(HttpStatus.BAD_REQUEST, "U1", "이미 존재하는 닉네임입니다."),

	// Common
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C1", "%s 리소스가 존재하지 않습니다."),
	INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "C2", "%s 의 입력값이 잘못되었습니다. value : %s"),

	// Auth
	UNAUTHORIZED_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A1", "유저가 존재하지 않습니다."),
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A2", "잘못된 접근입니다. 유효한 토큰이 아닙니다."),
	UNAUTHORIZED_REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A3", "리프레시 토큰이 존재하지 않습니다. 다시 로그인 하세요"),
	EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A4", "액세스 토큰이 만료되었습니다. 리프레시 하거나 다시 로그인 해야 합니다."),
	EMPTY_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A5", "리프레시 토큰이 존재하지 않습니다."),
	MISMATCH_LOGOUT_AUTHENTICATION_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A6",
		"액세스 토큰이나, 리프레시 토큰이 존재하지 않아 잘못된 로그아웃 요청입니다."),

	// Bookshelf
	ALREADY_CONTAIN_BOOKSHELF_ITEM(HttpStatus.BAD_REQUEST, "BS1", "이미 책장에 포함된 아아템입니다."),
	BOOKSHELF_USER_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "BS2", "해당 책장에 올바르지 않은 사용자 접근입니다."),

	// Book
	BOOK_DATA_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "B1", "잘못된 도서 데이터 형식입니다."),
	BOOK_API_NOT_AVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR, "B2", "도서 API를 호출할 수 없습니다."),
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

	// KAKAO
	KAKAO_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "-1", "서버 내부에서 처리 중에 에러가 발생한 경우(해결 방법: 재시도)"),
	KAKAO_INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "-2",
		"필수 인자가 포함되지 않은 경우나 호출 인자값의 데이타 타입이 적절하지 않거나 허용된 범위를 벗어난 경우 (해결 방법: 요청 파라미터 확인)"),
	KAKAO_UNAUTHORIZED(HttpStatus.FORBIDDEN, "-5",
		"해당 API에 대한 요청 권한이 없는 경우(해결 방법: 해당 API의 이해하기 문서를 참고하여 검수 진행, 권한 획득 후 재호출)"),
	KAKAO_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "-7", "서비스 점검 또는 내부 문제가 있는 경우 (해결 방법: 해당 서비스 공지사항 확인)"),
	KAKAO_INVALID_HEADER(HttpStatus.BAD_REQUEST, "-8", "올바르지 않은 헤더로 요청한 경우(해결 방법: 요청 헤더 확인)"),
	KAKAO_SERVICE_END(HttpStatus.GONE, "-9", "서비스가 종료된 API를 호출한 경우(해결 방법: 공지 메일이나 데브톡 공지확인)"),
	KAKAO_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "-10",
		"허용된 요청 회수를 초과한 경우(해결 방법: 쿼터 및 제한 확인 후 쿼터 범위 내로 호출 조정, 필요시 데브톡으로 제휴 문의)"),
	KAKAO_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "401",
		"유효하지 않은 앱키나 액세스 토큰으로 요청한 경우, 등록된 앱 정보와 호출된 앱 정보가 불일치 하는 경우(해결 방법: 앱키 확인 또는 토큰 갱신, 개발자 사이트에 등록된 앱 정보 확인)"),
	KAKAO_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "603", "카카오 플랫폼 내부에서 요청 처리 중 타임아웃이 발생한 경우"),
	KAKAO_SERVICE_CHECK(HttpStatus.SERVICE_UNAVAILABLE, "9798", "서비스 점검중");

	private final HttpStatus status;

	private final String code;

	private final String message;

	public static ErrorCode fromCode(String code) {
		return Arrays.stream(ErrorCode.values())
			.filter(errorCode -> errorCode.getCode().equals(code))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("에러 코드를 찾을 수 없습니다.: " + code));
	}
}
