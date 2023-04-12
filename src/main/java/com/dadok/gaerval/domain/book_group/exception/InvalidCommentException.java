package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class InvalidCommentException extends BusinessException {

	public InvalidCommentException() {
		super(ErrorCode.INVALID_COMMENT_NOT_PARENT);
	}

}
