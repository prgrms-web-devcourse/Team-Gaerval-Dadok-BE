package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class CannotLeaveGroupOwnerException extends BusinessException {
	private final ErrorCode errorCode = ErrorCode.CANNOT_LEAVE_GROUP_OWNER;

	public CannotLeaveGroupOwnerException() {
		super(ErrorCode.CANNOT_LEAVE_GROUP_OWNER);
	}

	@Override
	public String getMessage() {
		return errorCode.getMessage();
	}
}
