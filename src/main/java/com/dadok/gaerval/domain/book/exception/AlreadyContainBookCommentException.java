package com.dadok.gaerval.domain.book.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class AlreadyContainBookCommentException extends BusinessException {

	public AlreadyContainBookCommentException(ErrorCode errorCode) {
		super(errorCode.getMessage());
	}

}
