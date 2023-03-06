package com.dadok.gaerval.domain.job.dto.response;

import java.util.List;

import com.dadok.gaerval.domain.job.entity.JobGroup;

public record JobGroupResponse(
	String koreanName,
	JobGroup name,

	List<JobNameResponse> jobs
) {

	public record JobNameResponse(
		String koreanName,
		JobGroup.JobName name,
		int order
	) {
	}

}
