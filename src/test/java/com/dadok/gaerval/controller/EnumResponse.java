package com.dadok.gaerval.controller;

public record EnumResponse<T>(
	T data
) {

	public static <T> EnumResponse<T> of(T data) {
		return new EnumResponse<>(data);
	}

}
