package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class CannotDeleteMemberExistException extends BusinessException {

	public CannotDeleteMemberExistException() {
		super(ErrorCode.CANNOT_DELETE_MEMBER_EXIST);
	}

}
