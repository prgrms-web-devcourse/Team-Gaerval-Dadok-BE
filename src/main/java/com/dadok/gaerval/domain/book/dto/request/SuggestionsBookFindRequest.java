package com.dadok.gaerval.domain.book.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.global.util.SortDirection;

public record SuggestionsBookFindRequest(
	@NotNull
	JobGroup jobGroup,

	@Min(value = 0, message = "pageSize는 최소 0 입니다. default 10")
	@Max(value = 30, message = "pageSize는 최대 30 입니다.")
	Integer pageSize,

	Long bookCursorId,

	SortDirection sortDirection
) {

	public SuggestionsBookFindRequest(JobGroup jobGroup, Integer pageSize, Long bookCursorId,
		SortDirection sortDirection) {
		this.jobGroup = jobGroup;
		this.pageSize = pageSize == null ? 10 : pageSize;
		this.bookCursorId = bookCursorId;
		this.sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
	}

}
