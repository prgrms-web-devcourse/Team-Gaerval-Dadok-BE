package com.dadok.gaerval.domain.book_group.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class JoinLimitException extends BusinessException {

	private final ErrorCode errorCode = JOIN_LIMIT;

	public JoinLimitException() {
		super(JOIN_LIMIT);
	}

}
