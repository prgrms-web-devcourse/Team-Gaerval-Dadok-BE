package com.dadok.gaerval.domain.bookshelf.api;

import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.dadok.gaerval.controller.ControllerTest;
import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.PopularBookshelvesOfJobResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.SummaryBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

import lombok.SneakyThrows;

@WebMvcTest(controllers = BookshelfController.class)
class BookshelfControllerSliceTest extends ControllerTest {

	@MockBean
	private BookshelfService bookshelfService;

	@DisplayName("findPopularBookshelvesByJobGroup - 직군별 인기있는 책장 리스트 조회 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findBookshelvesByJobGroup() throws Exception {
		// Given
		String jobGroup = JobGroup.DEVELOPMENT.getDescription();
		PopularBookshelvesOfJobResponses responses = new PopularBookshelvesOfJobResponses(
			jobGroup, List.of(new SummaryBookshelfResponse(23L, "영지님의 책장",
			List.of(new SummaryBookshelfResponse.SummaryBookResponse(1L, "해리포터",
				"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
		))
		);

		given(bookshelfService.findPopularBookshelvesByJob(any(), eq(jobGroup)))
			.willReturn(responses);

		// When // Then
		mockMvc.perform(get("/api/suggestions/bookshelves")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.param("job_group", jobGroup)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.jobGroup").value(jobGroup))
			.andExpect(jsonPath("$.bookshelfResponses[*].bookshelfName").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].bookshelfId").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].books[*].bookId").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].books[*].title").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].books[*].imageUrl").exists())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				requestParameters(
					parameterWithName("job_group").description("직군")
				),
				responseFields(
					fieldWithPath("jobGroup").type(JsonFieldType.STRING).description("직군"),
					fieldWithPath("bookshelfResponses[].bookshelfName").type(JsonFieldType.STRING).description("책장 이름"),
					fieldWithPath("bookshelfResponses[].bookshelfId").type(JsonFieldType.NUMBER).description("책장 ID"),
					fieldWithPath("bookshelfResponses[].books[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("bookshelfResponses[].books[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("bookshelfResponses[].books[].imageUrl").type(JsonFieldType.STRING)
						.description("책 이미지 url")
				)
			));
	}

	@DisplayName("insertBookInBookshelf - 책장에 책 추가 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void insertBookInBookshelf() throws Exception {
		// Given
		var title = "미움받을 용기";
		var author = "기시미 이치로, 고가 후미타케";
		var isbn = "9788996991342";
		var contents = "인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다고 말한 철학자가 있다.";
		var url = "https://search.daum.net/search?w=bookpage&bookId=1467038&q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0";
		var imageUrl = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038";
		var apiProvider = "kakao";
		var publisher = "인플루엔셜";
		BookCreateRequest request = new BookCreateRequest(title, author, isbn, contents, url,
			imageUrl, publisher, apiProvider);

		given(bookshelfService.insertBookSelfItem(any(), eq(23L), eq(request)))
			.willReturn(23L);

		// When // Then
		mockMvc.perform(post("/api/bookshelves/{bookshelvesId}/books", 23L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(createJson(request))
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookshelvesId").description("첵장 Id")
				),
				requestFields(
					fieldWithPath("title").type(JsonFieldType.STRING).description("도서 제목"),
					fieldWithPath("author").type(JsonFieldType.STRING).description("도서 작가"),
					fieldWithPath("isbn").type(JsonFieldType.STRING).description("도서 isbn"),
					fieldWithPath("contents").type(JsonFieldType.STRING).description("도서 설명"),
					fieldWithPath("url").type(JsonFieldType.STRING).description("도서 url"),
					fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("도서 이미지 url"),
					fieldWithPath("publisher").type(JsonFieldType.STRING).description("출판사"),
					fieldWithPath("apiProvider").type(JsonFieldType.STRING).description("api 제공사")
				)
			));
	}

	@SneakyThrows
	@DisplayName("removeBookFormBookshelf - 책장에 책 제거 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void removeBookFormBookshelf() {
		// Given
		given(bookshelfService.removeBookSelfItem(any(), eq(23L), eq(34L)))
			.willReturn(23L);

		// When // Then
		mockMvc.perform(delete("/api/bookshelves/{bookshelvesId}/books/{bookId}", 23L, 34L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookshelvesId").description("첵장 Id"),
					parameterWithName("bookId").description("책 Id")
				)
			));
	}

	@DisplayName("findSummaryBookshelf - 자신의 책장 요약 조회 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findMySummaryBookshelf() throws Exception {
		// Given
		var responses = new SummaryBookshelfResponse(23L, "영지님의 책장",
			List.of(new SummaryBookshelfResponse.SummaryBookResponse(1L, "해리포터",
				"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
		);

		given(bookshelfService.findSummaryBookshelf(any()))
			.willReturn(responses);

		// When // Then
		mockMvc.perform(get("/api/bookshelves/me")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.bookshelfName").exists())
			.andExpect(jsonPath("$.bookshelfId").exists())
			.andExpect(jsonPath("$.books[*].bookId").exists())
			.andExpect(jsonPath("$.books[*].title").exists())
			.andExpect(jsonPath("$.books[*].imageUrl").exists())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				responseFields(
					fieldWithPath("bookshelfName").type(JsonFieldType.STRING).description("책장 이름"),
					fieldWithPath("bookshelfId").type(JsonFieldType.NUMBER).description("책장 ID"),
					fieldWithPath("books[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("books[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("books[].imageUrl").type(JsonFieldType.STRING)
						.description("책 이미지 url")
				)
			));
	}

	@DisplayName("findSummaryBookshelf - 사용자의 책장 요약 조회 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findSummaryBookshelfByUserId() throws Exception {
		// Given
		var responses = new SummaryBookshelfResponse(23L, "영지님의 책장",
			List.of(new SummaryBookshelfResponse.SummaryBookResponse(1L, "해리포터",
				"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
		);

		given(bookshelfService.findSummaryBookshelf(5L))
			.willReturn(responses);

		// When // Then
		mockMvc.perform(get("/api/users/{userId}/bookshelves", 5L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.bookshelfName").exists())
			.andExpect(jsonPath("$.bookshelfId").exists())
			.andExpect(jsonPath("$.books[*].bookId").exists())
			.andExpect(jsonPath("$.books[*].title").exists())
			.andExpect(jsonPath("$.books[*].imageUrl").exists())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("userId").description("유저 Id")
				),
				responseFields(
					fieldWithPath("bookshelfName").type(JsonFieldType.STRING).description("책장 이름"),
					fieldWithPath("bookshelfId").type(JsonFieldType.NUMBER).description("책장 ID"),
					fieldWithPath("books[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("books[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("books[].imageUrl").type(JsonFieldType.STRING)
						.description("책 이미지 url")
				)
			));
	}
}
