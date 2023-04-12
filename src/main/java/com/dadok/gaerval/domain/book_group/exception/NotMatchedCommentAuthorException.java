package com.dadok.gaerval.domain.book_group.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

import com.dadok.gaerval.global.error.exception.BusinessException;

public class NotMatchedCommentAuthorException extends BusinessException {

	public NotMatchedCommentAuthorException() {
		super(NOT_MATCHED_COMMENT_AUTHOR);
	}

}
