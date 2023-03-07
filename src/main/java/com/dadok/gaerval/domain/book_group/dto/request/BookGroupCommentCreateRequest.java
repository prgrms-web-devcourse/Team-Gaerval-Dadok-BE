package com.dadok.gaerval.domain.book_group.dto.request;


public record BookGroupCommentCreateRequest(Long parentCommentId,
											String comment) {
}
