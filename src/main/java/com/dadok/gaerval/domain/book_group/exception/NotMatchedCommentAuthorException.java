package com.dadok.gaerval.domain.book_group.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class NotMatchedCommentAuthorException extends BusinessException {

	private final ErrorCode errorCode = NOT_MATCHED_COMMENT_AUTHOR;

	public NotMatchedCommentAuthorException() {
		super(NOT_MATCHED_COMMENT_AUTHOR);
	}

	@Override
	public ErrorCode getErrorCode() {
		return this.errorCode;
	}

	@Override
	public String getMessage() {
		return this.errorCode.getMessage();
	}

}
