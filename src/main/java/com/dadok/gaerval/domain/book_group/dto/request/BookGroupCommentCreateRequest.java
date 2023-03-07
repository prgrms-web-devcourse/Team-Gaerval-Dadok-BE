package com.dadok.gaerval.domain.book_group.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record BookGroupCommentCreateRequest(
	Long parentCommentId,

	@NotBlank(message = "comment는 빈 값일 수 없습니다.")
	@Size(max = 2000)
	String comment) {
}
