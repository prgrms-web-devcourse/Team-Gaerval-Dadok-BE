package com.dadok.gaerval.domain.book_group.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record BookGroupDetailResponse(
	Long bookGroupId,
	String title,
	String introduce,
	Long ownerId,
	Boolean isOwner,
	Boolean isGroupMember,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate startDate,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate endDate,

	String bookTitle,
	String bookImageUrl,
	Long bookId,


	Integer maxMemberCount,
	Long currentMemberCount,
	Long commentCount

) {
}
