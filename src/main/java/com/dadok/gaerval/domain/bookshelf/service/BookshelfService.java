package com.dadok.gaerval.domain.bookshelf.service;

import java.util.Optional;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.DetailBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.PopularBookshelvesOfJobResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.SummaryBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.user.entity.User;

public interface BookshelfService {

	Bookshelf getById(Long bookshelfId);

	Optional<Bookshelf> findById(Long bookshelfId);

	DetailBookshelfResponse findDetailById(Long bookshelfId);

	Long createBookshelf(User user);

	PopularBookshelvesOfJobResponses findPopularBookshelvesByJob(Long userId, String jobGroup);

	Long insertBookSelfItem(Long userId, Long bookshelfId, BookCreateRequest bookCreateRequest);

	Long removeBookSelfItem(Long userId, Long bookshelfId, Long bookId);

	SummaryBookshelfResponse findSummaryBookshelf(Long userId);
}
