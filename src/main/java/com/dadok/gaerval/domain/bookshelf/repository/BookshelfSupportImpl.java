package com.dadok.gaerval.domain.bookshelf.repository;

import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelf.*;
import static com.dadok.gaerval.domain.job.entity.QJob.*;
import static com.dadok.gaerval.domain.user.entity.QUser.*;
import static com.querydsl.core.types.Projections.*;

import java.util.Optional;

import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookshelfSupportImpl implements BookshelfSupport {

	private final JPAQueryFactory query;

	@Override
	public Optional<BookShelfDetailResponse> findByIdWithUserAndJob(Long userId) {

		BookShelfDetailResponse bookShelfDetailResponse = query.select(constructor(BookShelfDetailResponse.class,
					bookshelf.id.as("bookShelfId"),
					bookshelf.name.as("bookShelfName"),
					bookshelf.isPublic.as("isPublic"), user.id.as("userId"),
					user.name.as("username"), user.nickname.as("userNickname"),
					user.profileImage.as("userProfileImage"),
					job.jobGroup, job.jobName, job.sortOrder

				)
			)
			.from(bookshelf)
			.leftJoin(bookshelf.user, user)
			.leftJoin(user.job, job)
			.where(bookshelf.user.id.eq(userId))
			.fetchOne();
		return Optional.ofNullable(bookShelfDetailResponse);
	}
}
