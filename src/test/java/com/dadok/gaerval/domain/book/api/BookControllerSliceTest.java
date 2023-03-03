package com.dadok.gaerval.domain.book.api;

import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.dadok.gaerval.controller.ControllerTest;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.testutil.BookObjectProvider;

@WebMvcTest(controllers = BookController.class)
class BookControllerSliceTest extends ControllerTest {

	@MockBean
	private BookService bookService;

	@DisplayName("findBooksByQuery - 검색어 기반으로 도서 API 검색 결과를 반환한다.")
	@Test
	void findBook_success() throws Exception {
		// given
		String keyword = "용기";
		given(bookService.findAllByKeyword(keyword)).willReturn(BookObjectProvider.mockBookData());

		// when
		mockMvc.perform(get("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.param("query", keyword)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				requestParameters(
					parameterWithName("query").description("검색어")
				),
				responseFields(
					fieldWithPath("searchBookResponseList").type(JsonFieldType.ARRAY)
						.optional()
						.description("도서 검색 결과 리스트"),
					fieldWithPath("searchBookResponseList[].title").type(JsonFieldType.STRING)
						.optional()
						.description("책 제목"),
					fieldWithPath("searchBookResponseList[].author").type(JsonFieldType.STRING)
						.optional()
						.description("작가 목록(쉼표로 구분)"),
					fieldWithPath("searchBookResponseList[].isbn").type(JsonFieldType.STRING)
						.optional()
						.description("isbn(13자리, 이전 버전인 10자리는 별도 관리 하지 않음)"),
					fieldWithPath("searchBookResponseList[].contents").type(JsonFieldType.STRING)
						.optional()
						.description("책 소개(소개글이 중간에 잘려서 오므로 말 줄임표 등의 처리 필요)"),
					fieldWithPath("searchBookResponseList[].url").type(JsonFieldType.STRING)
						.optional()
						.description("책 소개 url"),
					fieldWithPath("searchBookResponseList[].imageUrl").type(JsonFieldType.STRING)
						.optional()
						.description("썸네일"),
					fieldWithPath("searchBookResponseList[].apiProvider").type(JsonFieldType.STRING)
						.optional()
						.description("도서 API 제공자"),
					fieldWithPath("searchBookResponseList[].publisher").type(JsonFieldType.STRING)
						.optional()
						.description("출판사")
				)
			));

		// then
		verify(bookService).findAllByKeyword(keyword);
	}

	@DisplayName("findBookDetail - bookId로 도서 상세정보 조회에 성공한다.")
	@Test
	void findBookDetail_success() throws Exception {
		// given
		Long bookId = 123L;
		BookResponse expectedBookResponse = BookObjectProvider.createBookResponse();
		given(bookService.findDetailById(bookId)).willReturn(expectedBookResponse);

		// when
		mockMvc.perform(get("/api/books/{bookId}", bookId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookId").description("도서 ID")
				),
				responseFields(
					fieldWithPath("title").type(JsonFieldType.STRING)
						.optional()
						.description("책 제목"),
					fieldWithPath("author").type(JsonFieldType.STRING)
						.optional()
						.description("작가 목록(쉼표로 구분)"),
					fieldWithPath("isbn").type(JsonFieldType.STRING)
						.description("isbn(13자리, 이전 버전인 10자리는 별도 관리 하지 않음)"),
					fieldWithPath("contents").type(JsonFieldType.STRING)
						.optional()
						.description("책 소개(소개글이 중간에 잘려서 오므로 말 줄임표 등의 처리 필요)"),
					fieldWithPath("url").type(JsonFieldType.STRING)
						.optional()
						.description("책 소개 url"),
					fieldWithPath("imageUrl").type(JsonFieldType.STRING)
						.optional()
						.description("썸네일"),
					fieldWithPath("apiProvider").type(JsonFieldType.STRING)
						.optional()
						.description("도서 API 제공자"),
					fieldWithPath("publisher").type(JsonFieldType.STRING)
						.optional()
						.description("출판사"),
					fieldWithPath("imageKey").type(JsonFieldType.STRING)
						.optional()
						.description("이미지키"),
					fieldWithPath("id").type(JsonFieldType.NUMBER)
						.optional()
						.description("도서 id")
				)
			));

		// then
		verify(bookService).findDetailById(bookId);
	}
}