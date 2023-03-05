package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class ExceedMaximumNumberOfMemberException extends BusinessException {
	public ExceedMaximumNumberOfMemberException() {
		super(ErrorCode.EXCEED_MAXIMUM_NUMBER_OF_MEMBER);
	}
}
