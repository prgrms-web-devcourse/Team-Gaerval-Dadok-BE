package com.dadok.gaerval.domain.bookshelf.service;

import java.util.Optional;

import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.dto.request.BookshelfItemCreateRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookInShelfResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfSummaryResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.DetailBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.SuggestionBookshelvesByJobGroupResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.SuggestionBookshelvesResponses;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.user.entity.User;

public interface BookshelfService {

	Bookshelf getById(Long bookshelfId);

	Optional<Bookshelf> findById(Long bookshelfId);

	DetailBookshelfResponse findDetailById(Long bookshelfId);

	Long createBookshelf(User user);

	SuggestionBookshelvesByJobGroupResponses findSuggestionBookshelvesByJobGroup(Long userId, String jobGroup);

	Long insertBookSelfItem(Long userId, Long bookshelfId, BookshelfItemCreateRequest bookshelfItemCreateRequest);

	Long removeBookSelfItem(Long userId, Long bookshelfId, Long bookId);

	BookShelfSummaryResponse findSummaryBookshelf(Long userId);

	BookInShelfResponses findAllBooksInShelf(Long bookshelvesId, BooksInBookShelfFindRequest request);

	BookShelfDetailResponse findBookShelfWithJob(Long userId);

	void updateJobIdByUserId(Long userId, Long jobId);

	SuggestionBookshelvesResponses findSuggestionBookshelves();

	BookShelfDetailResponse findBookShelfById(Long bookshelfId);

}
