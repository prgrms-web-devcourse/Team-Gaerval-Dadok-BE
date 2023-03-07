package com.dadok.gaerval.domain.book_group.api;

import static com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator.*;
import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;
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

	@DisplayName("findAllBookGroups - 모임 리스트를 조회한다.")
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
					parameterWithName("pageSize").description("요청 데이터 수. default : 10").optional()
						.attributes(
							constrainsAttribute(BookGroupSearchRequest.class, "pageSize")
						),
					parameterWithName("groupCursorId").description("커서 book Id. 커서id가 없고 DESC면 가장 최근 데이터.").optional(),
					parameterWithName("sortDirection").description("정렬 순서. default : DESC").optional()
						.description("정렬 방식 : " +
							generateLinkCode(DocUrl.SORT_DIRECTION)
						)
				),
				responseFields(
					fieldWithPath("count").description("그룹 갯수").type(JsonFieldType.NUMBER),
					fieldWithPath("isEmpty").description("데이터가 없으면 empty = true").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isFirst").description("첫 번째 페이지 여부. ").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isLast").description("마지막 페이지 여부.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("hasNext").description("다음 데이터 존재 여부.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("bookGroups").type(JsonFieldType.ARRAY).description("모임들 명단"),
					fieldWithPath("bookGroups[].bookGroupId").type(JsonFieldType.NUMBER).description("모임 id"),
					fieldWithPath("bookGroups[].title").type(JsonFieldType.STRING).description("모임 제목"),
					fieldWithPath("bookGroups[].introduce").type(JsonFieldType.STRING).description("모임 소개"),
					fieldWithPath("bookGroups[].maxMemberCount").type(JsonFieldType.NUMBER)
						.description("모임 최대 멤버 수"),
					fieldWithPath("bookGroups[].hasJoinPasswd").type(JsonFieldType.BOOLEAN)
						.description("모임 비밀번호(잠김) 여부"),

					fieldWithPath("bookGroups[].isPublic").type(JsonFieldType.BOOLEAN).description("공개 여부"),

					fieldWithPath("bookGroups[].memberCount").type(JsonFieldType.NUMBER).description("모임 현재 멤버 수"),
					fieldWithPath("bookGroups[].commentCount").type(JsonFieldType.NUMBER).description("모임 현재 댓글 수"),
					fieldWithPath("bookGroups[].book.id").type(JsonFieldType.NUMBER).description("모임 책 id"),
					fieldWithPath("bookGroups[].book.imageUrl").type(JsonFieldType.STRING)
						.description("모임 책 image url"),
					fieldWithPath("bookGroups[].owner.id").type(JsonFieldType.NUMBER).description("모임장 id"),
					fieldWithPath("bookGroups[].owner.profileUrl").type(JsonFieldType.STRING)
						.description("모임장 프로필 url"),
					fieldWithPath("bookGroups[].owner.nickname").type(JsonFieldType.STRING).description("모임장 닉네임"
					)
				)
			));
		//then
	}

	@DisplayName("findMyBookGroups - 자신이 참여한 모임 리스트를 조회한다.")
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
					parameterWithName("pageSize").description("요청 데이터 수. default : 10").optional()
						.attributes(
							constrainsAttribute(BookGroupSearchRequest.class, "pageSize")
						),
					parameterWithName("groupCursorId").description("커서 book Id. 커서id가 없고 DESC면 가장 최근 데이터.").optional(),
					parameterWithName("sortDirection").description("정렬 순서. default : DESC").optional()
						.description("정렬 방식 : " +
							generateLinkCode(DocUrl.SORT_DIRECTION)
						)
				),
				responseFields(
					fieldWithPath("count").description("그룹 갯수").type(JsonFieldType.NUMBER),
					fieldWithPath("isEmpty").description("데이터가 없으면 empty = true").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isFirst").description("첫 번째 페이지 여부. ").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isLast").description("마지막 페이지 여부.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("hasNext").description("다음 데이터 존재 여부.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("bookGroups").type(JsonFieldType.ARRAY).description("모임들 명단"),
					fieldWithPath("bookGroups[].bookGroupId").type(JsonFieldType.NUMBER).description("모임 id"),
					fieldWithPath("bookGroups[].title").type(JsonFieldType.STRING).description("모임 제목"),
					fieldWithPath("bookGroups[].introduce").type(JsonFieldType.STRING).description("모임 소개"),
					fieldWithPath("bookGroups[].maxMemberCount").type(JsonFieldType.NUMBER)
						.description("모임 최대 멤버 수"),
					fieldWithPath("bookGroups[].hasJoinPasswd").type(JsonFieldType.BOOLEAN)
						.description("모임 비밀번호(잠김) 여부"),
					fieldWithPath("bookGroups[].hasJoinPasswd").type(JsonFieldType.BOOLEAN)
						.description("모임 비밀번호(잠김) 여부"),
					fieldWithPath("bookGroups[].isPublic").type(JsonFieldType.BOOLEAN).description("공개 여부"),

					fieldWithPath("bookGroups[].memberCount").type(JsonFieldType.NUMBER).description("모임 현재 멤버 수"),
					fieldWithPath("bookGroups[].commentCount").type(JsonFieldType.NUMBER).description("모임 현재 댓글 수"),
					fieldWithPath("bookGroups[].book.id").type(JsonFieldType.NUMBER).description("모임 책 id"),
					fieldWithPath("bookGroups[].book.imageUrl").type(JsonFieldType.STRING)
						.description("모임 책 image url"),
					fieldWithPath("bookGroups[].owner.id").type(JsonFieldType.NUMBER).description("모임장 id"),
					fieldWithPath("bookGroups[].owner.profileUrl").type(JsonFieldType.STRING)
						.description("모임장 프로필 url"),
					fieldWithPath("bookGroups[].owner.nickname").type(JsonFieldType.STRING).description("모임장 닉네임"
					)
				)
			));

	}

	@DisplayName("createBookGroup - 모임을 생성한다")
	@Test
	void createBookGroup() throws Exception {
		// Given
		var book = BookObjectProvider.createRequiredFieldBook();
		var request = new BookGroupCreateRequest(book.getId(),
			"소모임 화이팅", LocalDate.now(), LocalDate.now(), 5, "우리끼리 옹기종기", true, "월든 작가는?", "헨리데이빗소로우", false
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
					fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("도서 id")
						.attributes(
							constrainsAttribute(BookCreateRequest.class, "bookId")
						),
					fieldWithPath("title").type(JsonFieldType.STRING).description("모임 제목")
						.attributes(
							constrainsAttribute(BookGroupCreateRequest.class, "title")
						),
					fieldWithPath("startDate").type(JsonFieldType.STRING).description("모임 시작 날짜").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "startDate")
					),
					fieldWithPath("endDate").type(JsonFieldType.STRING).description("모임 종료 날짜").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "endDate")
					),
					fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER).description("모임 참여 최대 인원").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "maxMemberCount")
					),
					fieldWithPath("introduce").type(JsonFieldType.STRING).description("모임 소개글").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "introduce")
					),
					fieldWithPath("hasJoinPasswd").type(JsonFieldType.BOOLEAN).description("모임 비밀번호(잠김) 여부").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "hasJoinPasswd")
					),
					fieldWithPath("joinQuestion").type(JsonFieldType.STRING).optional()
						.description("모임 참여 제한 질문 : hasJoinPasswd = true 시 필수사항").attributes(
							constrainsAttribute(BookGroupCreateRequest.class, "joinQuestion")
						),
					fieldWithPath("joinPasswd").type(JsonFieldType.STRING).optional()
						.description("모임 참여 제한 비밀번호 : hasJoinPasswd = true 시 필수사항").attributes(
							constrainsAttribute(BookGroupCreateRequest.class, "joinPasswd")
						),
					fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("공개 여부").attributes(
						constrainsAttribute(BookGroupCreateRequest.class, "isPublic")
					)
				),
				responseHeaders(
					headerWithName(HttpHeaders.LOCATION).description("생성된 모임 상세 조회 리소스 uri")
				)
			));
	}

	@DisplayName("deleteBookGroup - 모임을 삭제한다")
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
					parameterWithName("groupId").description("모임 Id (bookGroup)")
				)
			));
	}

	@DisplayName("findGroup - 모임 디테일 정보를 반환한다.")
	@Test
	void findBookGroup_success() throws Exception {
		//given
		long bookGroupId = 999L;
		String title = "김영한님 JPA 읽으실분";
		String introduce = "우리모임은 JPA 책 스터디를 하려고 모여있어요";
		long ownerId = 1L;
		long bookId = 10L;
		boolean isOwner = false;
		boolean isGroupMember = true;
		LocalDate startDate = LocalDate.now().plusDays(1);
		LocalDate endDate = LocalDate.now().plusDays(7);
		String bookTitle = "Java ORM 표준 JPA 프로그래밍";
		String bookImageUrl = "http://jpaimage.jpeg";
		int maxMemberCount = 5;
		long currentMemberCount = 2L;
		long commentCount = 5L;

		BookGroupDetailResponse bookGroupDetailResponse = new BookGroupDetailResponse(bookGroupId,
			title, introduce,
			startDate, endDate, false, true, maxMemberCount, currentMemberCount, commentCount,
			new BookGroupDetailResponse.OwnerResponse(ownerId),
			new BookGroupDetailResponse.BookResponse(bookId, bookImageUrl, bookTitle),
			isOwner, isGroupMember
		);

		given(bookGroupService.findGroup(1L, bookGroupId))
			.willReturn(bookGroupDetailResponse);
		//when

		mockMvc.perform(get("/api/book-groups/{groupId}", bookGroupId)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					),
					pathParameters(
						parameterWithName("groupId").description("모임 Id (bookGroup)")
					),
					responseFields(
						fieldWithPath("bookGroupId").type(JsonFieldType.NUMBER).description("모임 Id"),
						fieldWithPath("title").type(JsonFieldType.STRING).description("모임 제목"),
						fieldWithPath("introduce").type(JsonFieldType.STRING).description("모임 소개글 "),
						fieldWithPath("owner").type(JsonFieldType.OBJECT).description("모임장 정보"),
						fieldWithPath("owner.id").type(JsonFieldType.NUMBER).description("모임장 Id(user Id)"),
						fieldWithPath("isOwner").type(JsonFieldType.BOOLEAN).description("요청유저가 모임장인지 여부"),
						fieldWithPath("isGroupMember").type(JsonFieldType.BOOLEAN).description("요청자가 모임에 속해있는 유저인지 여부"),
						fieldWithPath("startDate").type(JsonFieldType.STRING).description("모임 시작일"),
						fieldWithPath("endDate").type(JsonFieldType.STRING).description("모임 종료일"),
						fieldWithPath("hasJoinPasswd").type(JsonFieldType.BOOLEAN).description("모임 비밀번호(잠김) 여부"),
						fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("모임 내용 공개 여부"),
						fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER).description("모임 최대 인원"),
						fieldWithPath("currentMemberCount").type(JsonFieldType.NUMBER).description("현재 모임 인원"),
						fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("현재 모임 댓글 수"),
						fieldWithPath("book").type(JsonFieldType.OBJECT).description("책 정보"),
						fieldWithPath("book.id").type(JsonFieldType.NUMBER).description("책 Id."),
						fieldWithPath("book.title").type(JsonFieldType.STRING).description("모임 책 제목"),
						fieldWithPath("book.imageUrl").type(JsonFieldType.STRING).description("모임 책 image url")
					)
				)
			);
		//then
	}

	@DisplayName("join - 그룹에 가입한다. 패스워드 미입력")
	@Test
	void join_success() throws Exception {
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
						parameterWithName("groupId").description("모임 Id (bookGroup)")
					),
					requestFields(fieldWithPath("joinPassword").type(JsonFieldType.STRING).optional()
						.description("모임 비밀번호. "
							+ "\n 설정되어있지 않다면 null 가능"))
				)
			)
		;
	}
}