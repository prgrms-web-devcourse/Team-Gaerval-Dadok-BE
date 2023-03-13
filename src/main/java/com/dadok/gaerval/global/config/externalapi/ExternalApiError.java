package com.dadok.gaerval.global.config.externalapi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class ExternalApiError {
	private int code;
	private String msg;
}
