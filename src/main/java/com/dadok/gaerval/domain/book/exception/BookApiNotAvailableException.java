package com.dadok.gaerval.domain.book.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class BookApiNotAvailableException extends BusinessException {

	public BookApiNotAvailableException(ErrorCode errorCode) {
		super(errorCode);
	}

}

