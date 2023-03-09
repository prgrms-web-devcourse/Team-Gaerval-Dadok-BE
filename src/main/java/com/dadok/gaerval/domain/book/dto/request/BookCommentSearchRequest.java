package com.dadok.gaerval.domain.book.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.dadok.gaerval.global.util.SortDirection;

public record BookCommentSearchRequest(
	@Min(value = 0, message = "pageSize는 최소 0 입니다. default 10")
	@Max(value = 30, message = "pageSize는 최대 30 입니다.")
	Integer pageSize,

	Long bookCommentCursorId,

	SortDirection sortDirection
) {

	public BookCommentSearchRequest(Integer pageSize, Long bookCommentCursorId, SortDirection sortDirection) {
		this.pageSize = (pageSize == null) ? 10 : pageSize;
		this.bookCommentCursorId = bookCommentCursorId;
		this.sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
	}
}
