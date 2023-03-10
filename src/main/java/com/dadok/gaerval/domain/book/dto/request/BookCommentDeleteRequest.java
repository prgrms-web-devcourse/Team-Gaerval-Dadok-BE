package com.dadok.gaerval.domain.book.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record BookCommentDeleteRequest (
	@NotNull(message = "commentId는 null 일 수 없습니다.")
	@Min(value = 1, message = "commentId는 1이상입니다.")
	Long commentId
) {
}
