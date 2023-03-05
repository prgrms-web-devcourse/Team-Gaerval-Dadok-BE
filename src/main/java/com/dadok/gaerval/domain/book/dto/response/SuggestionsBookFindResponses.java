package com.dadok.gaerval.domain.book.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.dadok.gaerval.domain.job.entity.JobGroup;


public record SuggestionsBookFindResponses(

	boolean isFirst, // 첫번째냐

	boolean isLast,  // 마지막이냐

	boolean hasNext,

	int count, // 결과 총 갯수

	boolean isEmpty, // 반환 값이 0개인가

	JobGroup jobGroup,

	List<SuggestionsBookFindResponse> books
) {
	public SuggestionsBookFindResponses(Slice<SuggestionsBookFindResponse> slice, JobGroup jobGroup) {
		this(
			slice.isFirst(),
			slice.isLast(),
			slice.hasNext(),
			slice.getNumberOfElements(),
			slice.isEmpty(),
			jobGroup,
			slice.getContent()
		);
	}

}
