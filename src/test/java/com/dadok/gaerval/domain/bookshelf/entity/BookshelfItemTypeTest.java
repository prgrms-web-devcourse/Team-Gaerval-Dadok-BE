package com.dadok.gaerval.domain.bookshelf.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.dadok.gaerval.global.error.exception.InvalidArgumentException;

class BookshelfItemTypeTest {

	@DisplayName("소문자로 생성 성공 테스트")
	@ParameterizedTest
	@ValueSource(strings = {"wish", "read"})
	void create_success(String small) {
		assertDoesNotThrow(() -> BookshelfItemType.of(small));
	}

	@DisplayName("생성 실패 테스트")
	@ParameterizedTest
	@ValueSource(strings = {"wish2", "raed", "red", "wihs"})
	void create_fail(String failStr) {
		assertThrows(InvalidArgumentException.class, () -> BookshelfItemType.of(failStr));
	}

}