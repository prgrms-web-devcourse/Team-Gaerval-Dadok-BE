package com.dadok.gaerval.domain.bookshelf.service;

import java.util.Optional;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.DetailBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.PopularBookshelvesOfJobResponses;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.user.entity.User;

public interface BookshelfService {

	Bookshelf getById(Long bookshelfId);

	Optional<Bookshelf> findById(Long bookshelfId);

	DetailBookshelfResponse findDetailById(Long bookshelfId);

	Long createBookshelf(User user);

	PopularBookshelvesOfJobResponses findPopularBookshelvesByJob(User user, String jobGroup);

	Long insertBookSelfItem(User user, Long bookshelfId, BookCreateRequest bookCreateRequest);

	Long removeBookSelfItem(User user, Long bookshelfId, Long bookId);
}
