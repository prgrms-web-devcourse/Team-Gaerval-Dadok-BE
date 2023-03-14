package com.dadok.gaerval.domain.book.dto.response;

import java.util.List;

public record BookResponses(
	Boolean isLast,
	Integer pageableCount,
	Integer totalCount,
	List<SearchBookResponse> searchBookResponseList) {
}
