package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class NotMatchedPasswordException extends BusinessException {

	public NotMatchedPasswordException() {
		super(ErrorCode.NOT_MATCHED_PASSWORD);
	}

}