package com.dadok.gaerval.domain.book.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class NotMarkedBookException extends BusinessException {

	public NotMarkedBookException() {
		super(ErrorCode.INVALID_COMMENT_NOT_BOOKMARK);
	}

	@Override
	public ErrorCode getErrorCode() {
		return this.getErrorCode();
	}
}
