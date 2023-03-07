package com.dadok.gaerval.domain.book_group.dto.response;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class BookGroupCommentResponse{
	private Long commentId;
	private String contents;
	private Long bookGroupId;
	private Long parentCommentId;
	private Long userId;
	private String userProfileImage;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
}
