package com.dadok.gaerval.domain.book.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record BookCommentUpdateRequest(

	@NotNull(message = "commentId는 빈 값일 수 없습니다.")
	Long commentId,

	@NotBlank(message = "comment는 빈 값일 수 없습니다.")
	@Size(max = 500, message = "최대 길이는 500입니다.")
	String comment
) {
}
