package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class ExceedLimitMemberException extends BusinessException {

	private final ErrorCode errorCode = ErrorCode.EXCEED_LIMIT_MEMBER;

	private String message;

	public ExceedLimitMemberException() {
		super(ErrorCode.EXCEED_LIMIT_MEMBER);
		this.message = errorCode.getMessage();
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
