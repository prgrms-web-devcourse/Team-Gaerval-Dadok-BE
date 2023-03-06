package com.dadok.gaerval.domain.job.dto.response;

import java.util.List;

public record JobResponses(
	List<JobGroupResponse> jobGroups
) {
}
