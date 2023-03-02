package com.dadok.gaerval.global.error.exception;

import com.dadok.gaerval.global.error.ErrorCode;

public class DuplicateException extends BusinessException{

	public DuplicateException(ErrorCode errorCode) {
		super(errorCode);
	}

	public DuplicateException(Throwable cause) {
		super(cause);
	}

	public DuplicateException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

}
