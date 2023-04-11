package com.dadok.gaerval.domain.book.exception;

import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.BusinessException;

import lombok.Getter;

public class InvalidBookDataException extends BusinessException {
	@Getter
	private final ErrorCode errorCode;

	public InvalidBookDataException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public InvalidBookDataException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}
}
