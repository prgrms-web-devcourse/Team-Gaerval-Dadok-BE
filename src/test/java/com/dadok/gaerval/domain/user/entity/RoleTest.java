package com.dadok.gaerval.domain.user.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.dadok.gaerval.global.error.exception.InvalidArgumentException;

class RoleTest {

	@DisplayName("create - Role 생성에 성공한다.")
	@ParameterizedTest(name = "#{index} - Run test with args={0}")
	@CsvSource(value = {
		"USER:ROLE_USER:일반 사용자", "ADMIN:ROLE_ADMIN:운영자",
		"user:ROLE_USER:일반 사용자", "admin:ROLE_ADMIN:운영자",
		"role_user:ROLE_USER:일반 사용자", "role_admin:ROLE_ADMIN:운영자",

	},
		delimiterString = ":")
	void create_success(String roleName, String key, String title) {
		//when
		Role role = Role.of(roleName);

		//then
		assertEquals(role.getKey(), key);
		assertEquals(role.getName(), key);
		assertEquals(role.getDescription(), title);
		assertEquals(role.getAuthority(), key);
		assertEquals(role.getTitle(), title);
	}

	@DisplayName("create - Role 생성에 실패한다.")
	@ParameterizedTest(name = "#{index} - Run test with args={0}")
	@MethodSource("failValues")
	void create_fail(String wrongValue) {
		//when
		assertThrows(InvalidArgumentException.class,
			() -> Role.of(wrongValue));
	}

	static Stream<String> failValues() {
		return Stream.of("null", "mail", "fimail", "ff", "B", " ", "e", "");
	}

}