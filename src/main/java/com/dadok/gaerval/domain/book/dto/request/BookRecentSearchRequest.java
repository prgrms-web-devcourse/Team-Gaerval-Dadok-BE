package com.dadok.gaerval.domain.book.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public record BookRecentSearchRequest(
	@Min(value = 1, message = "Limit must be at least 1")
	@Max(value = 100, message = "Limit cannot exceed 100") Long limit
) {

	public BookRecentSearchRequest(Long limit) {
		this.limit = limit == null ? 10L : limit;
	}
}
