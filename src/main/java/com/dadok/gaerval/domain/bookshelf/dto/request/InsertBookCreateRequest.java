package com.dadok.gaerval.domain.bookshelf.dto.request;

import javax.validation.constraints.NotNull;

public record InsertBookCreateRequest(
	@NotNull(message = "bookId 는 null일 수 없습니다.")
	Long bookId
) {
}
