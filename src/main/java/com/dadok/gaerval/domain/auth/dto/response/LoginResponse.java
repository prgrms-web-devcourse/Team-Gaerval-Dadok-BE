package com.dadok.gaerval.domain.auth.dto.response;

public record LoginResponse(
	String accessToken,
	String refreshToken)
{}
