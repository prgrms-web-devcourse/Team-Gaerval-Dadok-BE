package com.dadok.gaerval.domain.book.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class BookApiNotAvailableException extends BusinessException {

	private final ErrorCode errorCode;

	public BookApiNotAvailableException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	@Override
	public ErrorCode getErrorCode() {
		return errorCode;
	}

}

