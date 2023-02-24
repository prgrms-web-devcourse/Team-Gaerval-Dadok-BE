package com.dadok.gaerval.global.config.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties{

	private final String secretKey;

	private final int expirationSecond;

	private final String issuer;

	public JwtProperties(String secretKey, int expirationSecond, String issuer) {
		this.secretKey = secretKey;
		this.expirationSecond = expirationSecond;
		this.issuer = issuer;
	}
}
