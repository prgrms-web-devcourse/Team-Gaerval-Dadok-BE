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
import com.dadok.gaerval.domain.book.dto.request.BookCommentDeleteRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentUpdateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponse;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponses;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookCommentService;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentDeleteRequest;
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

	@DisplayName("findBookComments - 도서 ID를 통해 코멘트 목록을 가져온다.")
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
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookId").description("도서 ID")
				),
				requestParameters(
					parameterWithName("bookCommentCursorId").description("커서 id가 없을 경우 정렬 순서에 따라 결정").optional(),
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
					fieldWithPath("bookGroupComments[]").type(JsonFieldType.ARRAY).description("리뷰 목록"),
					fieldWithPath("bookGroupComments[].commentId").type(JsonFieldType.NUMBER).description("코멘트 ID"),
					fieldWithPath("bookGroupComments[].contents").type(JsonFieldType.STRING).description("코멘트 내용"),
					fieldWithPath("bookGroupComments[].bookId").type(JsonFieldType.NUMBER).description("코멘트가 속한 도서 ID"),
					fieldWithPath("bookGroupComments[].userId").type(JsonFieldType.NUMBER).description("코멘트 작성자 ID"),
					fieldWithPath("bookGroupComments[].userProfileImage").type(JsonFieldType.STRING).description("코멘트 작성자 프로필 이미지 URL"),
					fieldWithPath("bookGroupComments[].createdAt").type(JsonFieldType.STRING).description("코멘트 작성 일자(yyyy-MM-dd HH:mm:ss)"),
					fieldWithPath("bookGroupComments[].modifiedAt").type(JsonFieldType.STRING).description("코멘트 수정 일자(yyyy-MM-dd HH:mm:ss)"),
					fieldWithPath("bookGroupComments[].nickname").type(JsonFieldType.STRING).description("코멘트 작성자 닉네임"),
					fieldWithPath("bookGroupComments[].writtenByCurrentUser").type(JsonFieldType.BOOLEAN).description("댓글 본인 여부")
				)
			));

		// then
		verify(bookCommentService).findBookCommentsBy(
			1L,
			1L,
			bookCommentSearchRequest
		);
	}


	@DisplayName("saveBookComment - 새로운 책 댓글을 저장한다.")
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
		mockMvc.perform(post("/api/books/{bookId}/comment", 1L)
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
					parameterWithName("bookId").description("도서 ID")
				),
				requestFields(
					fieldWithPath("comment").description("댓글 내용").type(JsonFieldType.STRING)
				),
				responseFields(
					fieldWithPath("commentId").description("저장된 댓글 ID").type(JsonFieldType.NUMBER)
				)
			));

		// then
		verify(bookCommentService).createBookComment(
			1L,
			1L,
			bookCommentCreateRequest
		);
	}


	@DisplayName("modifyBookComment - 도서 댓글을 수정한다.")
	@Test
	void modifyBookComment_success() throws Exception {
		// given
		BookCommentUpdateRequest bookCommentUpdateRequest = new BookCommentUpdateRequest(1L, "좋대서 샀어yo(아직 못읽음 ㅜㅜ)");
		BookCommentResponse expectedBookCommentResponse = new BookCommentResponse(1L, "좋대서 샀어yo(아직 못읽음 ㅜㅜ)", 1L, 1L,
			UserObjectProvider.PICTURE_URL, LocalDateTime.of(2023, 3,7,10,20), LocalDateTime.now(), "티나", true);

		given(bookCommentService.updateBookComment(
			1L,
			1L,
			bookCommentUpdateRequest
		)).willReturn(expectedBookCommentResponse);

		// when
		mockMvc.perform(patch("/api/books/{bookId}/comment", 1L)
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
					parameterWithName("bookId").description("도서 ID")
				),
				requestFields(
					fieldWithPath("comment").description("댓글 내용").type(JsonFieldType.STRING),
					fieldWithPath("commentId").description("댓글 아이디").type(JsonFieldType.NUMBER)
				),
				responseFields(
					fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("코멘트 ID"),
					fieldWithPath("contents").type(JsonFieldType.STRING).description("수정된 코멘트 내용"),
					fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("코멘트가 속한 도서 ID"),
					fieldWithPath("userId").type(JsonFieldType.NUMBER).description("코멘트 작성자 ID"),
					fieldWithPath("userProfileImage").type(JsonFieldType.STRING).description("코멘트 작성자 프로필 이미지 URL"),
					fieldWithPath("createdAt").type(JsonFieldType.STRING).description("코멘트 작성 일자(yyyy-MM-dd HH:mm:ss)"),
					fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("코멘트 수정 일자(yyyy-MM-dd HH:mm:ss)"),
					fieldWithPath("nickname").type(JsonFieldType.STRING).description("코멘트 작성자 닉네임"),
					fieldWithPath("writtenByCurrentUser").type(JsonFieldType.BOOLEAN).description("댓글 본인 여부")
				)
			));

		// then
		verify(bookCommentService).updateBookComment(
			1L,
			1L,
			bookCommentUpdateRequest
		);
	}


	@DisplayName("댓글 삭제에 성공한다.")
	@Test
	void deleteBookComment_ShouldReturnOk() throws Exception {
		// given
		Long bookId = 234L;
		Long CommentId = 234L;

		BookGroupCommentDeleteRequest request = new BookGroupCommentDeleteRequest(CommentId);

		doNothing().when(bookCommentService).deleteBookComment(bookId, 1L, request);


		// when then
		mockMvc.perform(delete("/api/books/{bookId}/comment", bookId)
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
					parameterWithName("bookId").description("도서 ID")
				),
				requestFields(
					fieldWithPath("commentId").type(JsonFieldType.NUMBER)
						.description("삭제할 댓글 아이디")
						.attributes(
							constrainsAttribute(BookCommentDeleteRequest.class, "commentId")
						)
				)
			));


	}

}