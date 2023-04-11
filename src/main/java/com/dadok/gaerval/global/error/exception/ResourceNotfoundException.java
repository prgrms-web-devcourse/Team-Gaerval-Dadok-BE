package com.dadok.gaerval.global.error.exception;

import static com.dadok.gaerval.global.error.ErrorCode.*;

public class ResourceNotfoundException extends BusinessException {

	public ResourceNotfoundException(Class<?> resource) {
		super(RESOURCE_NOT_FOUND, String.format(RESOURCE_NOT_FOUND.getMessage(), resource.getName()));
	}

}
