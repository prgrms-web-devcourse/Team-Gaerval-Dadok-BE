package com.dadok.gaerval.domain.book_group.dto.response;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookGroupResponse {

	private Long bookGroupId;

	private String title;

	private String introduce;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate startDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate endDate;

	private Integer maxMemberCount;

	private Boolean hasJoinPasswd;

	private Boolean isPublic;

	private Long memberCount;

	private Long commentCount;

	private BookResponse book;

	private OwnerResponse owner;

	public BookGroupResponse(Long bookGroupId, String title, String introduce, LocalDate startDate, LocalDate endDate,
		Integer maxMemberCount, Boolean hasJoinPasswd, Boolean isPublic, Long memberCount, Long commentCount,
		BookResponse book, OwnerResponse owner) {
		this.bookGroupId = bookGroupId;
		this.title = title;
		this.introduce = introduce;
		this.startDate = startDate;
		this.endDate = endDate;
		this.hasJoinPasswd = hasJoinPasswd;
		this.isPublic = isPublic;
		this.memberCount = memberCount;
		this.commentCount = commentCount;
		this.book = book;
		this.owner = owner;
		this.maxMemberCount = Objects.equals(maxMemberCount, 1000) ? null : maxMemberCount;
	}

	public record BookResponse(
		Long id,
		String imageUrl
	) {
	}

	public record OwnerResponse(
		Long id,
		String profileUrl,
		String nickname
	) {
	}
}
