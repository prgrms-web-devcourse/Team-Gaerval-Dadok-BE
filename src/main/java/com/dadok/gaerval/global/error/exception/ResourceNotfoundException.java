package com.dadok.gaerval.global.error.exception;

import com.dadok.gaerval.global.error.ErrorCode;

public class ResourceNotfoundException extends BusinessException {

	private ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;

	private Class<?> resource;

	public ResourceNotfoundException() {
		super(ErrorCode.RESOURCE_NOT_FOUND);
		this.resource = null;
	}

	public ResourceNotfoundException(Class<?> resource) {
		super(ErrorCode.RESOURCE_NOT_FOUND);
		this.resource = resource;
	}

}
