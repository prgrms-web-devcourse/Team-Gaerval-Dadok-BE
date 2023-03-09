package com.dadok.gaerval.domain.book.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class AlreadyContainBookCommentException extends BusinessException {

	private final ErrorCode errorCode = ErrorCode.ALREADY_CONTAIN_BOOK_COMMENT_MEMBER;

	public AlreadyContainBookCommentException() {
		super(ErrorCode.ALREADY_CONTAIN_BOOK_COMMENT_MEMBER);
	}

	@Override
	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
