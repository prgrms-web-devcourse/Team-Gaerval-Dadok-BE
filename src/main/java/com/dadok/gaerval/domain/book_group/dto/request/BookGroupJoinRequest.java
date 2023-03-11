package com.dadok.gaerval.domain.book_group.dto.request;

import javax.validation.constraints.Size;

import org.springframework.lang.Nullable;

public record BookGroupJoinRequest(
	@Nullable
	@Size(max = 10, message = "joinPasswd 은 최대 10자까지 입력가능합니다.")
	String joinPasswd
) {
}
