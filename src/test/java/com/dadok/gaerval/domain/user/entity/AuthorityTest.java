package com.dadok.gaerval.domain.user.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.global.error.exception.InvalidArgumentException;

class AuthorityTest {

	@DisplayName("create - Authority 생성에 성공한다.")
	@Test
	void create_success() {
		//given
		Role user = Role.USER;
		//when
		assertDoesNotThrow(() -> {
			Authority.create(user);
		});
	}

	@DisplayName("create - Role이 Null이면 Authority 생성에 실패한다.")
	@Test
	void create_fail() {
		//given
		Role user = null;
		//when
		assertThrows(InvalidArgumentException.class, () ->
			Authority.create(user));
	}

}