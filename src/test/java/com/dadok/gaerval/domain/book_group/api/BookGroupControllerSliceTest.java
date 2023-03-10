package com.dadok.gaerval.domain.book_group.api;

import static com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator.*;
import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;

import com.dadok.gaerval.controller.ControllerSliceTest;
import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupJoinRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupUpdateRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupDetailResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.service.BookGroupService;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.testutil.BookGroupObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

@WithMockCustomOAuth2LoginUser
@WebMvcTest(BookGroupController.class)
class BookGroupControllerSliceTest extends ControllerSliceTest {

	@MockBean
	private BookGroupService bookGroupService;

	@DisplayName("findAllBookGroups - ?????? ???????????? ????????????.")
	@Test
	void findAllBookGroups() throws Exception {
		//given
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, 999L, SortDirection.DESC);

		List<BookGroupResponse> responses = BookGroupObjectProvider.mockBookGroupResponses();

		BookGroupResponses bookGroupResponses = new BookGroupResponses(
			QueryDslUtil.toSlice(responses, PageRequest.of(0, 10)));

		given(bookGroupService.findAllBookGroups(request))
			.willReturn(bookGroupResponses);

		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		params.add("pageSize", request.pageSize().toString());
		params.add("groupCursorId", "999");
		params.add("sortDirection", SortDirection.DESC.name());

