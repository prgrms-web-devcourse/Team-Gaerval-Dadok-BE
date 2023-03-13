package com.dadok.gaerval.domain.book.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.SortingPolicy;
import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SearchBookResponse;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;
import com.dadok.gaerval.domain.book.dto.response.UserByBookResponses;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.exception.BookApiNotAvailableException;
import com.dadok.gaerval.domain.book.exception.InvalidBookDataException;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.global.config.externalapi.ExternalApiError;
import com.dadok.gaerval.global.config.externalapi.ExternalBookApiOperations;
import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultBookService implements BookService {

	static final int USER_VIEW_LIMIT = 3;
	private final ExternalBookApiOperations externalBookApiOperations;
	private final ObjectMapper objectMapper;
	private final BookRepository bookRepository;
	private final BookshelfItemRepository bookshelfItemRepository;
	private final BookMapper bookMapper;

	@Override
	@Transactional
	public BookResponses findAllByKeyword(BookSearchRequest bookSearchRequest) {

		AtomicReference<Boolean> isEnd = new AtomicReference<>(Boolean.TRUE);
		AtomicReference<Integer> pageableCount = new AtomicReference<>(0);
		AtomicReference<Integer> totalCount = new AtomicReference<>(0);

		if (StringUtils.isBlank(bookSearchRequest.query()) || !StringUtils.isAlphanumericSpace(
			bookSearchRequest.query())) {
			log.info("[DefaultBookService]-[findAllByKeyword] invalid keyword : {}", bookSearchRequest.query());
			return new BookResponses(isEnd.get(), pageableCount.get(), totalCount.get(), Collections.emptyList());
		}

		String result = externalBookApiOperations.searchBooks(bookSearchRequest.query(), bookSearchRequest.page(),
			bookSearchRequest.pageSize(), SortingPolicy.ACCURACY.getName());

		List<SearchBookResponse> searchBookResponseList = new ArrayList<>();

		try {
			JsonNode jsonNode = objectMapper.readTree(result);
			log.info("[DefaultBookService]-[findAllByKeyword] received data : {}", jsonNode.toPrettyString());

			Optional<JsonNode> documents = Optional.ofNullable(jsonNode.get("documents"));
			Optional<JsonNode> meta = Optional.ofNullable(jsonNode.get("meta"));

			meta.ifPresent(metaData -> {
				isEnd.set(metaData.get("is_end").asBoolean());
				pageableCount.set(metaData.get("pageable_count").asInt());
				totalCount.set(metaData.get("total_count").asInt());
			});

			if (meta.isEmpty()) {
				ExternalApiError externalApiError = objectMapper.readValue(result, ExternalApiError.class);
				throw new BookApiNotAvailableException(ErrorCode.fromCode(String.valueOf(externalApiError.getCode())));
			}

			documents.ifPresent(docs -> docs.forEach(document -> {
				List<String> allAuthors = new ArrayList<>();
				document.get("authors").forEach(authorNode -> allAuthors.add(authorNode.asText()));

				Book processedBook = BookDataProcessor.process(document.get("title").asText(),
					allAuthors,
					document.get("contents").asText(),
					document.get("isbn").asText(),
					document.get("url").asText(),
					document.get("thumbnail").asText(),
					document.get("publisher").asText()
				);

				searchBookResponseList.add(bookMapper.entityToSearchBookResponse(processedBook));
			}));

		} catch (JsonProcessingException e) {
			throw new InvalidBookDataException(ErrorCode.BOOK_DATA_INVALID);
		}
		return new BookResponses(isEnd.get(), pageableCount.get(), totalCount.get(), searchBookResponseList);
	}

	private Book createBook(BookCreateRequest bookCreateRequest) {
		Optional<Book> existsBook = this.findByIsbn(bookCreateRequest.isbn());
		if (existsBook.isPresent()) {
			return existsBook.map(book -> {
				book.change(bookCreateRequest);
				return bookRepository.save(book);
			}).orElseThrow(() -> new IllegalArgumentException("도서 데이터를 찾는데 실패했습니다."));
		} else {
			Book newBook = bookMapper.createBookRequestToEntity(bookCreateRequest);
			return bookRepository.save(newBook);
		}
	}

	@Override
	@Transactional
	public Long createBookAndReturnId(BookCreateRequest bookCreateRequest) {
		return this.createBook(bookCreateRequest).getId();
	}

	@Override
	public Book getById(Long bookId) {
		return bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotfoundException(Book.class));
	}

	@Override
	public Optional<Book> findById(Long bookId) {
		return bookRepository.findById(bookId);
	}

	@Override
	public Optional<Book> findByIsbn(String isbn) {
		return bookRepository.findBookByIsbn(isbn);
	}

	@Override
	public BookResponse findDetailById(Long bookId) {
		return bookMapper.entityToBookResponse(this.getById(bookId));
	}

	@Override
	public SuggestionsBookFindResponses findSuggestionBooks(SuggestionsBookFindRequest request) {
		return bookRepository.findSuggestionBooks(request);
	}

	@Override
	public UserByBookResponses findUserByBookId(Long bookId, Long userId) {
		return bookshelfItemRepository.findBookshelfItemUsersByBook(bookId, userId, USER_VIEW_LIMIT);
	}

}
