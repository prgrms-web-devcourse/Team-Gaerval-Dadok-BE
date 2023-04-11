package com.dadok.gaerval.domain.book.exception;

import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

import lombok.Getter;

public class AlreadyContainBookCommentException extends BusinessException {
	@Getter
	private final ErrorCode errorCode;

	public AlreadyContainBookCommentException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

}
