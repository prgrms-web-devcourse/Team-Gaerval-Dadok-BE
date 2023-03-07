package com.dadok.gaerval.domain.book_group.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.dadok.gaerval.global.util.SortDirection;

public record BookGroupCommentSearchRequest(@Min(value = 0, message = "pageSize는 최소 0 입니다.")
											@Max(value = 100, message = "pageSize는 최대 100 입니다.")
											Integer pageSize,
											Long groupCommentCursorId,

											SortDirection sortDirection) {

	public BookGroupCommentSearchRequest(Integer pageSize, Long groupCommentCursorId, SortDirection sortDirection) {
		this.pageSize = (pageSize == null) ? 10 : pageSize;
		this.groupCommentCursorId = groupCommentCursorId;
		this.sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
	}
}
