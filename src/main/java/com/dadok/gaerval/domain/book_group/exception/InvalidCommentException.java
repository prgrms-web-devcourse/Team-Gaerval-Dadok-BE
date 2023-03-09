package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class InvalidCommentException extends BusinessException {

	private final ErrorCode errorCode = ErrorCode.INVALID_COMMENT_NOT_PARENT;

	public InvalidCommentException(ErrorCode errorCode) {
		super(ErrorCode.INVALID_COMMENT_NOT_PARENT);
	}

}
