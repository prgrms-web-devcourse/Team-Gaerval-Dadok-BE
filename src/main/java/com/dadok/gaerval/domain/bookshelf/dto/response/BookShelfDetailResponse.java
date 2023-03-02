package com.dadok.gaerval.domain.bookshelf.dto.response;

import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;

public record BookShelfDetailResponse(
	Long bookshelfId,
	String bookshelfName,
	boolean isPublic,
	Long userId,
	String username,
	String userNickname,
	String userProfileImage,
	UserDetailResponse.JobDetailResponse job
) {

	public BookShelfDetailResponse(Long bookshelfId, String bookshelfName, boolean isPublic, Long userId,
		String username,
		String userNickname, String userProfileImage, JobGroup jobGroup, JobGroup.JobName jobName, int order) {

		this(bookshelfId, bookshelfName, isPublic, userId, username, userNickname, userProfileImage,
			new UserDetailResponse.JobDetailResponse(jobGroup, jobName, order)
		);
	}

}
