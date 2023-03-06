package com.dadok.gaerval.domain.bookshelf.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookInShelfResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfSummaryResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.DetailBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.PopularBookshelvesOfJobResponses;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfRepository;
import com.dadok.gaerval.domain.job.entity.JobGroup;
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
		return bookshelfRepository.findById(bookshelfId)
			.orElseThrow(() -> new ResourceNotfoundException(Bookshelf.class));
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
		Bookshelf bookshelf = Bookshelf.create(user);
		return bookshelfRepository.save(bookshelf).getId();
	}

	@Override
	@Transactional(readOnly = true)
	public PopularBookshelvesOfJobResponses findPopularBookshelvesByJob(Long userId, String jobGroup) {
		JobGroup searchJobGroup = JobGroup.findJobGroup(jobGroup);
		List<Bookshelf> bookshelves = bookshelfRepository.findAllByJob(searchJobGroup,
			PageRequest.of(0, 5, Sort.by(Sort.Order.desc("bookshelfItems.size"))), userId);
		List<BookShelfSummaryResponse> bookShelfSummaryRespons = bookshelves.stream().map(bookshelf ->
			new BookShelfSummaryResponse(bookshelf.getId(), bookshelf.getName(),
				bookshelf.getBookshelfItems().stream().limit(5).map(bookshelfItem ->
					new BookShelfSummaryResponse.BookSummaryResponse(
						bookshelfItem.getBook().getId(), bookshelfItem.getBook().getTitle(),
						bookshelfItem.getBook().getImageUrl()
					)
				).collect(Collectors.toList()))).collect(Collectors.toList());
		return new PopularBookshelvesOfJobResponses(jobGroup, bookShelfSummaryRespons);
	}

	@Override
	public Long insertBookSelfItem(Long userId, Long bookshelfId, BookCreateRequest bookCreateRequest) {
		Bookshelf bookshelf = validationBookshelfUser(userId, bookshelfId);
		Book book = bookService.findByIsbn(bookCreateRequest.isbn())
			.orElseGet(() -> bookService.createBook(bookCreateRequest));
		BookshelfItem bookshelfItem = BookshelfItem.create(bookshelf, book);
		bookshelfItemRepository.save(bookshelfItem);
		return bookshelfId;
	}

	@Override
	public Long removeBookSelfItem(Long userId, Long bookshelfId, Long bookId) {
		Bookshelf bookshelf = validationBookshelfUser(userId, bookshelfId);
		Book book = bookService.findById(bookId).orElseThrow(() -> new ResourceNotfoundException(Book.class));
		BookshelfItem bookshelfItem = bookshelfItemRepository.findByBookshelfAndBook(bookshelf, book)
			.orElseThrow(() -> new ResourceNotfoundException(BookshelfItem.class));
		bookshelfItemRepository.deleteById(bookshelfItem.getId());
		return bookshelfId;
	}

	@Override
	public BookShelfSummaryResponse findSummaryBookshelf(Long userId) {
		return bookshelfRepository.findSummaryById(userId)
			.orElseThrow(() -> new ResourceNotfoundException(Bookshelf.class));
	}

	@Transactional(readOnly = true)
	@Override
	public BookInShelfResponses findAllBooksInShelf(Long bookshelfId, BooksInBookShelfFindRequest request) {

		Slice<BookshelfItem> bookshelfItems = bookshelfItemRepository.findAllInBookShelf(bookshelfId, request);

		if (bookshelfItems.isEmpty()) {
			return BookInShelfResponses.empty(bookshelfItems);
		}

		List<BookInShelfResponses.BookInShelfResponse> bookInShelfResponses = bookshelfItems.getContent()
			.stream().map(bookshelfItem -> {
				Book book = bookshelfItem.getBook();
				return new BookInShelfResponses.BookInShelfResponse(
					book.getId(),
					book.getTitle(),
					book.getAuthor(),
					book.getIsbn(),
					book.getContents(),
					book.getImageUrl(),
					book.getUrl(),
					book.getPublisher()
				);
			}).toList();

		return new BookInShelfResponses(bookshelfItems, bookInShelfResponses);
	}

	@Transactional(readOnly = true)
	@Override
	public BookShelfDetailResponse findBookShelfWithJob(Long userId) {
		return bookshelfRepository.findByIdWithUserAndJob(userId)
			.orElseThrow(() -> new ResourceNotfoundException(Bookshelf.class));
	}

	private Bookshelf validationBookshelfUser(Long userId, Long bookshelfId) {
		Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId)
			.orElseThrow(() -> new ResourceNotfoundException(Bookshelf.class));
		bookshelf.validateOwner(userId);
		return bookshelf;
	}
}
