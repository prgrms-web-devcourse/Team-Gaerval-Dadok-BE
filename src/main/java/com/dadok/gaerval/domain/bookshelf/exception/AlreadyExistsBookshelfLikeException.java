package com.dadok.gaerval.domain.bookshelf.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class AlreadyExistsBookshelfLikeException extends BusinessException {

	public AlreadyExistsBookshelfLikeException(Long bookshelfId) {
		super(ALREADY_EXISTS_BOOKSHELF_LIKE, String.format(ALREADY_EXISTS_BOOKSHELF_LIKE.getMessage(), bookshelfId));
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

	@Override
	public ErrorCode getErrorCode() {
		return ALREADY_EXISTS_BOOKSHELF_LIKE;
	}
}
