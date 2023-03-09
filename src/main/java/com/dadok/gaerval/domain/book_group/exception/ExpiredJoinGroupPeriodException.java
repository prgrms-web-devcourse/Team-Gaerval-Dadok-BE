package com.dadok.gaerval.domain.book_group.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class ExpiredJoinGroupPeriodException extends BusinessException {

	private final ErrorCode errorCode = EXPIRED_JOIN_GROUP;

	public ExpiredJoinGroupPeriodException() {
		super(EXPIRED_JOIN_GROUP);
	}

	@Override
	public String getMessage() {
		return errorCode.getMessage();
	}

}
