package com.dadok.gaerval.global.util;

import java.util.UUID;

public interface UUIDGenerator {

	UUID generate();

	String generateToString();

	UUID fromString(String uuidString);

}
