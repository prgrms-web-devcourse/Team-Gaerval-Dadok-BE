package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class CannotDeleteMemberExistException extends BusinessException {

	private final ErrorCode errorCode = ErrorCode.CANNOT_DELETE_MEMBER_EXIST;

	public CannotDeleteMemberExistException() {
		super(ErrorCode.CANNOT_DELETE_MEMBER_EXIST);
	}
}
