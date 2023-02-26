package com.dadok.gaerval.domain.bookshelf.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.bookshelf.dto.request.PopularBookshelfOfJobRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.DetailBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.PopularBookshelfOfJobResponses;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfRepository;
import com.dadok.gaerval.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultBookshelfServiceI implements BookshelfService {

	private final BookshelfRepository bookshelfRepository;

	private final BookshelfItemRepository bookshelfItemRepository;

	@Override
	@Transactional(readOnly = true)
	public Bookshelf getById(Long bookId) {
		return bookshelfRepository.findById(bookId).orElseThrow();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Bookshelf> findById(Long bookId) {
		return bookshelfRepository.findById(bookId);
	}

	@Override
	@Transactional(readOnly = true)
	public DetailBookshelfResponse findDetailById(Long id) {
		return null;
	}

	@Override
	public Long createBookshelf(User user) {
		String name = user.getNickname() + "님의 책장";

		Bookshelf bookshelf = Bookshelf.create(name, user);
		return bookshelfRepository.save(bookshelf).getId();
	}

	@Override
	public PopularBookshelfOfJobResponses findAllByJob(PopularBookshelfOfJobRequest request) {
		return null;
	}

	@Override
	public Long insertBookSelfItem(User user, Long bookshelvesId, BookCreateRequest bookCreateRequest) {
		return null;
	}

	@Override
	public Long removeBookSelfItem(User user, Long bookshelvesId, Long bookId) {
		return null;
	}
}
