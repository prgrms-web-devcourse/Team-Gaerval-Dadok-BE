package com.dadok.gaerval.global.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dadok.gaerval.global.error.exception.InvalidArgumentException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexHelper {

	public final static String NICKNAME_REGEX = "^[가-힣0-9a-zA-Z]{2,10}$";

	public static void validateNickname(String nickname) {
		String regex = "^[가-힣0-9a-zA-Z]{2,10}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(nickname);

		if (!matcher.matches()) {
			throw new InvalidArgumentException(nickname, "nickname");
		}
	}

}
