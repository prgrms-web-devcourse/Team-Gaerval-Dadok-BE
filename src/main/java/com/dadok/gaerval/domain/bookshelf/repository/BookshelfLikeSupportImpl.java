package com.dadok.gaerval.domain.bookshelf.repository;

import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelfLike.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookshelfLikeSupportImpl implements BookshelfLikeSupport {

	private final JPAQueryFactory query;

	@Override
	public boolean existsByBookshelfIdAndUserId(Long bookshelfId, Long userId) {
		Integer fetchOne = query.selectOne().from(bookshelfLike)
			.where(bookshelfLike.bookshelf.id.eq(bookshelfId), bookshelfLike.user.id.eq(userId))
			.fetchFirst();

		return fetchOne != null;
	}
}
