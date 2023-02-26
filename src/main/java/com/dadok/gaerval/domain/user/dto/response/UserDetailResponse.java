package com.dadok.gaerval.domain.user.dto.response;

public record UserDetailResponse(
	Long userid,
	String name,
	String nickname,
	String email,
	String profileImage


	) {
}
