package com.dadok.gaerval.global.config.externalapi;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.request.SearchTarget;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SearchBookResponse;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.exception.BookApiNotAvailableException;
import com.dadok.gaerval.domain.book.exception.InvalidBookDataException;
import com.dadok.gaerval.domain.book.service.BookDataProcessor;
import com.dadok.gaerval.global.error.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientBookApiOperations implements ExternalBookApiOperations {

	private final WebClient webClient;
	private final ObjectMapper objectMapper;
	private final BookMapper bookMapper;

	@Override
	public BookResponses searchBooks(String query, int page, int size, String sort) {

		return processResult(webClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("query", query)
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("sort", sort)
				.build())
			.acceptCharset(StandardCharsets.UTF_8)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToFlux(String.class)
			.toStream().findFirst().orElse(""))
			;
	}

	@Override
	public BookResponses searchBooksWithTargetRestriction(String query, SearchTarget searchTarget, int page, int size,
		String sort) {
		return processResult(webClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("query", query)
				.queryParam("target", searchTarget.getName())
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("sort", sort)
				.build())
			.acceptCharset(StandardCharsets.UTF_8)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToFlux(String.class)
			.toStream().findFirst().orElse(""));
	}

	private BookResponses processResult(String result) {
		List<SearchBookResponse> searchBookResponseList = new ArrayList<>();
		AtomicReference<Boolean> isEnd = new AtomicReference<>(Boolean.TRUE);
		AtomicReference<Integer> pageableCount = new AtomicReference<>(0);
		AtomicReference<Integer> totalCount = new AtomicReference<>(0);

		try {
			JsonNode jsonNode = objectMapper.readTree(result);
			log.info("[WebClientBookApiOperations]-[processResult] received data : {}", jsonNode.toPrettyString());

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
}
