package com.dadok.gaerval.domain.user.dto.response;

import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.entity.Gender;

public record UserProfileResponse(
	Long userId,
	String nickname,
	String profileImage,
	Gender gender,
	UserDetailResponse.JobDetailResponse job
) {
	public UserProfileResponse(Long userId, String nickname,
		String profileImage, Gender gender, JobGroup jobGroup, JobGroup.JobName jobName, int order) {
		this(userId, nickname, profileImage, gender,
			new UserDetailResponse.JobDetailResponse(jobGroup, jobName, order));
	}
}
