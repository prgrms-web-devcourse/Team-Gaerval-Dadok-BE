package com.dadok.gaerval.domain.book.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SearchBookResponse;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.exception.InvalidBookDataException;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.global.config.webclient.SortingPolicy;
import com.dadok.gaerval.global.error.ErrorCode;
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

				Book book = Book.create(title, String.join(",", allAuthors), isbn, contents, url, imageUrl, apiProvider,
					publisher);
				searchBookResponseList.add(bookMapper.entityToSearchBookResponse(book));
				bookRepository.save(book);
			}));

		} catch (JsonProcessingException e) {
			throw new InvalidBookDataException(ErrorCode.BOOK_DATA_INVALID);
		}
		return new BookResponses(searchBookResponseList);
	}

	@Override
	public Book createBook(BookCreateRequest bookCreateRequest) {
		return null;
	}

	@Override
	public Book getById(Long bookId) {
		return null;
	}

	@Override
	public Optional<Book> findById(Long bookId) {
		return Optional.empty();
	}

	@Override
	public Optional<Book> findByIsbn(String isbn) {
		return Optional.empty();
	}

	private Flux<String> searchBooks(String query, int page, int size, String sort) {
		return webClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("query", query)
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("sort", sort)
				.build())
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToFlux(String.class);
	}
}
