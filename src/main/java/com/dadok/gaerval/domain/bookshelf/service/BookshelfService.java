package com.dadok.gaerval.domain.bookshelf.service;

import java.util.Optional;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.bookshelf.dto.request.PopularBookshelfOfJobRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.DetailBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.PopularBookshelfOfJobResponses;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.user.entity.User;

public interface BookshelfService {

	Bookshelf getById(Long bookId);

	Optional<Bookshelf> findById(Long bookId);

	DetailBookshelfResponse findDetailById(Long id);

	Long createBookshelf(User user);

	PopularBookshelfOfJobResponses findAllByJob(PopularBookshelfOfJobRequest request);

	Long insertBookSelfItem(User user, Long bookshelvesId, BookCreateRequest bookCreateRequest);

	Long removeBookSelfItem(User user, Long bookshelvesId, Long bookId);
}
