package com.dadok.gaerval.domain.book_group.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record BookGroupCommentUpdateRequest(
	@NotNull(message = "수정시에는 commendId를 반드시 입력해야합니다.")
	Long commentId,

	@NotBlank(message = "comment는 빈 값일 수 없습니다.")
	@Size(max = 2000)
	String comment)  {
}