		//when
		mockMvc.perform(get("/api/book-groups")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.params(params)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				requestParameters(
					parameterWithName("pageSize").description("?????? ????????? ???. default : 10").optional()
						.attributes(
							constrainsAttribute(BookGroupSearchRequest.class, "pageSize")
						),
					parameterWithName("groupCursorId").description("?????? book Id. ??????id??? ?????? DESC??? ?????? ?????? ?????????.").optional(),
					parameterWithName("sortDirection").description("?????? ??????. default : DESC").optional()
						.description("?????? ?????? : " +
							generateLinkCode(DocUrl.SORT_DIRECTION)
						)
				),
				responseFields(
					fieldWithPath("count").description("?????? ??????").type(JsonFieldType.NUMBER),
					fieldWithPath("isEmpty").description("???????????? ????????? empty = true").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isFirst").description("??? ?????? ????????? ??????. ").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isLast").description("????????? ????????? ??????.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("hasNext").description("?????? ????????? ?????? ??????.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("bookGroups").type(JsonFieldType.ARRAY).description("????????? ??????"),
					fieldWithPath("bookGroups[].bookGroupId").type(JsonFieldType.NUMBER).description("?????? id"),
					fieldWithPath("bookGroups[].title").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("bookGroups[].startDate").type(JsonFieldType.STRING).description("?????? ?????????"),
					fieldWithPath("bookGroups[].introduce").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("bookGroups[].endDate").type(JsonFieldType.STRING).description("?????? ?????????"),
					fieldWithPath("bookGroups[].maxMemberCount").type(JsonFieldType.NUMBER)
						.optional().description("?????? ?????? ?????? : ?????? ?????? ?????? null"),
					fieldWithPath("bookGroups[].hasJoinPasswd").type(JsonFieldType.BOOLEAN)
						.description("?????? ????????????(??????) ??????"),

					fieldWithPath("bookGroups[].isPublic").type(JsonFieldType.BOOLEAN).description("?????? ??????"),

					fieldWithPath("bookGroups[].memberCount").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? ???"),
					fieldWithPath("bookGroups[].commentCount").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? ???"),
					fieldWithPath("bookGroups[].book.id").type(JsonFieldType.NUMBER).description("?????? ??? id"),
					fieldWithPath("bookGroups[].book.imageUrl").type(JsonFieldType.STRING)
						.description("?????? ??? image url"),
					fieldWithPath("bookGroups[].owner.id").type(JsonFieldType.NUMBER).description("????????? id"),
					fieldWithPath("bookGroups[].owner.profileUrl").type(JsonFieldType.STRING)
						.description("????????? ????????? url"),
					fieldWithPath("bookGroups[].owner.nickname").type(JsonFieldType.STRING).description("????????? ?????????"
					)
				)
			));
		//then
	}

	@DisplayName("findMyBookGroups - ????????? ????????? ?????? ???????????? ????????????.")
	@Test
	void findMyBookGroups() throws Exception {
		//given
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, 999L, SortDirection.DESC);

		List<BookGroupResponse> responses = BookGroupObjectProvider.mockBookGroupResponses();

		BookGroupResponses bookGroupResponses = new BookGroupResponses(
			QueryDslUtil.toSlice(responses, PageRequest.of(0, 10)));

		given(bookGroupService.findAllBookGroupsByUser(eq(request), any()))
			.willReturn(bookGroupResponses);

		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		params.add("pageSize", request.pageSize().toString());
		params.add("groupCursorId", "999");
		params.add("sortDirection", SortDirection.DESC.name());

		//when
		mockMvc.perform(get("/api/book-groups/me")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.params(params)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				requestParameters(
					parameterWithName("pageSize").description("?????? ????????? ???. default : 10").optional()
						.attributes(
							constrainsAttribute(BookGroupSearchRequest.class, "pageSize")
						),
					parameterWithName("groupCursorId").description("?????? book Id. ??????id??? ?????? DESC??? ?????? ?????? ?????????.").optional(),
					parameterWithName("sortDirection").description("?????? ??????. default : DESC").optional()
						.description("?????? ?????? : " +
							generateLinkCode(DocUrl.SORT_DIRECTION)
						)
				),
				responseFields(
					fieldWithPath("count").description("?????? ??????").type(JsonFieldType.NUMBER),
					fieldWithPath("isEmpty").description("???????????? ????????? empty = true").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isFirst").description("??? ?????? ????????? ??????. ").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isLast").description("????????? ????????? ??????.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("hasNext").description("?????? ????????? ?????? ??????.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("bookGroups").type(JsonFieldType.ARRAY).description("????????? ??????"),
					fieldWithPath("bookGroups[].bookGroupId").type(JsonFieldType.NUMBER).description("?????? id"),
					fieldWithPath("bookGroups[].title").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("bookGroups[].introduce").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("bookGroups[].startDate").type(JsonFieldType.STRING).description("?????? ?????????"),
					fieldWithPath("bookGroups[].endDate").type(JsonFieldType.STRING).description("?????? ?????????"),
					fieldWithPath("bookGroups[].maxMemberCount").type(JsonFieldType.NUMBER)
						.optional().description("?????? ?????? ?????? : ?????? ?????? ?????? null"),
					fieldWithPath("bookGroups[].hasJoinPasswd").type(JsonFieldType.BOOLEAN)
						.description("?????? ????????????(??????) ??????"),
					fieldWithPath("bookGroups[].hasJoinPasswd").type(JsonFieldType.BOOLEAN)
						.description("?????? ????????????(??????) ??????"),
					fieldWithPath("bookGroups[].isPublic").type(JsonFieldType.BOOLEAN).description("?????? ??????"),

					fieldWithPath("bookGroups[].memberCount").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? ???"),
					fieldWithPath("bookGroups[].commentCount").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? ???"),
					fieldWithPath("bookGroups[].book.id").type(JsonFieldType.NUMBER).description("?????? ??? id"),
					fieldWithPath("bookGroups[].book.imageUrl").type(JsonFieldType.STRING)
						.description("?????? ??? image url"),
					fieldWithPath("bookGroups[].owner.id").type(JsonFieldType.NUMBER).description("????????? id"),
					fieldWithPath("bookGroups[].owner.profileUrl").type(JsonFieldType.STRING)
						.description("????????? ????????? url"),
					fieldWithPath("bookGroups[].owner.nickname").type(JsonFieldType.STRING).description("????????? ?????????"
					)
				)
			));

	}

	@DisplayName("createBookGroup - ????????? ????????????")
	@Test
	void createBookGroup() throws Exception {
		// Given
		var book = BookObjectProvider.createRequiredFieldBook();
		var request = new BookGroupCreateRequest(book.getId(),
			"????????? ?????????", LocalDate.now(), LocalDate.now().plusDays(2), 5, "???????????? ????????????", true, "?????? ??????????", "????????????????????????", false
		);

		given(bookGroupService.createBookGroup(any(), eq(request)))
			.willReturn(1L);

		// When // Then
		mockMvc.perform(post("/api/book-groups")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(createJson(request))
			).andExpect(status().isCreated())
			.andExpect(header().string("Location", containsString("/api/book-groups/1")))
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				requestFields(
					fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("?????? id")
						.attributes(
							constrainsAttribute(BookCreateRequest.class, "bookId")
						),
					fieldWithPath("title").type(JsonFieldType.STRING).description("?????? ??????")
						.attributes(
							constrainsAttribute(BookGroupCreateRequest.class, "title")
						),
					fieldWithPath("startDate").type(JsonFieldType.STRING).description("?????? ?????? ??????").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "startDate")
					),
					fieldWithPath("endDate").type(JsonFieldType.STRING).description("?????? ?????? ??????").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "endDate")
					),
					fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER)
						.description("?????? ?????? ?????? ?????? : ?????? ?????? ?????? null")
						.optional()
						.attributes(
							constrainsAttribute(BookGroupCreateRequest.class, "maxMemberCount")
						),
					fieldWithPath("introduce").type(JsonFieldType.STRING).description("?????? ?????????").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "introduce")
					),
					fieldWithPath("hasJoinPasswd").type(JsonFieldType.BOOLEAN).description("?????? ????????????(??????) ??????").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "hasJoinPasswd")
					),
					fieldWithPath("joinQuestion").type(JsonFieldType.STRING).optional()
						.description("?????? ?????? ?????? ?????? : hasJoinPasswd = true ??? ????????????").attributes(
							constrainsAttribute(BookGroupCreateRequest.class, "joinQuestion")
						),
					fieldWithPath("joinPasswd").type(JsonFieldType.STRING).optional()
						.description("?????? ?????? ?????? ???????????? : hasJoinPasswd = true ??? ????????????").attributes(
							constrainsAttribute(BookGroupCreateRequest.class, "joinPasswd")
						),
					fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("?????? ??????").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "isPublic")
					)
				),
				responseHeaders(
					headerWithName(HttpHeaders.LOCATION).description("????????? ?????? ?????? ?????? ????????? uri")
				),
				responseFields(
					fieldWithPath("bookGroupId").type(JsonFieldType.NUMBER).description("?????? Id")
				)
			));
	}

	@DisplayName("updateBookGroup - ????????? ????????????")
	@Test
	void updateBookGroup() throws Exception {
		// Given
		var book = BookObjectProvider.createRequiredFieldBook();
		var bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 1L);
		var request = new BookGroupUpdateRequest(
			bookGroup.getTitle(), "???????????? ?????? ??????", LocalDate.now().plusDays(4), 4
		);

		doNothing().when(bookGroupService).updateBookGroup(eq(bookGroup.getId()), any(), eq(request));

		// When // Then
		mockMvc.perform(patch("/api/book-groups/{bookGroupId}", bookGroup.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(createJson(request))
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookGroupId").description("?????? Id (bookGroup)")
				),
				requestFields(
					fieldWithPath("title").type(JsonFieldType.STRING).description("?????? ??????")
						.attributes(
							constrainsAttribute(BookGroupCreateRequest.class, "title")
						),
					fieldWithPath("introduce").type(JsonFieldType.STRING).description("?????? ?????????").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "introduce")
					),

					fieldWithPath("endDate").type(JsonFieldType.STRING).description("?????? ?????? ??????").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "endDate")
					),
					fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER)
						.description("?????? ?????? ?????? ?????? : ???????????? ?????? null")
						.optional()
						.attributes(
							constrainsAttribute(BookGroupCreateRequest.class, "maxMemberCount")
						)
				)
			));
	}

	@DisplayName("deleteBookGroup - ????????? ????????????")
	@Test
	void deleteBookGroup() throws Exception {
		// Given
		doNothing().when(bookGroupService).deleteBookGroup(eq(1L), any());

		// When // Then
		mockMvc.perform(delete("/api/book-groups/{groupId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("groupId").description("?????? Id (bookGroup)")
				)
			));
	}

	@DisplayName("findGroup - ?????? ????????? ????????? ????????????.")
	@Test
	void findBookGroup_success() throws Exception {
		//given
		long bookGroupId = 999L;
		String title = "???????????? JPA ????????????";
		String introduce = "??????????????? JPA ??? ???????????? ????????? ???????????????";
		long ownerId = 1L;
		long bookId = 10L;
		boolean isOwner = false;
		boolean isGroupMember = true;
		LocalDate startDate = LocalDate.now().plusDays(1);
		LocalDate endDate = LocalDate.now().plusDays(7);
		String bookTitle = "Java ORM ?????? JPA ???????????????";
		String bookImageUrl = "http://jpaimage.jpeg";
		int maxMemberCount = 5;
		long currentMemberCount = 2L;
		long commentCount = 5L;

		BookGroupDetailResponse bookGroupDetailResponse = new BookGroupDetailResponse(bookGroupId,
			title, introduce,
			startDate, endDate, false, "????????? ??????????", true, maxMemberCount, currentMemberCount, commentCount,
			new BookGroupDetailResponse.OwnerResponse(ownerId),
			new BookGroupDetailResponse.BookResponse(bookId, bookImageUrl, bookTitle),
			isOwner, isGroupMember
		);

		given(bookGroupService.findGroup(1L, bookGroupId))
			.willReturn(bookGroupDetailResponse);
		//when

		mockMvc.perform(get("/api/book-groups/{groupId}", bookGroupId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					),
					pathParameters(
						parameterWithName("groupId").description("?????? Id (bookGroup)")
					),
					responseFields(
						fieldWithPath("bookGroupId").type(JsonFieldType.NUMBER).description("?????? Id"),
						fieldWithPath("title").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("introduce").type(JsonFieldType.STRING).description("?????? ????????? "),
						fieldWithPath("owner").type(JsonFieldType.OBJECT).description("????????? ??????"),
						fieldWithPath("owner.id").type(JsonFieldType.NUMBER).description("????????? Id(user Id)"),
						fieldWithPath("isOwner").type(JsonFieldType.BOOLEAN).description("??????????????? ??????????????? ??????"),
						fieldWithPath("isGroupMember").type(JsonFieldType.BOOLEAN).description("???????????? ????????? ???????????? ???????????? ??????"),
						fieldWithPath("startDate").type(JsonFieldType.STRING).description("?????? ?????????"),
						fieldWithPath("endDate").type(JsonFieldType.STRING).description("?????? ?????????"),
						fieldWithPath("hasJoinPasswd").type(JsonFieldType.BOOLEAN).description("?????? ????????????(??????) ??????"),
						fieldWithPath("joinQuestion").type(JsonFieldType.STRING).description("?????? ????????? ??????"),
						fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("?????? ?????? ?????? ??????"),
						fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER)
							.optional()
							.description("?????? ?????? ?????? : ?????? ?????? ?????? null"),
						fieldWithPath("currentMemberCount").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
						fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? ???"),
						fieldWithPath("book").type(JsonFieldType.OBJECT).description("??? ??????"),
						fieldWithPath("book.id").type(JsonFieldType.NUMBER).description("??? Id."),
						fieldWithPath("book.title").type(JsonFieldType.STRING).description("?????? ??? ??????"),
						fieldWithPath("book.imageUrl").type(JsonFieldType.STRING).description("?????? ??? image url")
					)
				)
			);
		//then
	}

	@DisplayName("join - ????????? ????????????. ???????????? ?????????")
	@Test
	void BookGroupJoin() throws Exception {
		//given
		Long bookGroupId = 1L;
		BookGroupJoinRequest request = new BookGroupJoinRequest(null);

		willDoNothing().given(bookGroupService).join(bookGroupId, 1L, request);
		//when
		mockMvc.perform(post("/api/book-groups/{groupId}/join", bookGroupId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(createJson(request))
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					),
					pathParameters(
						parameterWithName("groupId").description("?????? Id (bookGroup)")
					),
					requestFields(fieldWithPath("joinPasswd").type(JsonFieldType.STRING).optional()
						.description("?????? ????????????. "
							+ "\n ?????????????????? ????????? null ??????"))
				)
			)
		;
	}

	@DisplayName("leave - ????????? ????????????.")
	@Test
	void BookGroupLeave() throws Exception {
		//given
		Long bookGroupId = 1L;

		willDoNothing().given(bookGroupService).leave(bookGroupId, 1L);
		//when
		mockMvc.perform(delete("/api/book-groups/{groupId}/leave", bookGroupId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					),
					pathParameters(
						parameterWithName("groupId").description("?????? Id (bookGroup)")
					)
				)
			)
		;
	}
}