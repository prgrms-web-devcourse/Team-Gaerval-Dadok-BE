package com.dadok.gaerval.domain.user.dto.response;

public record UserJobRegisterResponse(
	Long userId,
	UserDetailResponse.JobDetailResponse job
) {

}
