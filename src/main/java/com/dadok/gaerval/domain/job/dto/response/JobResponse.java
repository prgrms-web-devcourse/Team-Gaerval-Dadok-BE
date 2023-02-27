package com.dadok.gaerval.domain.job.dto.response;

import java.util.List;

public record JobResponse(
	JobGroupResponse jobGroup,
	List<JobNameResponse> jobNames
) {

	public record JobGroupResponse(
		String koreanName,
		String name
	) {
	}

	public record JobNameResponse(
		String koreanName,
		String name,
		int order
	) {
	}
}
