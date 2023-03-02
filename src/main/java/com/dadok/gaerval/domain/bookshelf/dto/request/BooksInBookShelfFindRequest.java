package com.dadok.gaerval.domain.bookshelf.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItemType;
import com.dadok.gaerval.global.util.SortDirection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class BooksInBookShelfFindRequest {

	private BookshelfItemType type;

	@Min(value = 0, message = "pageSize는 최소 0 입니다.")
	@Max(value = 100, message = "pageSize는 최대 100 입니다.")
	private Integer pageSize = 10;

	private Long bookCursorId;

	private SortDirection sortDirection = SortDirection.DESC;

}
