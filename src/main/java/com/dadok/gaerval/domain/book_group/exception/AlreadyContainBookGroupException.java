package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class AlreadyContainBookGroupException extends BusinessException {

	private final ErrorCode errorCode = ErrorCode.ALREADY_CONTAIN_BOOK_GROUP_MEMBER;

	public AlreadyContainBookGroupException() {
		super(ErrorCode.ALREADY_CONTAIN_BOOK_GROUP_MEMBER);
	}

	@Override
	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
