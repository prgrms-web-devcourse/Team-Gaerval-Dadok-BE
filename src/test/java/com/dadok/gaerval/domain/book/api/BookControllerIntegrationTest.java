package com.dadok.gaerval.domain.book.api;

import static org.hamcrest.MatcherAssert.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.dadok.gaerval.domain.book.dto.request.BookSearchRequest;
import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponses;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class BookControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BookService bookService;

	@Test
	@DisplayName("findBooksByQuery - 검색어 기반으로 도서 API 검색 결과를 반환한다.")
	@WithMockCustomOAuth2LoginUser
	void testFindBooksByQuery() throws Exception {
		String keyword = "용기";
		BookSearchRequest bookSearchRequest = new BookSearchRequest(1, 10, keyword);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		params.add("page", bookSearchRequest.page().toString());
		params.add("pageSize", bookSearchRequest.pageSize().toString());
		params.add("query", bookSearchRequest.query());

		MvcResult mvcResult = mockMvc.perform(get("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.params(params)
				.characterEncoding(StandardCharsets.UTF_8))
			.andExpect(status().isOk())
			.andReturn();

		String json = mvcResult.getResponse().getContentAsString();
		BookResponses response = objectMapper.readValue(json, BookResponses.class);
		assertThat(response.searchBookResponseList(), hasSize(greaterThanOrEqualTo(0)));
		if (!response.searchBookResponseList().isEmpty()) {
			assertThat(response.searchBookResponseList().get(0).title(), is(notNullValue()));
		}
	}

	@Test
	@WithMockCustomOAuth2LoginUser
	void testFindRecentQuery() throws Exception {
		int limit = 10;

		MvcResult mvcResult = mockMvc.perform(get("/api/books/recent-searches")
				.contentType(MediaType.APPLICATION_JSON)
				.param("limit", String.valueOf(limit))
			)
			.andExpect(status().isOk())
			.andReturn();

		String json = mvcResult.getResponse().getContentAsString();

		BookRecentSearchResponses response = objectMapper.readValue(json, BookRecentSearchResponses.class);
		assertThat(response.bookRecentSearchResponses(), hasSize(greaterThanOrEqualTo(0)));
	}
}
