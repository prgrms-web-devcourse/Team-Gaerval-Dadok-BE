package com.dadok.gaerval.global.error.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

public class InvalidArgumentException extends BusinessException {

	public InvalidArgumentException(String message) {
		super(INVALID_ARGUMENT, message);
	}

	public InvalidArgumentException(Object value, String valueName) {
		super(INVALID_ARGUMENT, String.format(INVALID_ARGUMENT.getMessage(), valueName, value));
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
