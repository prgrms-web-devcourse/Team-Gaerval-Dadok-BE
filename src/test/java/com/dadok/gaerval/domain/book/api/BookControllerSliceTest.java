package com.dadok.gaerval.domain.book.api;

import static com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.dadok.gaerval.controller.ControllerTest;
import com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator;
import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponse;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.global.util.QueryDslUtil;
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

	@DisplayName("findSuggestionBooks - JobGroup으로 가장 책장에 많이 꽂힌 책 순으로 가져온다.")
	@Test
	void findSuggestionBooks_success() throws Exception {
		SuggestionsBookFindRequest request = new SuggestionsBookFindRequest(JobGroup.DEVELOPMENT, 10,
			null, null);

		List<SuggestionsBookFindResponse> suggestionsBookFindResponses = List.of(
			new SuggestionsBookFindResponse(1L, "http://imageurl4.com"
				, "jpa", "김영한", "123456789", "영진", "http://이미지링크4.com",
				JobGroup.DEVELOPMENT.getGroupName(),
				JobGroup.JobName.BACKEND_DEVELOPER.getJobName(), 10L),
			new SuggestionsBookFindResponse(99L, "http://imageurl5.com"
				, "에스큐엘정복", "김도강", "123456789", "영풍", "http://이미지링크5.com",
				JobGroup.DEVELOPMENT.getGroupName(),
				JobGroup.JobName.BACKEND_DEVELOPER.getJobName(), 8L),
			new SuggestionsBookFindResponse(100L, "http://imageurl7.com"
				, "나는 왜이렇게 귀여울까", "김별", "123456789", "교보", "http://이미지링크2.com",
				JobGroup.DEVELOPMENT.getGroupName(),
				JobGroup.JobName.BACKEND_DEVELOPER.getJobName(), 5L),
			new SuggestionsBookFindResponse(10L, "http://imageurl4.com"
				, "세상에서 김별이 제일 귀엽다", "강형욱", "123456789", "오렐리", "http://이미지링크3.com",
				JobGroup.DEVELOPMENT.getGroupName(),
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
		params.add("bookCursorId", null);

		//when
		mockMvc.perform(get("/api/books/suggestions")
				.contentType(MediaType.APPLICATION_JSON)
				.params(params)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
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
					parameterWithName("bookCursorId").description("커서 book Id. 커서id가 없고 DESC면 가장 최근 데이터.").optional(),
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
					fieldWithPath("jobGroup").type(JsonFieldType.STRING).description("직군 한글명 :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

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
					fieldWithPath("books[].jobName").type(JsonFieldType.STRING).description("책을 꽂은 사람의 직업 한글명 :  " +
						DocumentLinkGenerator.generateLinkCode(DocUrl.JOB_NAME)),
					fieldWithPath("books[].count").type(JsonFieldType.NUMBER).description("집계된 책 갯수")
				)
			))
		;
		//then
	}
}