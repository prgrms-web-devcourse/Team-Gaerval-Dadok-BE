package com.dadok.gaerval.domain.auth.token;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@RedisHash(value = "refreshToken")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

	@Id
	private String token; // uuid

	private Long userId;

	@TimeToLive
	private long expiration;

	private List<String> roles;

	public RefreshToken(String token, Long userId, long expiration, List<String> roles) {
		this.token = token;
		this.userId = userId;
		this.expiration = expiration;
		this.roles = roles;
	}

}
