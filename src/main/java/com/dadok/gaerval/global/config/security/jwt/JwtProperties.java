package com.dadok.gaerval.global.config.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class JwtProperties{

	private final String secretKey;

	private final int expirationSecond;

	private final String issuer;

	public JwtProperties(
		@Value("${jwt.secret-key}") String secretKey,
		@Value("${jwt.expiration-second}") int expirationSecond,
		@Value("${jwt.issuer}") String issuer) {
		this.secretKey = secretKey;
		this.expirationSecond = expirationSecond;
		this.issuer = issuer;
	}
}
