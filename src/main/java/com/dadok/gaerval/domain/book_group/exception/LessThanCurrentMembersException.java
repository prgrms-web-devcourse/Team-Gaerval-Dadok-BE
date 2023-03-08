package com.dadok.gaerval.domain.book_group.exception;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

public class LessThanCurrentMembersException extends BusinessException {
	private final ErrorCode errorCode = ErrorCode.LESS_THAN_CURRENT_MEMBERS;

	private final int currentMembersCount;

	public LessThanCurrentMembersException(int currentMembersCount) {
		super(ErrorCode.LESS_THAN_CURRENT_MEMBERS);
		this.currentMembersCount = currentMembersCount;
	}

	@Override
	public String getMessage() {
		return String.format(errorCode.getMessage(), currentMembersCount);
	}
}
