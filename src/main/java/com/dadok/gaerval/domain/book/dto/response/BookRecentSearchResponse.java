package com.dadok.gaerval.domain.book.dto.response;

import java.time.LocalDateTime;

public record BookRecentSearchResponse(
	String keyword,
	LocalDateTime createdAt
) {
}
