package com.dadok.gaerval.domain.bookshelf.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItemType;
import com.dadok.gaerval.global.util.SortDirection;

public record BooksInBookShelfFindRequest(

	BookshelfItemType type,

	@Min(value = 0, message = "pageSize는 최소 0 입니다.")
	@Max(value = 100, message = "pageSize는 최대 100 입니다.")
	Integer pageSize,

	Long bookCursorId,

	SortDirection sortDirection

) {
	public BooksInBookShelfFindRequest() {
		this(null, 10, null, SortDirection.DESC);
	}

	public BooksInBookShelfFindRequest(
		@Min(value = 0, message = "pageSize는 최소 0 입니다.")
		@Max(value = 100, message = "pageSize는 최대 100 입니다.")
		Integer pageSize,
		Long bookCursorId
	) {
		this(null, pageSize, bookCursorId, SortDirection.DESC);
	}

}
