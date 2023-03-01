package com.dadok.gaerval.domain.bookshelf.repository;

import static com.dadok.gaerval.domain.book.entity.QBook.*;
import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelf.*;
import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelfItem.*;

import java.util.Optional;

import org.springframework.data.domain.Sort;

import com.dadok.gaerval.domain.book.entity.QBook;
import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.QBookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.QBookshelfItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookShelfSupportImpl implements BookShelfSupport {

	private final JPAQueryFactory query;

	@Override
	public Optional<Bookshelf> findAllWithBooks(Long bookShelfId, BooksInBookShelfFindRequest request) {
		query.from(bookshelfItem)
			.leftJoin(bookshelfItem.bookshelf, bookshelf)
			.leftJoin(bookshelfItem.book, book)
			.where()

		return Optional.empty();
	}

	private BooleanExpression generetedCursorId(Long cursorId, Sort.Direction direction) {
		if (cursorId == null) {
			return null;
		}



	}
}
