package com.dadok.gaerval.domain.book_group.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

public record BookGroupResponses(
	boolean isFirst, // 첫번째냐

	boolean isLast,  // 마지막이냐

	boolean hasNext,

	int count, // 결과 총 갯수

	boolean isEmpty, // 반환 값이 0개인가

	List<BookGroupResponse> bookGroups
) {

	public BookGroupResponses(Slice<BookGroupResponse> slice) {
		this(slice.isFirst(), slice.isLast(), slice.hasNext(), slice.getNumberOfElements(), slice.isEmpty(),
			slice.getContent());
	}

}
