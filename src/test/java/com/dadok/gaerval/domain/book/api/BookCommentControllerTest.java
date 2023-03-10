package com.dadok.gaerval.domain.book.api;

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
import java.time.LocalDateTime;

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
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.controller.ControllerSliceTest;
import com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator;
import com.dadok.gaerval.domain.book.dto.request.BookCommentCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentUpdateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponse;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponses;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookCommentService;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.testutil.BookCommentObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

@WebMvcTest(controllers = BookCommentController.class)
@WithMockCustomOAuth2LoginUser
class BookCommentControllerTest extends ControllerSliceTest {

	@MockBean
	private BookCommentService bookCommentService;

	@DisplayName("findBookComments - ?????? ID??? ?????? ????????? ????????? ????????????.")
	@Test
	void findBookComments_success() throws Exception {
		// given
		User user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1L);

		Slice<BookCommentResponse> bookFindResponses = QueryDslUtil.toSlice(
			BookCommentObjectProvider.createMockResponses(),
			PageRequest.of(0, 50, Sort.by(
				Sort.Direction.DESC, "comment_id"
			)));

		BookCommentSearchRequest bookCommentSearchRequest = new BookCommentSearchRequest(10, null, null);

		BookCommentResponses bookCommentResponses = new BookCommentResponses(bookFindResponses);

		given(bookCommentService.findBookCommentsBy(
			1L,
			1L,
			bookCommentSearchRequest
		)).willReturn(bookCommentResponses);

		// when
		mockMvc.perform(get("/api/books/{bookId}/comments", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookId").description("?????? ID")
				),
				requestParameters(
					parameterWithName("bookCommentCursorId").description("?????? id??? ?????? ?????? ?????? ????????? ?????? ??????").optional(),
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
					fieldWithPath("bookComments[]").type(JsonFieldType.ARRAY).description("?????? ??????"),
					fieldWithPath("bookComments[].commentId").type(JsonFieldType.NUMBER).description("????????? ID"),
					fieldWithPath("bookComments[].contents").type(JsonFieldType.STRING).description("????????? ??????"),
					fieldWithPath("bookComments[].bookId").type(JsonFieldType.NUMBER).description("???????????? ?????? ?????? ID"),
					fieldWithPath("bookComments[].userId").type(JsonFieldType.NUMBER).description("????????? ????????? ID"),
					fieldWithPath("bookComments[].userProfileImage").type(JsonFieldType.STRING)
						.description("????????? ????????? ????????? ????????? URL"),
					fieldWithPath("bookComments[].createdAt").type(JsonFieldType.STRING)
						.description("????????? ?????? ??????(yyyy-MM-dd HH:mm:ss)"),
					fieldWithPath("bookComments[].modifiedAt").type(JsonFieldType.STRING)
						.description("????????? ?????? ??????(yyyy-MM-dd HH:mm:ss)"),
					fieldWithPath("bookComments[].nickname").type(JsonFieldType.STRING).description("????????? ????????? ?????????"),
					fieldWithPath("bookComments[].writtenByCurrentUser").type(JsonFieldType.BOOLEAN)
						.description("?????? ?????? ??????")
				)
			));

		// then
		verify(bookCommentService).findBookCommentsBy(
			1L,
			1L,
			bookCommentSearchRequest
		);
	}

	@DisplayName("saveBookComment - ????????? ??? ????????? ????????????.")
	@Test
	void saveBookComment_success() throws Exception {
		// given
		BookCommentCreateRequest bookCommentCreateRequest = BookCommentObjectProvider.createBookCommentCreateRequest();

		given(bookCommentService.createBookComment(
			1L,
			1L,
			bookCommentCreateRequest
		)).willReturn(1L);

		// when
		mockMvc.perform(post("/api/books/{bookId}/comments", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.content(objectMapper.writeValueAsString(bookCommentCreateRequest))
				.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isCreated())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookId").description("?????? ID")
				),
				requestFields(
					fieldWithPath("comment").description("?????? ??????").type(JsonFieldType.STRING)
				),
				responseFields(
					fieldWithPath("commentId").description("????????? ?????? ID").type(JsonFieldType.NUMBER)
				)
			));

		// then
		verify(bookCommentService).createBookComment(
			1L,
			1L,
			bookCommentCreateRequest
		);
	}

	@DisplayName("modifyBookComment - ?????? ????????? ????????????.")
	@Test
	void modifyBookComment_success() throws Exception {
		// given
		BookCommentUpdateRequest bookCommentUpdateRequest = new BookCommentUpdateRequest(1L, "????????? ??????yo(?????? ????????? ??????)");
		BookCommentResponse expectedBookCommentResponse = new BookCommentResponse(1L, "????????? ??????yo(?????? ????????? ??????)", 1L, 1L,
			UserObjectProvider.PICTURE_URL, LocalDateTime.of(2023, 3, 7, 10, 20), LocalDateTime.now(), "??????", true);

		given(bookCommentService.updateBookComment(
			1L,
			1L,
			bookCommentUpdateRequest
		)).willReturn(expectedBookCommentResponse);

		// when
		mockMvc.perform(patch("/api/books/{bookId}/comments", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.content(objectMapper.writeValueAsString(bookCommentUpdateRequest))
				.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookId").description("?????? ID")
				),
				requestFields(
					fieldWithPath("comment").description("?????? ??????").type(JsonFieldType.STRING),
					fieldWithPath("commentId").description("?????? ?????????").type(JsonFieldType.NUMBER)
				),
				responseFields(
					fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("????????? ID"),
					fieldWithPath("contents").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
					fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("???????????? ?????? ?????? ID"),
					fieldWithPath("userId").type(JsonFieldType.NUMBER).description("????????? ????????? ID"),
					fieldWithPath("userProfileImage").type(JsonFieldType.STRING).description("????????? ????????? ????????? ????????? URL"),
					fieldWithPath("createdAt").type(JsonFieldType.STRING).description("????????? ?????? ??????(yyyy-MM-dd HH:mm:ss)"),
					fieldWithPath("modifiedAt").type(JsonFieldType.STRING)
						.description("????????? ?????? ??????(yyyy-MM-dd HH:mm:ss)"),
					fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ????????? ?????????"),
					fieldWithPath("writtenByCurrentUser").type(JsonFieldType.BOOLEAN).description("?????? ?????? ??????")
				)
			));

		// then
		verify(bookCommentService).updateBookComment(
			1L,
			1L,
			bookCommentUpdateRequest
		);
	}

	@DisplayName("?????? ????????? ????????????.")
	@Test
	void deleteBookComment_ShouldReturnOk() throws Exception {
		// given
		Long bookId = 234L;
		Long commentId = 234L;

		doNothing().when(bookCommentService).deleteBookComment(bookId, 1L, commentId);

		// when then
		mockMvc.perform(delete("/api/books/{bookId}/comments/{commentId}", bookId, commentId)
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
					parameterWithName("bookId").description("?????? ID"),
					parameterWithName("commentId").description("?????? ID")
				)
			));

	}

}