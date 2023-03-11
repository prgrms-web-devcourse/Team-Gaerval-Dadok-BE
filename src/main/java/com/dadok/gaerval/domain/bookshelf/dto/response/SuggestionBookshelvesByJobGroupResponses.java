package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

import com.dadok.gaerval.domain.job.entity.JobGroup;

public record SuggestionBookshelvesByJobGroupResponses(
	JobGroup jobGroup,
	List<BookShelfSummaryResponse> bookshelfResponses) {
}

