package com.dadok.gaerval.domain.book_group.api;

import static com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator.*;
import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;
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
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentUpdateRequest;
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

	@DisplayName("?????? ????????? ????????????.")
	@Test
	void createBookGroupComment_ShouldReturnCreated() throws Exception {
		// given
		Long groupId = 234L;
		Long bookGroupCommentId = 234L;
		BookGroupCommentCreateRequest request = new BookGroupCommentCreateRequest(123L,
			"??? ??? ?????????");

		given(bookGroupCommentService.createBookGroupComment(groupId, 1L, request))
			.willReturn(bookGroupCommentId);

		// when then
		mockMvc.perform(post("/api/book-groups/{groupId}/comments", groupId)
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
					parameterWithName("groupId").description("?????? ID")
				),
				requestFields(
					fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER)
						.optional()
						.description("???????????? id (????????? null) null?????? ????????????"),
					fieldWithPath("comment").type(JsonFieldType.STRING).description("?????? ??????")
						.attributes(
							constrainsAttribute(BookGroupCommentCreateRequest.class, "comment")
						)
				)
			))
		;
	}

	@DisplayName("groupId??? ?????? ????????? ??????????????? ????????????.")
	@Test
	void findBookGroupsComment_ShouldReturnOk() throws Exception {

		// given
		Long groupId = 234L;

		BookGroupCommentSearchRequest request = new BookGroupCommentSearchRequest(10, 100L, SortDirection.DESC);
		List<BookGroupCommentResponse> bookGroupCommentResponseList = BookGroupCommentObjectProvider.mockCommentResponses;

		BookGroupCommentResponses bookGroupCommentResponses = new BookGroupCommentResponses(
			true,
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
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("groupId").description("?????? ID")
				),
				requestParameters(
					parameterWithName("groupCommentCursorId").description("?????? id??? ?????? ?????? ?????? ????????? ?????? ??????").optional(),
					parameterWithName("pageSize").description("?????? ????????? ?????? default : 10")
						.optional()
						.attributes(
							constrainsAttribute(BookGroupCommentSearchRequest.class, "pageSize")
						),
					parameterWithName("sortDirection").description("?????? ??????. default : DESC")
						.optional()
						.description("?????? ?????? : " +
							generateLinkCode(DocumentLinkGenerator.DocUrl.SORT_DIRECTION)
						)
				),
				responseFields(
					fieldWithPath("count").description("?????? ??????").type(JsonFieldType.NUMBER),
					fieldWithPath("isEmpty").description("???????????? ????????? empty = true").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isFirst").description("??? ?????? ????????? ??????").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isLast").description("????????? ????????? ??????").type(JsonFieldType.BOOLEAN),
					fieldWithPath("hasNext").description("?????? ????????? ?????? ??????").type(JsonFieldType.BOOLEAN),
					fieldWithPath("bookGroup.isPublic").description("?????? ?????? ??????").optional().type(JsonFieldType.BOOLEAN),
					fieldWithPath("bookGroupComments[]").type(JsonFieldType.ARRAY).description("?????? ??????"),
					fieldWithPath("bookGroupComments[].commentId").type(JsonFieldType.NUMBER).description("?????? ID"),
					fieldWithPath("bookGroupComments[].contents").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("bookGroupComments[].bookGroupId").type(JsonFieldType.NUMBER).description("?????? ID"),
					fieldWithPath("bookGroupComments[].parentCommentId").type(JsonFieldType.NUMBER)
						.description("?????? ?????? ID"),
					fieldWithPath("bookGroupComments[].userId").type(JsonFieldType.NUMBER).description("????????? ID"),
					fieldWithPath("bookGroupComments[].userProfileImage").type(JsonFieldType.STRING)
						.description("????????? ????????? ?????????"),
					fieldWithPath("bookGroupComments[].createdAt").type(JsonFieldType.STRING).description("?????????"),
					fieldWithPath("bookGroupComments[].modifiedAt").type(JsonFieldType.STRING).description("?????????"),
					fieldWithPath("bookGroupComments[].nickname").type(JsonFieldType.STRING).description("?????????"),
					fieldWithPath("bookGroupComments[].writtenByCurrentUser").type(JsonFieldType.BOOLEAN)
						.description("?????? ?????? ??????")
				)));

		verify(bookGroupCommentService).findAllBookGroupCommentsByGroup(eq(request), any(), any());
	}

	@DisplayName("?????? ????????? ????????????.")
	@Test
	void updateBookGroupComment_ShouldReturnOk() throws Exception {
		// given
		Long groupId = 234L;
		Long bookGroupCommentId = 234L;
		String modifiedComment = "??? ??? ?????????";
		BookGroupCommentUpdateRequest request = new BookGroupCommentUpdateRequest(modifiedComment);

		willDoNothing().given(bookGroupCommentService)
			.updateBookGroupComment(groupId, 1L, bookGroupCommentId, request);

		// when then
		mockMvc.perform(patch("/api/book-groups/{groupId}/comments/{commentId}", groupId, bookGroupCommentId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(createJson(request))
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("groupId").description("?????? Id"),
					parameterWithName("commentId").description("?????? ?????? Id")
				),
				requestFields(
					fieldWithPath("comment").type(JsonFieldType.STRING).description("????????? ?????? ??????")
						.attributes(
							constrainsAttribute(BookGroupCommentUpdateRequest.class, "comment")
						)
				)
			));

	}

	@DisplayName("?????? ????????? ????????????.")
	@Test
	void deleteBookGroupComment_ShouldReturnOk() throws Exception {
		// given
		Long groupId = 234L;
		Long bookGroupCommentId = 234L;

		doNothing().when(bookGroupCommentService).deleteBookGroupComment(groupId, 1L, bookGroupCommentId);

		// when then
		mockMvc.perform(delete("/api/book-groups/{groupId}/comments/{commentId}", groupId, bookGroupCommentId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("groupId").description("?????? ID"),
					parameterWithName("commentId").description("?????? ID")
				)
			));

	}
}