package com.dadok.gaerval.domain.bookshelf.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.bookshelf.dto.request.PopularBookshelfOfJobRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.DetailBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.PopularBookshelfOfJobResponses;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultBookshelfService implements BookshelfService {

	private final BookshelfRepository bookshelfRepository;

	private final BookshelfItemRepository bookshelfItemRepository;

	private final BookService bookService;

	@Override
	@Transactional(readOnly = true)
	public Bookshelf getById(Long bookshelfId) {
		return bookshelfRepository.findById(bookshelfId).orElseThrow(ResourceNotfoundException::new);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Bookshelf> findById(Long bookshelfId) {
		return bookshelfRepository.findById(bookshelfId);
	}

	@Override
	@Transactional(readOnly = true)
	public DetailBookshelfResponse findDetailById(Long bookshelfId) {
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
	public Long insertBookSelfItem(User user, Long bookshelfId, BookCreateRequest bookCreateRequest) {
		Bookshelf bookshelf = validationBookshelfAuth(user, bookshelfId);
		Book book = bookService.findByIsbn(bookCreateRequest.isbn())
			.orElseGet(() -> bookService.createBook(bookCreateRequest));
		BookshelfItem bookshelfItem = BookshelfItem.create(bookshelf, book);
		bookshelfItemRepository.save(bookshelfItem);
		return bookshelfId;

	}

	@Override
	public Long removeBookSelfItem(User user, Long bookshelfId, Long bookId) {
		Bookshelf bookshelf = validationBookshelfAuth(user, bookshelfId);
		Book book = bookService.findById(bookId).orElseThrow();
		BookshelfItem bookshelfItem = bookshelfItemRepository.findByBookshelfAndBook(bookshelf, book)
			.orElseThrow(IllegalArgumentException::new);
		bookshelfItemRepository.deleteById(bookshelfItem.getId());
		return bookshelfId;
	}

	private Bookshelf validationBookshelfAuth(User user, Long bookshelfId) {
		Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId).orElseThrow();
		if (!Objects.equals(bookshelf.getUser(), user)) {
			throw new ResourceNotfoundException();
		}
		return bookshelf;
	}
}
