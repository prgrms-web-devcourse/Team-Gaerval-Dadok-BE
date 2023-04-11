package com.dadok.gaerval.domain.book.dto.response;

import java.util.List;

public record BookResponses (
	Integer requestedPageNumber,
	Integer requestedPageSize,
	Boolean isLast,
	Integer pageableCount,
	Integer totalCount,
	List<SearchBookResponse> searchBookResponseList) {
}
