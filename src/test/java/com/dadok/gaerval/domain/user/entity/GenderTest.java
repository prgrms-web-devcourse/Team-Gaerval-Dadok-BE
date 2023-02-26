package com.dadok.gaerval.domain.user.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.dadok.gaerval.global.error.exception.InvalidArgumentException;

class GenderTest {

	@DisplayName("create - 대문자로 GENDER 생성에 성공한다.")
	@ParameterizedTest(name = "#{index} - Run test with args={0}")
	@CsvSource(value = {"MALE:남자", "FEMALE:여자", "NONE:미설정"}, delimiterString = ":")
	void create_withUppercase_success(String genderStr, String description) {
		//when
		Gender gender = Gender.of(genderStr);

		//then
		assertEquals(gender.getName(), genderStr);
		assertEquals(gender.getDescription(), description);
	}

	@DisplayName("create - 소문자로 GENDER 생성에 성공한다.")
	@ParameterizedTest(name = "#{index} - Run test with args={0}")
	@CsvSource(value = {"male:남자", "female:여자", "NONE:미설정"}, delimiterString = ":")
	void create_withLowercase_success(String genderStr, String description) {
		//when
		Gender gender = Gender.of(genderStr);

		//then
		assertEquals(gender.getName(), genderStr.toUpperCase());
		assertEquals(gender.getDescription(), description);
	}

	@DisplayName("create - m이나 f로 GENDER 생성에 성공한다.")
	@ParameterizedTest(name = "#{index} - Run test with args={0}")
	@CsvSource(value = {"m:남자:MALE", "f:여자:FEMALE"}, delimiterString = ":")
	void create_withShort_success(String shortStr, String description, String name) {
		//when
		Gender gender = Gender.of(shortStr);

		//then
		assertEquals(gender.getName(), name);
		assertEquals(gender.getDescription(), description);
	}

	@DisplayName("create - 잘못된 값을 입력하면 GENDER 생성에 실패한다.")
	@ParameterizedTest(name = "#{index} - Run test with args={0}")
	@MethodSource("shortValues")
	void create_withShort_fail(String wrongValue) {
		//when
		assertThrows(InvalidArgumentException.class,
			() -> Gender.of(wrongValue));
	}

	static Stream<String> shortValues() {
		return Stream.of("null", "mail", "fimail", "ff", "B", " ", "e", "");
	}
}