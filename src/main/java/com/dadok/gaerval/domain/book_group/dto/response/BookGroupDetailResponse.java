package com.dadok.gaerval.domain.book_group.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record BookGroupDetailResponse(
	Long bookGroupId,
	String title,
	String introduce,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate startDate,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate endDate,
	Boolean hasJoinPasswd,
	Boolean isPublic,

	Integer maxMemberCount,
	Long currentMemberCount,
	Long commentCount,

	OwnerResponse owner,
	BookResponse book,
	Boolean isOwner,
	Boolean isGroupMember
) {

	public record BookResponse(
		Long id,
		String title,
		String imageUrl
	){}

	public record OwnerResponse(
		Long id
	) {}

}
