package com.dadok.gaerval.domain.user.dto.response;

public record UserJobChangeResponse(

	Long userId,

	UserDetailResponse.JobDetailResponse job
) {

}
