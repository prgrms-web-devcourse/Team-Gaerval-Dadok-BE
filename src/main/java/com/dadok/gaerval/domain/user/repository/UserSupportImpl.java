package com.dadok.gaerval.domain.user.repository;

import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSupportImpl implements UserSupport{

	private final JPAQueryFactory query;

	@Override
	public UserDetailResponse findUserDetail(Long userId) {
		return null;
	}

}
