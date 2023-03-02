package com.dadok.gaerval.domain.user.repository;

import static com.dadok.gaerval.domain.job.entity.QJob.*;
import static com.dadok.gaerval.domain.user.entity.QUser.*;

import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSupportImpl implements UserSupport {

	private final JPAQueryFactory query;

	@Override
	public UserDetailResponse findUserDetail(Long userId) {
		return query.select(Projections.constructor(UserDetailResponse.class,
					user.id, user.name, user.nickname.nickname, user.oauthNickname, user.email, user.profileImage, user.gender, user.authProvider,
					user.job.jobGroup, user.job.jobName, user.job.sortOrder
				)
			)
			.from(user)
			.leftJoin(user.job, job)
			.where(user.id.eq(userId))
			.fetchOne();
	}

	@Override
	public UserProfileResponse findUserProfile(Long userId) {
		return query.select(Projections.constructor(UserProfileResponse.class,
					user.id, user.nickname.nickname, user.profileImage, user.gender,
					user.job.jobGroup, user.job.jobName, user.job.sortOrder
				)
			)
			.from(user)
			.leftJoin(user.job, job)
			.where(user.id.eq(userId))
			.fetchOne();
	}

}
