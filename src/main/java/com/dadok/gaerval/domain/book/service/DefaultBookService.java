package com.dadok.gaerval.domain.book.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.SortingPolicy;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SearchBookResponse;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.exception.InvalidBookDataException;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultBookService implements BookService {

	private final WebClient webClient;
	private final ObjectMapper objectMapper;
	private final BookRepository bookRepository;
	private final BookMapper bookMapper;

	@Override
	@Transactional
	public BookResponses findAllByKeyword(String keyword) {
		// TODO 페이징 처리

		if (!StringUtils.hasText(keyword)) {
			return new BookResponses(Collections.emptyList());
		}

		System.out.println("search keyword : " + keyword);

		Flux<String> resultFlux = searchBooks(keyword, 1, 10, SortingPolicy.ACCURACY.getName());

		List<SearchBookResponse> searchBookResponseList = new ArrayList<>();
		try {
			JsonNode jsonNode = objectMapper.readTree(resultFlux.toStream().findFirst().orElse(""));
			log.info(jsonNode.toPrettyString());

			Optional<JsonNode> documents = Optional.of(jsonNode.get("documents"));

			documents.ifPresent(docs -> docs.forEach(document -> {
				String title = document.get("title").asText();
				List<String> allAuthors = new ArrayList<>();
				document.get("authors").forEach(authorNode -> allAuthors.add(authorNode.asText()));
				String isbn = document.get("isbn").asText().split(" ")[1];
				String contents = document.get("contents").asText();
				String url = document.get("url").asText();
				String imageUrl = document.get("thumbnail").asText();
				String apiProvider = AuthProvider.KAKAO.getName();
				String publisher = document.get("publisher").asText();

				String processingContents;

				if (!StringUtils.hasText(contents) || contents.length() == 0) {
					processingContents = "콘텐츠가 없습니다.";
				} else {
					if (contents.length() >= 1999)
						processingContents = contents.substring(0, 1998);
					else
						processingContents = contents;
				}

				Book book = Book.create(title, String.join(",", allAuthors), isbn,
					processingContents, url, imageUrl, apiProvider,
					publisher);

				searchBookResponseList.add(bookMapper.entityToSearchBookResponse(book));
				// TODO 저장정책
				// bookRepository.save(book);
			}));

		} catch (JsonProcessingException e) {
			throw new InvalidBookDataException(ErrorCode.BOOK_DATA_INVALID);
		}
		return new BookResponses(searchBookResponseList);
	}

	@Override
	@Transactional
	public Book createBook(BookCreateRequest bookCreateRequest) {
		Optional<Book> existsBook = this.findByIsbn(bookCreateRequest.isbn());
		if (existsBook.isPresent()) {
			return existsBook.map(book -> {
				book.changeUrl(bookCreateRequest.url());
				book.changeContents(bookCreateRequest.contents());
				book.changeAuthor(bookCreateRequest.author());
				book.changeTitle(bookCreateRequest.title());
				book.changeImageUrl(bookCreateRequest.imageUrl());
				book.changeApiProvider(bookCreateRequest.apiProvider());

				return bookRepository.save(book);
			}).orElseThrow(() -> new IllegalArgumentException("도서 데이터를 찾는데 실패했습니다."));
		} else {
			Book newBook = Book.create(
				bookCreateRequest.title(),
				bookCreateRequest.author(),
				bookCreateRequest.isbn(),
				bookCreateRequest.contents(),
				bookCreateRequest.url(),
				bookCreateRequest.imageUrl(),
				bookCreateRequest.apiProvider(),
				bookCreateRequest.publisher()
			);
			return bookRepository.save(newBook);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Book getById(Long bookId) {
		return bookRepository.findById(bookId).orElseThrow(()-> new ResourceNotfoundException(Book.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Book> findById(Long bookId) {
		return bookRepository.findById(bookId);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Book> findByIsbn(String isbn) {
		return bookRepository.findBookByIsbn(isbn);
	}

	@Override
	@Transactional(readOnly = true)
	public BookResponse findDetailById(Long bookId) {
		return bookMapper.entityToBookResponse(this.getById(bookId));
	}

	private Flux<String> searchBooks(String query, int page, int size, String sort) {
		return webClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("query", query)
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("sort", sort)
				.build())
			.retrieve()
			.bodyToFlux(String.class);
	}
}
