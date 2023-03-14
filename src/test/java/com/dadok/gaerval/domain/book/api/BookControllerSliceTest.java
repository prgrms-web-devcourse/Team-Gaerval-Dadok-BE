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

	@DisplayName("findBooksByQuery - 검색어 기반으로 도서 API 검색 결과를 반환한다.")
	@Test
	void findBook_success() throws Exception {
		// given
		String keyword = "용기";
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
					parameterWithName("page").description("page (기본값 :1) ")
						.attributes(
							constrainsAttribute(BookSearchRequest.class, "page")
						)
					,
					parameterWithName("pageSize").description("pageSize (기본값:10)")
						.attributes(
							constrainsAttribute(BookSearchRequest.class, "pageSize")
						),
					parameterWithName("query").description("검색어")
						.attributes(
							constrainsAttribute(BookSearchRequest.class, "query")
						)
				),
				responseFields(
					fieldWithPath("isLast").type(JsonFieldType.BOOLEAN)
						.description("마지막 데이터 여부"),
					fieldWithPath("pageableCount").type(JsonFieldType.NUMBER)
						.description("조회 가능한 데이터 여부(중복 제외된 결과)"),
					fieldWithPath("totalCount").type(JsonFieldType.NUMBER)
						.description("전체 데이터 개수"),
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
		verify(bookService).findAllByKeyword(bookSearchRequest);
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
					fieldWithPath("bookId").type(JsonFieldType.NUMBER)
						.optional()
						.description("도서 id")
				)
			));

		// then
		verify(bookService).findDetailById(bookId);
	}

	@DisplayName("findUsersByBook - bookId로 도서를 책장에 꽂은 사용자 정보를 조회한다.")
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
					parameterWithName("bookId").description("도서 ID")
				),
				responseFields(
					fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("도서 id"),
					fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("도서를 꽂은 총 사용자 수"),
					fieldWithPath("isInMyBookshelf").type(JsonFieldType.BOOLEAN)
						.description("내가 꽂은 책인지 여부 - 비 회원의 경우 false"),
					fieldWithPath("users").type(JsonFieldType.ARRAY).description("도서를 꽂은 사용자 정보 - 최대 3명"),
					fieldWithPath("users[].userId").type(JsonFieldType.NUMBER).description("사용자 id"),
					fieldWithPath("users[].profileImage").type(JsonFieldType.STRING).description("사용자 profileImage")
				)
			));

		// then
		verify(bookService).findUserByBookId(eq(bookId), any());
	}

	@DisplayName("findSuggestionBooks - JobGroup으로 가장 책장에 많이 꽂힌 책 순으로 가져온다.")
	@Test
	void findSuggestionBooks_success() throws Exception {
		SuggestionsBookFindRequest request = new SuggestionsBookFindRequest(JobGroup.DEVELOPMENT, 10,
			95L, SortDirection.DESC);

		List<SuggestionsBookFindResponse> suggestionsBookFindResponses = List.of(
			new SuggestionsBookFindResponse(1L, "http://imageurl4.com"
				, "jpa", "김영한", "123456789", "영진", "http://이미지링크4.com",
				JobGroup.DEVELOPMENT.getGroupName(), 10L),
			new SuggestionsBookFindResponse(99L, "http://imageurl5.com"
				, "에스큐엘정복", "김도강", "123456789", "영풍", "http://이미지링크5.com",
				JobGroup.DEVELOPMENT.getGroupName(), 8L),
			new SuggestionsBookFindResponse(100L, "http://imageurl7.com"
				, "나는 왜이렇게 귀여울까", "김별", "123456789", "교보", "http://이미지링크2.com",
				JobGroup.JobName.BACKEND_DEVELOPER.getJobName(), 5L),
			new SuggestionsBookFindResponse(10L, "http://imageurl4.com"
				, "세상에서 김별이 제일 귀엽다", "강형욱", "123456789", "오렐리", "http://이미지링크3.com",
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
					parameterWithName("jobGroup").description("직군명." +
							generateLinkCode(DocUrl.JOB_GROUP))
						.attributes(
							constrainsAttribute(SuggestionsBookFindRequest.class, "jobGroup")
						),
					parameterWithName("pageSize").description("요청 데이터 수. default : 10").optional()
						.attributes(
							constrainsAttribute(SuggestionsBookFindRequest.class, "pageSize")
						),
					parameterWithName("bookCursorId").description("커서 book Id. 커서id가 null이고 DESC면 가장 최근 데이터.")
						.optional(),
					parameterWithName("sortDirection").description("정렬 순서. default : DESC").optional()
						.description("정렬 방식 : " +
							generateLinkCode(DocUrl.SORT_DIRECTION)
						)

				),
				responseFields(
					fieldWithPath("count").description("책 갯수").type(JsonFieldType.NUMBER),
					fieldWithPath("isEmpty").description("데이터가 없으면 empty = true").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isFirst").description("첫 번째 페이지 여부. ").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isLast").description("마지막 페이지 여부.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("hasNext").description("다음 데이터 존재 여부.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("jobGroup").type(JsonFieldType.STRING).description("직군 영어명 :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),
					fieldWithPath("jobGroupKoreanName").type(JsonFieldType.STRING).description("직군 한글명"),

					fieldWithPath("books").description("책장속 책들").type(JsonFieldType.ARRAY),
					fieldWithPath("books[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("books[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("books[].isbn").description("책 isbn. 고유번호").type(JsonFieldType.STRING),
					fieldWithPath("books[].author").description("책 작가").type(JsonFieldType.STRING),
					fieldWithPath("books[].imageUrl").type(JsonFieldType.STRING)
						.description("책 이미지 url"),
					fieldWithPath("books[].url").description("책 정보로 이동하는 url").type(JsonFieldType.STRING),
					fieldWithPath("books[].publisher").description("출판사").type(JsonFieldType.STRING),
					fieldWithPath("books[].jobGroup").type(JsonFieldType.STRING).description("책을 꽂은 사람의 직군 한글명 :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),
					fieldWithPath("books[].count").type(JsonFieldType.NUMBER).description("집계된 책 갯수")
				)
			))
		;
		//then
	}

	@DisplayName("saveBookDetail - 도서를 저장하는데 성공한다.")
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
						fieldWithPath("title").type(JsonFieldType.STRING).description("도서 제목")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "title")
							),
						fieldWithPath("author").type(JsonFieldType.STRING).description("도서 작가")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "author")
							),
						fieldWithPath("isbn").type(JsonFieldType.STRING).description("도서 isbn")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "isbn")
							),
						fieldWithPath("contents").type(JsonFieldType.STRING).description("도서 설명")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "contents")
							),
						fieldWithPath("url").type(JsonFieldType.STRING).description("도서 url")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "url")
							),
						fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("도서 이미지 url")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "imageUrl")
							),
						fieldWithPath("publisher").type(JsonFieldType.STRING).description("출판사")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "publisher")
							),
						fieldWithPath("apiProvider").type(JsonFieldType.STRING).description("api 제공사")
							.attributes(
								constrainsAttribute(BookCreateRequest.class, "apiProvider")
							)
					),
					responseFields(
						fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("생성된 도서 id")
					)
				)
			);
	}
}