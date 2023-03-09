package com.dadok.gaerval.domain.book.dto.response;

import java.util.List;

public record UserByBookResponses(
	Long bookId,
	Integer totalCount,
	Boolean isInMyBookshelf,
	List<UserByBookResponse> users
) {
}
