package com.dadok.gaerval.global.config.externalapi;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.request.BestSellerSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.SearchTarget;
import com.dadok.gaerval.domain.book.dto.response.BestSellerBookResponse;
import com.dadok.gaerval.domain.book.dto.response.BestSellerBookResponses;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.OriginalBookData;
import com.dadok.gaerval.domain.book.dto.response.SearchBookResponse;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.exception.BookApiNotAvailableException;
import com.dadok.gaerval.domain.book.exception.InvalidBookDataException;
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

	private final KakaoWebClientConfig kakaoWebClientConfig;
	private final AladinWebClientConfig aladinWebClientConfig;

	private final ObjectMapper objectMapper;
	private final BookMapper bookMapper;
	private static final int MAXIMUM_ALADIN_COUNT = 200;

	@Override
	public BookResponses searchBooks(String query, int page, int size, String sort) {

		return processBookSearchResult(kakaoWebClientConfig.getWebClient().get()
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
				.toStream().findFirst().orElse(""),
			page, size)
			;
	}

	@Override
	public BookResponses searchBooksWithTargetRestriction(String query, SearchTarget searchTarget, int page, int size,
		String sort) {
		return processBookSearchResult(kakaoWebClientConfig.getWebClient().get()
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
				.toStream().findFirst().orElse(""),
			page, size);
	}

	private BookResponses processBookSearchResult(String result, int page, int size) {
		List<SearchBookResponse> searchBookResponseList = new ArrayList<>();
		AtomicReference<Boolean> isEnd = new AtomicReference<>(Boolean.TRUE);
		AtomicReference<Integer> pageableCount = new AtomicReference<>(0);
		AtomicReference<Integer> totalCount = new AtomicReference<>(0);

		try {
			JsonNode jsonNode = objectMapper.readTree(result);
			log.info("[WebClientBookApiOperations]-[processBookSearchResult] received data : {}",
				jsonNode.toPrettyString());

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

				Book processedBook = bookMapper.originalBookDataToEntity(new OriginalBookData(document.get("title").asText(),
					allAuthors,
					document.get("contents").asText(),
					document.get("isbn").asText(),
					document.get("url").asText(),
					document.get("thumbnail").asText(),
					document.get("publisher").asText(),
					BookApiProvider.KAKAO)
				);

				searchBookResponseList.add(bookMapper.entityToSearchBookResponse(processedBook));
			}));

		} catch (JsonProcessingException e) {
			throw new InvalidBookDataException(ErrorCode.BOOK_DATA_INVALID);
		}

		return new BookResponses(page, size, isEnd.get(), pageableCount.get(), totalCount.get(),
			searchBookResponseList);
	}

	@Override
	public BestSellerBookResponses searchWeeklyBestSellers(BestSellerSearchRequest bestSellerSearchRequest) {
		return processBestSellerSearchResult(aladinWebClientConfig.getWebClient().get()
				.uri(uriBuilder -> uriBuilder
					.path("ItemList.aspx")
					.queryParam("start", bestSellerSearchRequest.page())
					.queryParam("MaxResults", bestSellerSearchRequest.pageSize())
					.queryParam("CategoryId", bestSellerSearchRequest.categoryId())
					.queryParam("QueryType", "Bestseller")
					.queryParams(aladinWebClientConfig.defaultQueryParams())
					.build())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(String.class)
				.toStream().findFirst().orElse(""),
			bestSellerSearchRequest.page(), bestSellerSearchRequest.pageSize());
	}

	private BestSellerBookResponses processBestSellerSearchResult(String result, int page, int size) {
		List<BestSellerBookResponse> bestSellerBookResponseList = new ArrayList<>();
		AtomicReference<Boolean> isEnd = new AtomicReference<>(Boolean.TRUE);
		AtomicReference<Integer> pageableCount = new AtomicReference<>(0);
		AtomicReference<Integer> totalCount = new AtomicReference<>(0);

		try {
			JsonNode jsonNode = objectMapper.readTree(result);
			log.info("[WebClientBookApiOperations]-[processBestSellerSearchResult] received data : {}",
				jsonNode.toPrettyString());

			Optional<JsonNode> documents = Optional.ofNullable(jsonNode.get("item"));
			int totalResults = jsonNode.get("totalResults").asInt();
			int startIndex = jsonNode.get("startIndex").asInt();
			int itemsPerPage = jsonNode.get("itemsPerPage").asInt();

			isEnd.set(totalResults <= startIndex + itemsPerPage);
			pageableCount.set(MAXIMUM_ALADIN_COUNT);
			totalCount.set(totalResults);

			documents.ifPresent(docs -> docs.forEach(document -> {
				Book processedBook = bookMapper.originalBookDataToEntity(new OriginalBookData(document.get("title").asText(),
					List.of(document.get("author").asText()),
					document.get("description").asText(),
					document.get("isbn").asText(),
					document.get("link").asText(),
					document.get("cover").asText(),
					document.get("publisher").asText(),
					BookApiProvider.ALADIN)
				);

				bestSellerBookResponseList.add(new BestSellerBookResponse(processedBook.getTitle(),
					processedBook.getAuthor(),
					processedBook.getIsbn(),
					processedBook.getContents(),
					processedBook.getUrl(),
					processedBook.getImageUrl(),
					processedBook.getApiProvider(),
					processedBook.getPublisher(),
					document.get("bestRank").asInt()
				));
			}));

		} catch (JsonProcessingException e) {
			throw new InvalidBookDataException(ErrorCode.BOOK_DATA_INVALID);
		}

		return new BestSellerBookResponses(page, size, isEnd.get(), pageableCount.get(), totalCount.get(),
			bestSellerBookResponseList);
	}
}
