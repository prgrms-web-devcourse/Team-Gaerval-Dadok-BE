package com.dadok.gaerval.domain.book.api;

import static com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator.*;
import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.any;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.dadok.gaerval.controller.ControllerSliceTest;
import com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator;
import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponse;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;
import com.dadok.gaerval.domain.book.dto.response.UserByBookResponse;
import com.dadok.gaerval.domain.book.dto.response.UserByBookResponses;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

@WebMvcTest(controllers = BookController.class)
class BookControllerSliceTest extends ControllerSliceTest {

	@MockBean
	private BookService bookService;

	@DisplayName("findBooksByQuery - ????????? ???????????? ?????? API ?????? ????????? ????????????.")
	@Test
	void findBook_success() throws Exception {
		// given
		String keyword = "??????";
		BookSearchRequest bookSearchRequest = new BookSearchRequest(1, 10, keyword);
		given(bookService.findAllByKeyword(bookSearchRequest)).willReturn(
			BookObjectProvider.mockBookData());

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		params.add("page", bookSearchRequest.page().toString());
		params.add("pageSize", bookSearchRequest.pageSize().toString());
		params.add("query", bookSearchRequest.query());

		// when
		mockMvc.perform(get("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.params(params)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				requestParameters(
					parameterWithName("page").description("page (????????? :1) ")
						.attributes(
							constrainsAttribute(BookSearchRequest.class, "page")
						)
					,
					parameterWithName("pageSize").description("pageSize (?????????:10)")
						.attributes(
							constrainsAttribute(BookSearchRequest.class, "pageSize")
						),
					parameterWithName("query").description("?????????")
						.attributes(
							constrainsAttribute(BookSearchRequest.class, "query")
						)
				),
				responseFields(
					fieldWithPath("isLast").type(JsonFieldType.BOOLEAN)
						.description("????????? ????????? ??????"),
					fieldWithPath("pageableCount").type(JsonFieldType.NUMBER)
						.description("?????? ????????? ????????? ??????(?????? ????????? ??????)"),
					fieldWithPath("totalCount").type(JsonFieldType.NUMBER)
						.description("?????? ????????? ??????"),
					fieldWithPath("searchBookResponseList").type(JsonFieldType.ARRAY)
						.optional()
						.description("?????? ?????? ?????? ?????????"),
					fieldWithPath("searchBookResponseList[].title").type(JsonFieldType.STRING)
						.optional()
						.description("??? ??????"),
					fieldWithPath("searchBookResponseList[].author").type(JsonFieldType.STRING)
						.optional()
						.description("?????? ??????(????????? ??????)"),
					fieldWithPath("searchBookResponseList[].isbn").type(JsonFieldType.STRING)
						.optional()
						.description("isbn(13??????, ?????? ????????? 10????????? ?????? ?????? ?????? ??????)"),
					fieldWithPath("searchBookResponseList[].contents").type(JsonFieldType.STRING)
						.optional()
						.description("??? ??????(???????????? ????????? ????????? ????????? ??? ????????? ?????? ?????? ??????)"),
					fieldWithPath("searchBookResponseList[].url").type(JsonFieldType.STRING)
						.optional()
						.description("??? ?????? url"),
					fieldWithPath("searchBookResponseList[].imageUrl").type(JsonFieldType.STRING)
						.optional()
						.description("?????????"),
					fieldWithPath("searchBookResponseList[].apiProvider").type(JsonFieldType.STRING)
						.optional()
						.description("?????? API ?????????"),
					fieldWithPath("searchBookResponseList[].publisher").type(JsonFieldType.STRING)
						.optional()
						.description("?????????")
				)
			));

