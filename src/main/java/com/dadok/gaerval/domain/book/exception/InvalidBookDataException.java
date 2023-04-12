package com.dadok.gaerval.domain.book.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class InvalidBookDataException extends BusinessException {

	public InvalidBookDataException(ErrorCode errorCode) {
		super(errorCode.getMessage());
	}
}
