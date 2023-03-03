package com.dadok.gaerval.infra.slack;

import com.dadok.gaerval.global.error.exception.BusinessException;

public class SlackException extends BusinessException {

	public SlackException(String message) {
		super(message);
	}

	public SlackException(String message, Throwable cause) {
		super(message, cause);
	}

}