		// then
		verify(bookService).findAllByKeyword(bookSearchRequest);
	}

	@DisplayName("findBookDetail - bookId??? ?????? ???????????? ????????? ????????????.")
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
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookId").description("?????? ID")
				),
				responseFields(
					fieldWithPath("title").type(JsonFieldType.STRING)
						.optional()
						.description("??? ??????"),
					fieldWithPath("author").type(JsonFieldType.STRING)
						.optional()
						.description("?????? ??????(????????? ??????)"),
					fieldWithPath("isbn").type(JsonFieldType.STRING)
						.description("isbn(13??????, ?????? ????????? 10????????? ?????? ?????? ?????? ??????)"),
					fieldWithPath("contents").type(JsonFieldType.STRING)
						.optional()
						.description("??? ??????(???????????? ????????? ????????? ????????? ??? ????????? ?????? ?????? ??????)"),
					fieldWithPath("url").type(JsonFieldType.STRING)
						.optional()
						.description("??? ?????? url"),
					fieldWithPath("imageUrl").type(JsonFieldType.STRING)
						.optional()
						.description("?????????"),
					fieldWithPath("apiProvider").type(JsonFieldType.STRING)
						.optional()
						.description("?????? API ?????????"),
					fieldWithPath("publisher").type(JsonFieldType.STRING)
						.optional()
						.description("?????????"),
					fieldWithPath("imageKey").type(JsonFieldType.STRING)
						.optional()
						.description("????????????"),
					fieldWithPath("bookId").type(JsonFieldType.NUMBER)
						.optional()
						.description("?????? id")
				)
			));

		// then
		verify(bookService).findDetailById(bookId);
	}

	@DisplayName("findUsersByBook - bookId??? ????????? ????????? ?????? ????????? ????????? ????????????.")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findUsersByBook() throws Exception {
		// given
		var bookId = 123L;
		var expectedBookResponse = new UserByBookResponses(1L, 5, false,
			List.of(new UserByBookResponse(3L, "imageUrl"), new UserByBookResponse(4L, "imageUrl"),
				new UserByBookResponse(5L, "imageUrl")));

		given(bookService.findUserByBookId(eq(bookId), any()))
			.willReturn(expectedBookResponse);

		// when
		mockMvc.perform(get("/api/books/{bookId}/users", bookId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookId").description("?????? ID")
				),
				responseFields(
					fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("?????? id"),
					fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("????????? ?????? ??? ????????? ???"),
					fieldWithPath("isInMyBookshelf").type(JsonFieldType.BOOLEAN)
						.description("?????? ?????? ????????? ?????? - ??? ????????? ?????? false"),
					fieldWithPath("users").type(JsonFieldType.ARRAY).description("????????? ?????? ????????? ?????? - ?????? 3???"),
					fieldWithPath("users[].userId").type(JsonFieldType.NUMBER).description("????????? id"),
					fieldWithPath("users[].profileImage").type(JsonFieldType.STRING).description("????????? profileImage")
				)
			));

		// then
		verify(bookService).findUserByBookId(eq(bookId), any());
	}

	@DisplayName("findSuggestionBooks - JobGroup?????? ?????? ????????? ?????? ?????? ??? ????????? ????????????.")
	@Test
	void findSuggestionBooks_success() throws Exception {
		SuggestionsBookFindRequest request = new SuggestionsBookFindRequest(JobGroup.DEVELOPMENT, 10,
			95L, SortDirection.DESC);

		List<SuggestionsBookFindResponse> suggestionsBookFindResponses = List.of(
			new SuggestionsBookFindResponse(1L, "http://imageurl4.com"
				, "jpa", "?????????", "123456789", "??????", "http://???????????????4.com",
				JobGroup.DEVELOPMENT.getGroupName(), 10L),
			new SuggestionsBookFindResponse(99L, "http://imageurl5.com"
				, "??????????????????", "?????????", "123456789", "??????", "http://???????????????5.com",
				JobGroup.DEVELOPMENT.getGroupName(), 8L),
			new SuggestionsBookFindResponse(100L, "http://imageurl7.com"
				, "?????? ???????????? ????????????", "??????", "123456789", "??????", "http://???????????????2.com",
				JobGroup.JobName.BACKEND_DEVELOPER.getJobName(), 5L),
			new SuggestionsBookFindResponse(10L, "http://imageurl4.com"
				, "???????????? ????????? ?????? ?????????", "?????????", "123456789", "?????????", "http://???????????????3.com",
				JobGroup.JobName.BACKEND_DEVELOPER.getJobName(), 1L)
		);

		Slice<SuggestionsBookFindResponse> bookFindResponses = QueryDslUtil.toSlice(suggestionsBookFindResponses,
			PageRequest.of(0, 50, Sort.by(
				Sort.Direction.DESC, "book_id"
			)));

		SuggestionsBookFindResponses responses = new SuggestionsBookFindResponses(bookFindResponses,
			JobGroup.DEVELOPMENT);

		given(bookService.findSuggestionBooks(request))
			.willReturn(responses);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		params.add("jobGroup", JobGroup.DEVELOPMENT.name());
		params.add("pageSize", request.pageSize().toString());
		params.add("bookCursorId", String.valueOf(95));
		params.add("sortDirection", SortDirection.DESC.name());

		//when
		mockMvc.perform(get("/api/books/suggestions")
				.contentType(MediaType.APPLICATION_JSON)
				.params(params)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				requestParameters(
					parameterWithName("jobGroup").description("?????????." +
							generateLinkCode(DocUrl.JOB_GROUP))
						.attributes(
							constrainsAttribute(SuggestionsBookFindRequest.class, "jobGroup")
						),
					parameterWithName("pageSize").description("?????? ????????? ???. default : 10").optional()
						.attributes(
							constrainsAttribute(SuggestionsBookFindRequest.class, "pageSize")
						),
					parameterWithName("bookCursorId").description("?????? book Id. ??????id??? null?????? DESC??? ?????? ?????? ?????????.")
						.optional(),
					parameterWithName("sortDirection").description("?????? ??????. default : DESC").optional()
						.description("?????? ?????? : " +
							generateLinkCode(DocUrl.SORT_DIRECTION)
						)

				),
				responseFields(
					fieldWithPath("count").description("??? ??????").type(JsonFieldType.NUMBER),
					fieldWithPath("isEmpty").description("???????????? ????????? empty = true").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isFirst").description("??? ?????? ????????? ??????. ").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isLast").description("????????? ????????? ??????.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("hasNext").description("?????? ????????? ?????? ??????.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("jobGroup").type(JsonFieldType.STRING).description("?????? ????????? :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),
					fieldWithPath("jobGroupKoreanName").type(JsonFieldType.STRING).description("?????? ?????????"),

					fieldWithPath("books").description("????????? ??????").type(JsonFieldType.ARRAY),
					fieldWithPath("books[].bookId").type(JsonFieldType.NUMBER).description("??? ID"),
					fieldWithPath("books[].title").type(JsonFieldType.STRING).description("??? ??????"),
					fieldWithPath("books[].isbn").description("??? isbn. ????????????").type(JsonFieldType.STRING),
					fieldWithPath("books[].author").description("??? ??????").type(JsonFieldType.STRING),
					fieldWithPath("books[].imageUrl").type(JsonFieldType.STRING)
						.description("??? ????????? url"),
					fieldWithPath("books[].url").description("??? ????????? ???????????? url").type(JsonFieldType.STRING),
					fieldWithPath("books[].publisher").description("?????????").type(JsonFieldType.STRING),
					fieldWithPath("books[].jobGroup").type(JsonFieldType.STRING).description("?????? ?????? ????????? ?????? ????????? :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),
					fieldWithPath("books[].count").type(JsonFieldType.NUMBER).description("????????? ??? ??????")
				)
			))
		;
		//then
	}

	@DisplayName("saveBookDetail - ????????? ??????????????? ????????????.")
	@Test
	void saveBookDetail_success() throws Exception {
		BookCreateRequest bookCreateRequest = BookObjectProvider.createBookCreateRequest();

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(objectMapper.writeValueAsString(bookCreateRequest)))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", containsString("/api/books/")))
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					),
					requestFields(
						fieldWithPath("title").type(JsonFieldType.STRING).description("?????? ??????")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "title")
							),
						fieldWithPath("author").type(JsonFieldType.STRING).description("?????? ??????")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "author")
							),
						fieldWithPath("isbn").type(JsonFieldType.STRING).description("?????? isbn")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "isbn")
							),
						fieldWithPath("contents").type(JsonFieldType.STRING).description("?????? ??????")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "contents")
							),
						fieldWithPath("url").type(JsonFieldType.STRING).description("?????? url")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "url")
							),
						fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("?????? ????????? url")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "imageUrl")
							),
						fieldWithPath("publisher").type(JsonFieldType.STRING).description("?????????")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "publisher")
							),
						fieldWithPath("apiProvider").type(JsonFieldType.STRING).description("api ?????????")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "apiProvider")
							)
					),
					responseFields(
						fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("????????? ?????? id")
					)
				)
			);
	}
}