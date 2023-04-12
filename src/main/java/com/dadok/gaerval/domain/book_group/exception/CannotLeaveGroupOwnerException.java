package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class CannotLeaveGroupOwnerException extends BusinessException {

	public CannotLeaveGroupOwnerException() {
		super(ErrorCode.CANNOT_LEAVE_GROUP_OWNER);
	}

}
