package com.dadok.gaerval.domain.user.dto.request;

import javax.validation.constraints.NotNull;

import com.dadok.gaerval.domain.job.entity.JobGroup;

public record UserJobChangeRequest(

	@NotNull(message = "jobGroup은 null이면 안됩니다.")
	JobGroup jobGroup,

	@NotNull(message = "jobName은 null이면 안됩니다.")
	JobGroup.JobName jobName
) {
}
