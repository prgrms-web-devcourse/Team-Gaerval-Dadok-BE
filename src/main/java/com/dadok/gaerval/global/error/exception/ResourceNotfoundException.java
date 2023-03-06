package com.dadok.gaerval.global.error.exception;

import com.dadok.gaerval.global.error.ErrorCode;

public class ResourceNotfoundException extends BusinessException {

	private ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;

	private Class<?> resource;

	public ResourceNotfoundException(Class<?> resource) {
		super(ErrorCode.RESOURCE_NOT_FOUND);
		this.resource = resource;
	}

	public String getMessage() {
		return String.format(this.errorCode.getMessage(), resource.getName());
	}

}
