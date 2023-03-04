package com.dadok.gaerval.domain.book.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.dadok.gaerval.domain.job.entity.JobGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SuggestionsBookFindResponses {

	private boolean isFirst; // 첫번째냐

	private boolean isLast;  // 마지막이냐

	private boolean hasNext;

	private int count; // 결과 총 갯수

	private boolean isEmpty; // 반환 값이 0개인가

	private JobGroup jobGroup;

	private List<SuggestionsBookFindResponse> books;

	public SuggestionsBookFindResponses(Slice<SuggestionsBookFindResponse> slice,
		JobGroup jobGroup
	) {
		this.isFirst = slice.isFirst();
		this.isLast = slice.isLast();
		this.hasNext = slice.hasNext();
		this.count = slice.getNumberOfElements();
		this.isEmpty = slice.isEmpty();
		this.books = slice.getContent();
		this.jobGroup = jobGroup;
	}

}
