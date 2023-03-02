package com.dadok.gaerval.domain.user.vo;

import java.util.Objects;

import javax.persistence.Embeddable;

import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.util.RegexHelper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nickname {

	private String nickname;

	public Nickname(String nickname) {
		RegexHelper.validateNickname(nickname);
		this.nickname = nickname;
	}

	public String nickname() {
		return nickname;
	}

	@JacocoExcludeGenerated
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Nickname nickname1 = (Nickname)o;
		return Objects.equals(nickname, nickname1.nickname);
	}

	@JacocoExcludeGenerated
	@Override
	public int hashCode() {
		return Objects.hash(nickname);
	}

}
