package com.dadok.gaerval.domain.book_group.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

import com.dadok.gaerval.global.error.exception.BusinessException;

public class ExpiredJoinGroupPeriodException extends BusinessException {

	public ExpiredJoinGroupPeriodException() {
		super(EXPIRED_JOIN_GROUP);
	}

}
