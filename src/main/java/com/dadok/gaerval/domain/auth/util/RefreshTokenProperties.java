package com.dadok.gaerval.domain.auth.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "refresh-token")
@AllArgsConstructor
public class RefreshTokenProperties {

	private int expirationSeconds;

}
