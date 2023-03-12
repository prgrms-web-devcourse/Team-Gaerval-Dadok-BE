package com.dadok.gaerval.global.util;

import java.util.UUID;

import org.springframework.stereotype.Component;
@Component
public class DefaultUUIDGenerator implements UUIDGenerator {

	@Override
	public UUID generate() {
		return UUID.randomUUID();
	}

	@Override
	public String generateToString() {
		return UUID.randomUUID().toString();
	}

	@Override
	public UUID fromString(String uuidString) {
		return UUID.fromString(uuidString);
	}

}
