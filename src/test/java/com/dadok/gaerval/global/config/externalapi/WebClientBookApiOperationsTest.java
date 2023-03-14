package com.dadok.gaerval.global.config.externalapi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.request.SortingPolicy;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SearchBookResponse;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class WebClientBookApiOperationsTest {

	@Mock
	private WebClient webClient;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private BookMapper bookMapper;

	@Mock
	private WebClientBookApiOperations webClientBookApiOperations;

	@DisplayName("정상데이터를 처리하는데 성공한다.")
	@Test
	void searchBooks_process_success() throws JsonProcessingException {
		// given
		String keyword = "test";
		String result = """
			{
			    "meta": {
			        "is_end": true,
			        "total_count": 1,
			        "pageable_count": 1
			    },
			    "documents": [
			        {
			            "authors": [
			                "%s"
			            ],
			            "contents": "%s",
			            "isbn": "%s",
			            "publisher": "%s",
			            "thumbnail": "%s",
			            "title": "%s",
			            "url": "%s"
			        }
			    ]
			}
			""".formatted(BookObjectProvider.author, BookObjectProvider.contents, BookObjectProvider.isbn,
			BookObjectProvider.publisher, BookObjectProvider.imageUrl, BookObjectProvider.title,
			BookObjectProvider.url);

		JsonNode jsonNode = objectMapper.readTree(result);
		Optional<JsonNode> documents = Optional.ofNullable(jsonNode.get("documents"));
		Optional<JsonNode> meta = Optional.ofNullable(jsonNode.get("meta"));

		SearchBookResponse searchBookResponse = new SearchBookResponse(
			BookObjectProvider.title,
			BookObjectProvider.author,
			BookObjectProvider.isbn,
			BookObjectProvider.contents,
			BookObjectProvider.url,
			BookObjectProvider.imageUrl,
			BookObjectProvider.apiProvider,
			BookObjectProvider.publisher
		);

		List<SearchBookResponse> expectedResponses = Collections.singletonList(searchBookResponse);
		BookResponses bookResponses =  new BookResponses(true, 1, 1, expectedResponses);

		given(webClientBookApiOperations.searchBooks(keyword, 1, 10, SortingPolicy.ACCURACY.getName()))
			.willReturn(bookResponses);


	}

	@DisplayName("searchBooks - 정상 데이터를 정상처리하는데 성공한다.")
	@Test
	void testSearchBooks() throws JsonProcessingException {
		// given
		String query = "test";
		int page = 1;
		int size = 10;
		String sort = SortingPolicy.ACCURACY.getName();

		String result = """
			{
			    "meta": {
			        "is_end": true,
			        "total_count": 1,
			        "pageable_count": 1
			    },
			    "documents": [
			        {
			            "authors": [
			                "%s"
			            ],
			            "contents": "%s",
			            "isbn": "%s",
			            "publisher": "%s",
			            "thumbnail": "%s",
			            "title": "%s",
			            "url": "%s"
			        }
			    ]
			}
			""".formatted(BookObjectProvider.author, BookObjectProvider.contents, BookObjectProvider.isbn,
			BookObjectProvider.publisher, BookObjectProvider.imageUrl, BookObjectProvider.title,
			BookObjectProvider.url);

		given(webClient.get()
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
			.toStream()
			.findFirst())
			.willReturn(Optional.of(result));

		SearchBookResponse expectedResponse = new SearchBookResponse(
			BookObjectProvider.title,
			BookObjectProvider.author,
			BookObjectProvider.isbn,
			BookObjectProvider.contents,
			BookObjectProvider.url,
			BookObjectProvider.imageUrl,
			BookObjectProvider.apiProvider,
			BookObjectProvider.publisher
		);

		BookResponses expectedResponses = new BookResponses(true, 1, 1, List.of(expectedResponse));

		// when
		BookResponses actualResponses = webClientBookApiOperations.searchBooks(query, page, size, sort);

		// then
		assertEquals(actualResponses, expectedResponses);
	}

	@DisplayName("일일 호출 횟수 초과 데이터 처리에 성공한다.")
	@Test
	void searchBooks_processCallExceed_success() {
	}


	@DisplayName("빈 데이터 처리에 성공한다.")
	@Test
	void searchBooks_processEmptyData_success() {
	}
}
