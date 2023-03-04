package com.dadok.gaerval.domain.book_group.api;

import static com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator.*;
import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
import org.springframework.util.LinkedMultiValueMap;

import com.dadok.gaerval.controller.ControllerTest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.service.BookGroupService;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

@WithMockCustomOAuth2LoginUser
@WebMvcTest(BookGroupController.class)
class BookGroupControllerTest extends ControllerTest {

	@MockBean
	private BookGroupService bookGroupService;

	@DisplayName("findAllBookGroups - 모임 리스트를 조회한다.")
	@Test
	void findAllBookGroups() throws Exception {
		//given
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, 999L, SortDirection.DESC);

		List<BookGroupResponse> responses = List.of(
			new BookGroupResponse(
				999L, "모임999", "모임 999에용", 2L, 100L, 4452L,
				"http://bookImageUrl1.com", 1341234L, "http://ownerProfile1.com",
				"나는모임장이다1"
			),

			new BookGroupResponse(
				997L, "모임997", "모임 997에용", 5L, 2L, 2083L,
				"http://bookImageUrl1.com", 941234L, "http://ownerProfile1.com",
				"나는모임장이다1"
			),

			new BookGroupResponse(
				995L, "모임995", "모임 995에용", 5L, 0L, 442L,
				"http://bookImageUrl1.com", 1334L, "http://ownerProfile1.com",
				"나는모임장이다1"
			)
			,
			new BookGroupResponse(
				994L, "모임994", "모임 994에용", 3L, 30L, 44L,
				"http://bookImageUrl1.com", 1341234L, "http://ownerProfile1.com",
				"나는모임장이다1"
			)
			,
			new BookGroupResponse(
				993L, "모임993", "모임 993에용", 4L, 4000L, 913452L,
				"http://bookImageUrl1.com", 123234L, "http://ownerProfile1.com",
				"나는모임장이다1"
			)
		);

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
					fieldWithPath("bookGroups[].memberCount").type(JsonFieldType.NUMBER).description("모임 현재 멤버 수"),
					fieldWithPath("bookGroups[].commentCount").type(JsonFieldType.NUMBER).description("모임 현재 댓글 수"),
					fieldWithPath("bookGroups[].bookId").type(JsonFieldType.NUMBER).description("모임 책 id"),
					fieldWithPath("bookGroups[].bookImageUrl").type(JsonFieldType.STRING).description("모임 책 image url"),
					fieldWithPath("bookGroups[].ownerId").type(JsonFieldType.NUMBER).description("모임장 id"),
					fieldWithPath("bookGroups[].ownerProfileUrl").type(JsonFieldType.STRING).description("모임장 프로필 url"),
					fieldWithPath("bookGroups[].ownerNickname").type(JsonFieldType.STRING).description("모임장 닉네임"
					)
				)
			));
		//then

	}

}