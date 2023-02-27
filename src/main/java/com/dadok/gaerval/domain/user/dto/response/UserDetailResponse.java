package com.dadok.gaerval.domain.user.dto.response;

import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.entity.Gender;
import com.dadok.gaerval.global.config.security.AuthProvider;

import lombok.Getter;

public record UserDetailResponse(
	Long userId,
	String name,
	String nickname,
	String email,
	String profileImage,
	Gender gender,
	AuthProvider authProvider,
	JobDetailResponse job
) {

	public UserDetailResponse(Long userId, String name, String nickname, String email, String profileImage,
		Gender gender,
		AuthProvider authProvider, JobGroup jobGroup, JobGroup.JobName jobName, int order) {
		this(userId, name, nickname, email, profileImage, gender, authProvider,
			new JobDetailResponse(jobGroup, jobName, order));
	}

	@Getter
	public static class JobDetailResponse {

		private String jobGroupKoreanName;
		private JobGroup jobGroupName;
		private String jobNameKoreanName;
		private JobGroup.JobName jobName;
		private int order;

		public JobDetailResponse(JobGroup jobGroup, JobGroup.JobName jobName, int order) {
			if (jobGroup != null && jobName != null) {
				this.jobGroupKoreanName = jobGroup.getGroupName();
				this.jobGroupName = jobGroup;
				this.jobNameKoreanName = jobName.getJobName();
				this.jobName = jobName;
				this.order = order;
			}
		}
	}

}