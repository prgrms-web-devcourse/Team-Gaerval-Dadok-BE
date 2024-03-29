package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class BookGroupOwnerNotMatchedException extends BusinessException {

	public BookGroupOwnerNotMatchedException() {
		super(ErrorCode.BOOK_GROUP_OWNER_NOT_MATCHED);
	}

}
