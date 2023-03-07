package com.dadok.gaerval.domain.book_group.api;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.dadok.gaerval.controller.ControllerSliceTest;
import com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponses;
import com.dadok.gaerval.domain.book_group.service.BookGroupCommentService;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.testutil.BookGroupCommentObjectProvider;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

@WebMvcTest(controllers = BookGroupCommentController.class)
@WithMockCustomOAuth2LoginUser
class BookGroupCommentControllerSliceTest extends ControllerSliceTest {

	@MockBean
	private BookGroupCommentService bookGroupCommentService;

	@DisplayName("댓글 저장에 성공한다.")
	@Test
	void createBookGroupComment_ShouldReturnCreated() throws Exception {
		// given
		Long groupId = 234L;
		Long bookGroupCommentId = 234L;
		BookGroupCommentCreateRequest request = new BookGroupCommentCreateRequest(123L,
			"이 책 좋아요");

		given(bookGroupCommentService.createBookGroupComment(groupId, 1L, request))
			.willReturn(bookGroupCommentId);

		// when then
		mockMvc.perform(post("/api/book-groups/{groupId}/comment", groupId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(createJson(request))
			)
			.andExpect(status().isCreated())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("groupId").description("모임 ID")
				),
				requestFields(
					fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).optional().description("부모댓글 id (없으면 null) null이면 부모댓글"),
					fieldWithPath("comment").type(JsonFieldType.STRING).description("댓글 내용")
						.attributes(
							constrainsAttribute(BookGroupCommentCreateRequest.class, "comment")
						)
				)
			))
		;
	}

	@DisplayName("groupId를 통해 댓글을 조회하는데 성공한다.")
	@Test
	void findBookGroupsComment_ShouldReturnOk() throws Exception {

		// given
		Long groupId = 234L;
		Long userId = 234L;
		BookGroupCommentSearchRequest request = new BookGroupCommentSearchRequest(10, 100L, SortDirection.DESC);
		List<BookGroupCommentResponse> bookGroupCommentResponseList = BookGroupCommentObjectProvider.mockCommentResponses;

		BookGroupCommentResponses bookGroupCommentResponses = new BookGroupCommentResponses(
			QueryDslUtil.toSlice(bookGroupCommentResponseList, PageRequest.of(0, 10)));

		given(bookGroupCommentService.findAllBookGroupCommentsByGroup(eq(request), any(), any()))
			.willReturn(bookGroupCommentResponses);

		// when then
		mockMvc.perform(get("/api/book-groups/{groupId}/comments", groupId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.param("groupCommentCursorId", "100")
				.param("pageSize", request.pageSize().toString())
				.param("sortDirection", request.sortDirection().toString())
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("groupId").description("모임 ID")
				),
				requestParameters(
					parameterWithName("groupCommentCursorId").description("커서 id가 없을 경우 정렬 순서에 따라 결정").optional(),
					parameterWithName("pageSize").description("요청 데이터 개수 default : 10")
						.optional()
						.attributes(
							constrainsAttribute(BookGroupCommentSearchRequest.class, "pageSize")
						),
					parameterWithName("sortDirection").description("정렬 순서. default : DESC")
						.optional()
						.description("정렬 방식 : " +
							generateLinkCode(DocumentLinkGenerator.DocUrl.SORT_DIRECTION)
						)
				),
				responseFields(
					fieldWithPath("count").description("댓글 갯수").type(JsonFieldType.NUMBER),
					fieldWithPath("isEmpty").description("데이터가 없으면 empty = true").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isFirst").description("첫 번째 페이지 여부").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isLast").description("마지막 페이지 여부").type(JsonFieldType.BOOLEAN),
					fieldWithPath("hasNext").description("다음 데이터 존재 여부").type(JsonFieldType.BOOLEAN),
					fieldWithPath("bookGroupComments[]").type(JsonFieldType.ARRAY).description("댓글 목록"),
					fieldWithPath("bookGroupComments[].commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
					fieldWithPath("bookGroupComments[].contents").type(JsonFieldType.STRING).description("댓글 내용"),
					fieldWithPath("bookGroupComments[].bookGroupId").type(JsonFieldType.NUMBER).description("모임 ID"),
					fieldWithPath("bookGroupComments[].parentCommentId").type(JsonFieldType.NUMBER).description("부모 댓 ID"),
					fieldWithPath("bookGroupComments[].userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
					fieldWithPath("bookGroupComments[].userProfileImage").type(JsonFieldType.STRING).description("사용자 프로필 이미지"),
					fieldWithPath("bookGroupComments[].createdAt").type(JsonFieldType.STRING).description("생성일"),
					fieldWithPath("bookGroupComments[].modifiedAt").type(JsonFieldType.STRING).description("수정일")
				)));

		verify(bookGroupCommentService).findAllBookGroupCommentsByGroup(eq(request), any(), any());
	}

}