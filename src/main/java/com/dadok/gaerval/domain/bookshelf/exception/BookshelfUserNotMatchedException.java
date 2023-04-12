package com.dadok.gaerval.domain.bookshelf.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class BookshelfUserNotMatchedException extends BusinessException {

	public BookshelfUserNotMatchedException() {
		super(ErrorCode.BOOKSHELF_USER_NOT_MATCHED);
	}
}
