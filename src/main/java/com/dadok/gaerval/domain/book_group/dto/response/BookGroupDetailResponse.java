package com.dadok.gaerval.domain.book_group.dto.response;

import static com.dadok.gaerval.domain.book_group.entity.BookGroup.*;

import java.time.LocalDate;
import java.util.Objects;

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
	String joinQuestion,
	Boolean isPublic,

	Integer maxMemberCount,
	Long currentMemberCount,
	Long commentCount,

	OwnerResponse owner,
	BookResponse book,
	Boolean isOwner,
	Boolean isGroupMember
) {
	public BookGroupDetailResponse(Long bookGroupId, String title, String introduce,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
		LocalDate startDate,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
		LocalDate endDate, Boolean hasJoinPasswd, String joinQuestion, Boolean isPublic, Integer maxMemberCount,
		Long currentMemberCount, Long commentCount, OwnerResponse owner, BookResponse book, Boolean isOwner,
		Boolean isGroupMember) {
		this.bookGroupId = bookGroupId;
		this.title = title;
		this.introduce = introduce;
		this.startDate = startDate;
		this.endDate = endDate;
		this.hasJoinPasswd = hasJoinPasswd;
		this.joinQuestion = joinQuestion;
		this.isPublic = isPublic;
		this.currentMemberCount = currentMemberCount;
		this.commentCount = commentCount;
		this.owner = owner;
		this.book = book;
		this.isOwner = isOwner;
		this.isGroupMember = isGroupMember;
		this.maxMemberCount = Objects.equals(maxMemberCount, NO_LIMIT_MEMBER_COUNT) ? null : maxMemberCount;
	}

	public record BookResponse(
		Long id,
		String title,
		String imageUrl
	) {
	}

	public record OwnerResponse(
		Long id
	) {
	}

}
