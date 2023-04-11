package com.dadok.gaerval.domain.user.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

import com.dadok.gaerval.global.error.exception.DuplicateException;

public class DuplicateNicknameException extends DuplicateException {

	public DuplicateNicknameException() {
		super(ALREADY_EXISTS_NICKNAME);
	}

	public DuplicateNicknameException(Throwable cause) {
		super(ALREADY_EXISTS_NICKNAME, cause);
	}

}
