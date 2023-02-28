package com.dadok.gaerval.global.error.exception;

public class InvalidArgumentException extends BusinessException {

	public InvalidArgumentException(String message) {
		super(message);
	}

	public InvalidArgumentException(Object value, String valueName) {
		super(String.format("%s의 입력값이 잘못되었습니다. value : %s", valueName, value));
	}

}
