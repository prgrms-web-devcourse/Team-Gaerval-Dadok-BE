package com.dadok.gaerval.domain.book_group.dto.response;

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

	private Integer maxMemberCount;

	private Long memberCount;

	private Long commentCount;

	private Long bookId;

	private String bookImageUrl;

	private Long ownerId;

	private String ownerProfileUrl;

	private String ownerNickname;

}
