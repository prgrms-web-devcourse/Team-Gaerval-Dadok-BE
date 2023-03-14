package com.dadok.gaerval.domain.book.dto.request;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;

public record BookSearchRequest(
	@Range(min = 1, max = 50, message = "page는 1에서 50사이 값입니다.")
	Integer page,
	@Range(min = 1, max = 50, message = "pageSize는 1에서 50사이 값입니다.")
	Integer pageSize,
	@NotBlank(message = "검색어는 빈 값일 수 없습니다.")
	String query
) {
	public BookSearchRequest(Integer page, Integer pageSize, String query) {
		this.page = (page == null) ? 1 : page;
		this.pageSize = (pageSize == null) ? 10 : page;
		this.query = query;
	}
}
