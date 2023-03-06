package com.dadok.gaerval.domain.bookshelf.repository;

import static com.dadok.gaerval.domain.book.entity.QBook.*;
import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelf.*;
import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelfItem.*;
import static com.dadok.gaerval.domain.job.entity.QJob.*;
import static com.dadok.gaerval.domain.user.entity.QUser.*;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.group.GroupBy.*;
import static com.querydsl.core.types.Projections.*;

import java.util.Optional;

import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfSummaryResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookshelfSupportImpl implements BookshelfSupport {

	private final JPAQueryFactory query;

	@Override
	public Optional<BookShelfDetailResponse> findByIdWithUserAndJob(Long userId) {

		BookShelfDetailResponse bookShelfDetailResponse = query.select(constructor(BookShelfDetailResponse.class,
					bookshelf.id.as("bookshelfId"),
					bookshelf.name.as("bookshelfName"),
					bookshelf.isPublic.as("isPublic"),
					user.id.as("userId"),
					user.name.as("username"),
					user.nickname.nickname.as("userNickname"),
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

	@Override
	public Optional<BookShelfSummaryResponse> findSummaryById(Long userId) {
		var transform = query.from(bookshelf)
			.leftJoin(bookshelf.bookshelfItems, bookshelfItem)
			.leftJoin(bookshelfItem.book, book)
			.where(bookshelf.user.id.eq(userId))
			.orderBy(bookshelfItem.id.desc())
			.transform(
				groupBy(bookshelf.id).list(constructor(BookShelfSummaryResponse.class,
					bookshelf.id,
					bookshelf.name,
					list(
						constructor(BookShelfSummaryResponse.BookSummaryResponse.class,
							book.id, book.title, book.imageUrl)
					))));

		if (transform.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(transform.get(0));
	}
}
