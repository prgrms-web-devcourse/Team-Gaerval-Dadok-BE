package com.dadok.gaerval.global.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "spring.redis")
@AllArgsConstructor
public class RedisProperties {

	private final String host;

	private final int port;

	private final String password;

}
