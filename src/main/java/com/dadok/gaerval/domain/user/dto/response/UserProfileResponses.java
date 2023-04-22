package com.dadok.gaerval.domain.user.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

public record UserProfileResponses(
	boolean isFirst,
	boolean isLast,
	boolean hasNext,
	int count,
	boolean isEmpty,

	List<UserProfileResponse> users
) {

	public UserProfileResponses(Slice<UserProfileResponse> slice) {
		this(slice.isFirst(),
			slice.isLast(),
			slice.hasNext(),
			slice.getNumberOfElements(),
			slice.isEmpty(),
			slice.getContent());
	}
}
