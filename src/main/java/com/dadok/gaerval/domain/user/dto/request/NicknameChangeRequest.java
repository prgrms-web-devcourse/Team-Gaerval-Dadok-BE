package com.dadok.gaerval.domain.user.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.dadok.gaerval.global.util.RegexHelper;

import lombok.Getter;

public class NicknameChangeRequest {

	@Pattern(regexp = RegexHelper.NICKNAME_REGEX, message = "특수문자와 공백을 제외한 한글, 숫자, 영어 2~10글자만 허용합니다.")
	@NotBlank
	@Getter
	private String nickname;

}
