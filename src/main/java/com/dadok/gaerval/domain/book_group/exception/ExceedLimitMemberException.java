package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class ExceedLimitMemberException extends BusinessException {

	private final ErrorCode errorCode = ErrorCode.EXCEED_LIMIT_MEMBER;

	public ExceedLimitMemberException() {
		super(ErrorCode.EXCEED_LIMIT_MEMBER);
	}
}
