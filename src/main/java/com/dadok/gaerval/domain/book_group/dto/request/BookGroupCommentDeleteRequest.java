package com.dadok.gaerval.domain.book_group.dto.request;

import javax.validation.constraints.NotNull;

public record BookGroupCommentDeleteRequest(
	@NotNull(message = "삭제할 코멘트 id는 빈값일 수 없습니다.")
	Long commentId
) {
}
