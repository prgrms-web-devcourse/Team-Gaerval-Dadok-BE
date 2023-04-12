package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class NotContainBookGroupException extends BusinessException {

	public NotContainBookGroupException() {
		super(ErrorCode.NOT_CONTAIN_BOOK_GROUP_MEMBER);
	}
}
