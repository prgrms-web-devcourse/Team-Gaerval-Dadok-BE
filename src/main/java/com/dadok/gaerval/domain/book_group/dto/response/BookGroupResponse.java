package com.dadok.gaerval.domain.book_group.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
