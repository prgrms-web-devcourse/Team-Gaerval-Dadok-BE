package com.dadok.gaerval.global.util;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.util.StringUtils;

import com.dadok.gaerval.global.error.exception.InvalidArgumentException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonValidator {

	private static final UrlValidator urlValidator = new UrlValidator();

	private static final EmailValidator emailValidator = EmailValidator.getInstance();

	private static final String NULL_MESSAGE = "%s는 null일 수 없습니다. ";

	public static void validateNotnull(Object obj, String valueName) {
		if (Objects.isNull(obj)) {
			throw new InvalidArgumentException(String.format(NULL_MESSAGE, valueName));
		}
	}

	public static void validateBlank(String input, String valueName) {
		if (!StringUtils.hasText(input)) {
			throw new InvalidArgumentException(String.format(NULL_MESSAGE, valueName));
		}
	}

	public static void validateLengthGraterThen(String value, int length, String valueName) {
		validateNotnull(value, valueName);
		if (value.length() < length) {
			throw new InvalidArgumentException(
				String.format("%s의 길이는 %s 이상여야 합니다. ", valueName, length));
		}
	}

	public static void validateLengthLessThen(String value, int length, String valueName) {
		validateNotnull(value, valueName);
		if (value.length() > length) {
			throw new InvalidArgumentException(
				String.format("%s의 길이는 %s 이하여야 합니다. ", valueName, length));
		}
	}

	public static void validateLengthLessThenWithNullable(String value, int length, String valueName) {
		if (value == null) {
			return;
		}

		if (value.length() > length) {
			throw new InvalidArgumentException(
				String.format("%s의 길이는 %s 이하여야 합니다. ", valueName, length));
		}
	}

	public static void validateLengthInRange(String value, int minLength, int maxLength, String valueName) {
		validateNotnull(value, valueName);

		if (value.length() >= maxLength || value.length() < minLength) {
			log.info("에러 발생. valueName : {}, value : {}, length : {}",
				valueName, value, value.length());
			log.debug("에러 발생. valueName : {}, value : {}, length : {}",
				valueName, value, value.length());
			System.out.println("CommonValidator.validateLengthInRange \n"
			+ String.format("에러 발생. valueName : %s, value : %s, length : %s",
				valueName, value, value.length()));

			throw new InvalidArgumentException(
				String.format("%s의 길이는 %s 이상, %s 이하여야 합니다. ", valueName, minLength, maxLength));
		}
	}

	public static void validateUrl(String url, String valueName) {
		validateNotnull(url, valueName);

		if (!urlValidator.isValid(valueName)) {
			throw new InvalidArgumentException(
				String.format("url 형식이 유효하지 않습니다. input : %s", valueName));
		}
	}

	public static void validateUrlWithLength(String url, int maxLength, String valueName) {
		validateNotnull(url, valueName);
		validateUrl(url, valueName);
		validateLengthLessThen(url, maxLength, valueName);
	}

	public static void validate(String value, Predicate<String> expression, String valueName) {
		if (!expression.test(value)) {
			throw new InvalidArgumentException(
				String.format("%s 가 유효하지 않습니다. ", valueName));
		}
	}

	public static void validateEmail(String email) {
		validateNotnull(email, "email");
		if (!emailValidator.isValid(email)) {
			throw new InvalidArgumentException("이메일 형식이 유효하지 않습니다.");
		}
	}

	public static void validateIsFuture(LocalDate localDate, String valueName) {
		validateNotnull(localDate, valueName);
		LocalDate currentDate = LocalDate.now();

		if (localDate.isEqual(currentDate)) {
			return;
		}

		if ( !localDate.isAfter(currentDate)) {
			throw new InvalidArgumentException(
				String.format("%s는 현재시간보다 이전 입니다.", currentDate));
		}
	}

	public static void validateIsBefore(LocalDate localDate, String valueName) {
		validateNotnull(localDate, valueName);
		LocalDate currentDate = LocalDate.now();

		if (localDate.isEqual(currentDate)) {
			return;
		}

		if (!localDate.isBefore(currentDate)) {
			throw new InvalidArgumentException(
				String.format("%s는 현재시간보다 이후 입니다.", currentDate));
		}
	}

	public static void validatePeriod(LocalDate startDate, LocalDate endDate) {
		validateNotnull(startDate, "startDate");
		validateNotnull(endDate, "endDate");
		LocalDate currentDate = LocalDate.now();

		if (startDate.isEqual(currentDate)) {
			return;
		}

		if (!startDate.isAfter(LocalDate.now())) {
			throw new InvalidArgumentException(
				String.format("시작 날짜는 현재시간 이후 부터 가능합니다. startDate : %s , endDate : %s.", startDate, endDate));
		}

		if (!endDate.isAfter(startDate)) {
			throw new InvalidArgumentException(
				String.format(" 종료 날짜는 시작 날짜 이후 부터 가능합니다. startDate : %s , endDate : %s.", startDate, endDate));

		}

	}


}
