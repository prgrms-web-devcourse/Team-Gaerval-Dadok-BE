package com.dadok.gaerval.domain.bookshelf.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItemType;
import com.dadok.gaerval.global.util.SortDirection;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class BooksInBookShelfFindRequest {

	private BookshelfItemType type;

	@Min(value = 0, message = "pageSize는 최소 0 입니다.")
	@Max(value = 100, message = "pageSize는 최대 100 입니다.")
	private Integer pageSize = 10;

	private Long bookCursorId;

	private SortDirection sortDirection;

	public BooksInBookShelfFindRequest(BookshelfItemType type, Integer pageSize, Long bookCursorId,
		SortDirection sortDirection) {
		this.type = type;
		this.pageSize = pageSize == null ? 10 : pageSize;
		this.bookCursorId = bookCursorId;
		this.sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
	}
}
