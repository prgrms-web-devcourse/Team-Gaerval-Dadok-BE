package com.dadok.gaerval.domain.book_group.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

public record BookGroupCommentResponses(
	boolean isFirst, // 첫번째냐
	boolean isLast,  // 마지막이냐
	boolean hasNext,
	int count, // 결과 총 갯수
	boolean isEmpty, // 반환 값이 0개인가

	BookGroupResponse bookGroup,
	List<BookGroupCommentResponse> bookGroupComments) {

	public BookGroupCommentResponses(Boolean isPublic, Boolean isGroupMember, Slice<BookGroupCommentResponse> slice) {
		this(slice.isFirst(),
			slice.isLast(),
			slice.hasNext(),
			slice.getNumberOfElements(),
			slice.isEmpty(),
			new BookGroupResponse(isPublic, isGroupMember),
			slice.getContent());
	}

	public record BookGroupResponse(Boolean isPublic, Boolean isGroupMember) {
	}
}
