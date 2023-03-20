package com.dadok.gaerval.domain.book.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class BookNotMarkedException extends BusinessException {

	private static final ErrorCode errorCode = ErrorCode.INVALID_COMMENT_NOT_BOOKMARK;

	public BookNotMarkedException() {
		super(errorCode);
	}

	@Override
	public ErrorCode getErrorCode() {
		return super.getErrorCode();
	}
}
