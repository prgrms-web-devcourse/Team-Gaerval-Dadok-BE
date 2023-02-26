package com.dadok.gaerval.domain.bookshelf.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class AlreadyContainBookshelfItemException extends BusinessException {

	private ErrorCode errorCode = ErrorCode.ALREADY_CONTAIN_BOOKSHELF_ITEM;

	public AlreadyContainBookshelfItemException() {
		super(ErrorCode.ALREADY_CONTAIN_BOOKSHELF_ITEM);
	}
}
