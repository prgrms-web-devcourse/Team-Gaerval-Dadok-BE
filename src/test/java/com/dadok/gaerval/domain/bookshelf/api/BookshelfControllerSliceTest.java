package com.dadok.gaerval.domain.bookshelf.api;

import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.dadok.gaerval.controller.ControllerTest;
import com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.dto.request.InsertBookCreateRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookInShelfResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfSummaryResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.SuggestionBookshelvesByJobGroupResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.SuggestionBookshelvesResponses;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItemType;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

import lombok.SneakyThrows;

@WebMvcTest(controllers = BookshelfController.class)
class BookshelfControllerSliceTest extends ControllerTest {

	@MockBean
	private BookshelfService bookshelfService;

	@DisplayName("findSuggestionBookshelvesByJobGroup - 직군별 인기있는 책장 리스트 조회 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findSuggestionBookshelvesByJobGroup() throws Exception {
		// Given
		String jobGroup = JobGroup.DEVELOPMENT.getDescription();
		SuggestionBookshelvesByJobGroupResponses responses = new SuggestionBookshelvesByJobGroupResponses(
			jobGroup, List.of(new BookShelfSummaryResponse(23L, "영지님의 책장",
				List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "해리포터1",
						"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"),
					new BookShelfSummaryResponse.BookSummaryResponse(2L, "해리포터2",
						"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
			),
			new BookShelfSummaryResponse(13L, "규란님의 책장",
				List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "해리포터1",
						"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"),
					new BookShelfSummaryResponse.BookSummaryResponse(2L, "해리포터2",
						"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
			))
		);

		given(bookshelfService.findSuggestionBookshelvesByJobGroup(any(), eq(jobGroup)))
			.willReturn(responses);

		// When // Then
		mockMvc.perform(get("/api/suggestions/bookshelves")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.param("job_group", jobGroup)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.jobGroup").value(jobGroup))
			.andExpect(jsonPath("$.bookshelfResponses[*].bookshelfName").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].bookshelfId").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].books[*].bookId").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].books[*].title").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].books[*].imageUrl").exists())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				requestParameters(
					parameterWithName("job_group").description(
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)
							+ " (한글 코드명 입력)"
					)
				),
				responseFields(
					fieldWithPath("jobGroup").type(JsonFieldType.STRING).description("직군"),
					fieldWithPath("bookshelfResponses[].bookshelfName").type(JsonFieldType.STRING).description("책장 이름"),
					fieldWithPath("bookshelfResponses[].bookshelfId").type(JsonFieldType.NUMBER).description("책장 ID"),
					fieldWithPath("bookshelfResponses[].books[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("bookshelfResponses[].books[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("bookshelfResponses[].books[].imageUrl").type(JsonFieldType.STRING)
						.description("책 이미지 url")
				)
			));
	}

	@DisplayName("findSuggestionBookshelves - 미로그인 사용자 노출용 인기있는 책장 리스트 조회 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findSuggestionBookshelves() throws Exception {
		// Given
		String jobGroup = JobGroup.DEVELOPMENT.getDescription();
		var responses = new SuggestionBookshelvesResponses(
			List.of(new BookShelfSummaryResponse(23L, "영지님의 책장",
					List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "해리포터1",
							"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"),
						new BookShelfSummaryResponse.BookSummaryResponse(2L, "해리포터2",
							"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
				),
				new BookShelfSummaryResponse(13L, "규란님의 책장",
					List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "해리포터1",
							"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"),
						new BookShelfSummaryResponse.BookSummaryResponse(2L, "해리포터2",
							"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
				))
		);

		given(bookshelfService.findSuggestionBookshelves())
			.willReturn(responses);

		// When // Then
		mockMvc.perform(get("/api/suggestions/bookshelves/default")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.bookshelfResponses[*].bookshelfName").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].bookshelfId").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].books[*].bookId").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].books[*].title").exists())
			.andExpect(jsonPath("$.bookshelfResponses[*].books[*].imageUrl").exists())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				responseFields(
					fieldWithPath("bookshelfResponses[].bookshelfId").type(JsonFieldType.NUMBER)
						.description("책장 ID - (미로그인 사용자를 위한 api로 로그인 사용자 접근시 응답에 자신의 책장이 포함될 수 있습니다.)"),
					fieldWithPath("bookshelfResponses[].bookshelfName").type(JsonFieldType.STRING).description("책장 이름"),
					fieldWithPath("bookshelfResponses[].books[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("bookshelfResponses[].books[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("bookshelfResponses[].books[].imageUrl").type(JsonFieldType.STRING)
						.description("책 이미지 url")
				)
			));
	}

	@DisplayName("insertBookInBookshelf - 책장에 책 추가 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void insertBookInBookshelf() throws Exception {
		// Given
		InsertBookCreateRequest request = new InsertBookCreateRequest(55L);

		given(bookshelfService.insertBookSelfItem(any(), eq(23L), eq(request)))
			.willReturn(23L);

		// When // Then
		mockMvc.perform(post("/api/bookshelves/{bookshelvesId}/books", 23L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(createJson(request))
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookshelvesId").description("첵장 Id")
				),
				requestFields(
					fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("도서 id")
						.attributes(
							constrainsAttribute(InsertBookCreateRequest.class, "bookId")
						)
				)
			));
	}

	@SneakyThrows
	@DisplayName("removeBookFormBookshelf - 책장에 책 제거 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void removeBookFormBookshelf() {
		// Given
		given(bookshelfService.removeBookSelfItem(any(), eq(23L), eq(34L)))
			.willReturn(23L);

		// When // Then
		mockMvc.perform(delete("/api/bookshelves/{bookshelvesId}/books/{bookId}", 23L, 34L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookshelvesId").description("첵장 Id"),
					parameterWithName("bookId").description("책 Id")
				)
			));
	}

	@DisplayName("findSummaryBookshelf - 자신의 책장 요약 조회 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findMySummaryBookshelf() throws Exception {
		// Given
		var responses = new BookShelfSummaryResponse(23L, "영지님의 책장",
			List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "해리포터",
				"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
		);

		given(bookshelfService.findSummaryBookshelf(any()))
			.willReturn(responses);

		// When // Then
		mockMvc.perform(get("/api/bookshelves/me")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.bookshelfName").exists())
			.andExpect(jsonPath("$.bookshelfId").exists())
			.andExpect(jsonPath("$.books[*].bookId").exists())
			.andExpect(jsonPath("$.books[*].title").exists())
			.andExpect(jsonPath("$.books[*].imageUrl").exists())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				responseFields(
					fieldWithPath("bookshelfName").type(JsonFieldType.STRING).description("책장 이름"),
					fieldWithPath("bookshelfId").type(JsonFieldType.NUMBER).description("책장 ID"),
					fieldWithPath("books[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("books[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("books[].imageUrl").type(JsonFieldType.STRING)
						.description("책 이미지 url")
				)
			));
	}

	@DisplayName("findSummaryBookshelf - 사용자의 책장 요약 조회 - 성공")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findSummaryBookshelfByUserId() throws Exception {
		// Given
		var responses = new BookShelfSummaryResponse(23L, "영지님의 책장",
			List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "해리포터1",
					"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"),
				new BookShelfSummaryResponse.BookSummaryResponse(2L, "해리포터2",
					"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
		);

		given(bookshelfService.findSummaryBookshelf(5L))
			.willReturn(responses);

		// When // Then
		mockMvc.perform(get("/api/users/{userId}/bookshelves", 5L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.bookshelfName").exists())
			.andExpect(jsonPath("$.bookshelfId").exists())
			.andExpect(jsonPath("$.books[*].bookId").exists())
			.andExpect(jsonPath("$.books[*].title").exists())
			.andExpect(jsonPath("$.books[*].imageUrl").exists())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("userId").description("유저 Id")
				),
				responseFields(
					fieldWithPath("bookshelfName").type(JsonFieldType.STRING).description("책장 이름"),
					fieldWithPath("bookshelfId").type(JsonFieldType.NUMBER).description("책장 ID"),
					fieldWithPath("books[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("books[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("books[].imageUrl").type(JsonFieldType.STRING)
						.description("책 이미지 url")
				)
			));
	}

	@DisplayName("findBooksInBookShelf - 책장속 책들을 조회한다. ")
	@Test
	void findBooksInBookShelf() throws Exception {
		//given
		Long bookShelfId = 3L;
		BooksInBookShelfFindRequest booksInBookShelfFindRequest = new BooksInBookShelfFindRequest(
			BookshelfItemType.READ, 10, 3L, SortDirection.DESC);

		Book book1 = BookObjectProvider.createAllFieldBook();
		ReflectionTestUtils.setField(book1, "id", 1L);
		Book book2 = BookObjectProvider.createAllFieldBook();
		ReflectionTestUtils.setField(book2, "id", 2L);
		Book book3 = BookObjectProvider.createAllFieldBook();
		ReflectionTestUtils.setField(book3, "id", 3L);

		User kakaoUser = UserObjectProvider.createKakaoUser(JobObjectProvider.backendJob());
		ReflectionTestUtils.setField(kakaoUser, "id", 1L);
		Bookshelf createBookShelf = Bookshelf.create(kakaoUser);
		ReflectionTestUtils.setField(createBookShelf, "id", bookShelfId);
		List<BookshelfItem> bookshelfItems = List.of(
			BookshelfItem.create(createBookShelf, book1, BookshelfItemType.WISH),
			BookshelfItem.create(createBookShelf, book2, BookshelfItemType.WISH),
			BookshelfItem.create(createBookShelf, book3, BookshelfItemType.READ));

		SliceImpl<BookshelfItem> bookshelfItemSlice = new SliceImpl<>(bookshelfItems, PageRequest.of(0, 50,
			Sort.by(Sort.Direction.DESC, "id")), false);

		List<BookInShelfResponses.BookInShelfResponse> bookInShelfResponseLists = bookshelfItemSlice.getContent()
			.stream().map(bookshelfItem -> {
				Book book = bookshelfItem.getBook();
				return new BookInShelfResponses.BookInShelfResponse(
					book.getId(),
					book.getTitle(),
					book.getAuthor(),
					book.getIsbn(),
					book.getContents(),
					book.getImageUrl(),
					book.getUrl(),
					book.getPublisher()
				);
			}).toList();

		BookInShelfResponses bookInShelfResponses = new BookInShelfResponses(bookshelfItemSlice,
			bookInShelfResponseLists);

		given(bookshelfService.findAllBooksInShelf(bookShelfId, booksInBookShelfFindRequest))
			.willReturn(bookInShelfResponses);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		params.add("type", booksInBookShelfFindRequest.getType().name());
		params.add("pageSize", booksInBookShelfFindRequest.getPageSize().toString());
		params.add("bookCursorId", Long.toString(3L));
		params.add("sortDirection", SortDirection.DESC.name());

		//when
		mockMvc.perform(get("/api/bookshelves/{bookshelvesId}/books", bookShelfId)
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
				pathParameters(
					parameterWithName("bookshelvesId").description("첵장 Id")
				),
				requestParameters(
					parameterWithName("type").description("책장 아이템 타입. ").optional(),
					parameterWithName("pageSize").description("요청 데이터 수. default : 10").optional()
						.attributes(
							constrainsAttribute(BooksInBookShelfFindRequest.class, "pageSize")
						),
					parameterWithName("bookCursorId").description("커서 book Id. 커서id가 없고 DESC면 가장 최근 데이터.").optional(),
					parameterWithName("sortDirection").description("정렬 순서. default : DESC").optional()
						.description("정렬 방식 : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.SORT_DIRECTION)
						)

				),
				responseFields(
					fieldWithPath("count").description("책 갯수").type(JsonFieldType.NUMBER),
					fieldWithPath("isEmpty").description("데이터가 없으면 empty = true").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isFirst").description("첫 번째 페이지 여부. ").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isLast").description("마지막 페이지 여부.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("hasNext").description("다음 데이터 존재 여부.").type(JsonFieldType.BOOLEAN),

					fieldWithPath("books").description("책장속 책들").type(JsonFieldType.ARRAY),
					fieldWithPath("books[].bookId").type(JsonFieldType.NUMBER).description("책 ID"),
					fieldWithPath("books[].title").type(JsonFieldType.STRING).description("책 제목"),
					fieldWithPath("books[].isbn").description("책 isbn. 고유번호").type(JsonFieldType.STRING),
					fieldWithPath("books[].author").description("책 작가").type(JsonFieldType.STRING),
					fieldWithPath("books[].contents").description("책 설명").type(JsonFieldType.STRING),
					fieldWithPath("books[].imageUrl").type(JsonFieldType.STRING)
						.description("책 이미지 url"),
					fieldWithPath("books[].url").description("책 정보로 이동하는 url").type(JsonFieldType.STRING),
					fieldWithPath("books[].publisher").description("출판사").type(JsonFieldType.STRING)
				)
			))
		;

		//then
	}

	@DisplayName("findBookShelfWithUserJob - 유저와 책장과 직업을 같이 조회해온다.")
	@Test
	void findBookShelfWithUserJob() throws Exception {
		//given

		Long userId = 1L;

		BookShelfDetailResponse bookShelfDetailResponse = new BookShelfDetailResponse(1L, "책장이름", true, userId,
			"username", "userNickname",
			"http://dadok.com/images", JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER, 5);

		given(bookshelfService.findBookShelfWithJob(userId))
			.willReturn(bookShelfDetailResponse);
		//when

		mockMvc.perform(get("/api/bookshelves")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.param("userId", userId.toString())
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				requestParameters(
					parameterWithName("userId").description("조회할 책장의 userId")
						.attributes(
							key("constraints").value("Must not be null")
						)

				),
				responseFields(
					fieldWithPath("bookshelfId").type(JsonFieldType.NUMBER).description("책장 Id"),
					fieldWithPath("bookshelfName").type(JsonFieldType.STRING).description("책장 이름"),
					fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("책장 공개 여부"),

					fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 Id"),
					fieldWithPath("username").type(JsonFieldType.STRING).description("유저 이름"),
					fieldWithPath("userNickname").type(JsonFieldType.STRING).description("유저 닉네임"),
					fieldWithPath("userProfileImage").type(JsonFieldType.STRING).description("유저 프로필 이미지 url"),
					fieldWithPath("job").type(JsonFieldType.OBJECT).description("유저의 직업"),
					fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING)
						.description("직군 한글명"),
					fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING)
						.description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

					fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING)
						.description("직업 한글명"),

					fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
					fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

				)
			));
		//then
		verify(bookshelfService).findBookShelfWithJob(userId);
	}

	@DisplayName("findBookshelfById - 책장 id로 유저와 책장과 직업을 같이 조회해온다.")
	@Test
	void findBookshelfById() throws Exception {
		//given

		Long bookshelfId = 1L;

		BookShelfDetailResponse bookShelfDetailResponse = new BookShelfDetailResponse(1L, "책장이름", true, bookshelfId,
			"username", "userNickname",
			"http://dadok.com/images", JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER, 5);

		given(bookshelfService.findBookShelfById(bookshelfId))
			.willReturn(bookShelfDetailResponse);
		//when

		mockMvc.perform(get("/api/bookshelves/{bookshelfId}", bookshelfId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)

			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookshelfId").description("조회할 책장의 bookshelfId")
						.attributes(
							key("constraints").value("Must not be null")
						)

				),
				responseFields(
					fieldWithPath("bookshelfId").type(JsonFieldType.NUMBER).description("책장 Id"),
					fieldWithPath("bookshelfName").type(JsonFieldType.STRING).description("책장 이름"),
					fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("책장 공개 여부"),

					fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 Id"),
					fieldWithPath("username").type(JsonFieldType.STRING).description("유저 이름"),
					fieldWithPath("userNickname").type(JsonFieldType.STRING).description("유저 닉네임"),
					fieldWithPath("userProfileImage").type(JsonFieldType.STRING).description("유저 프로필 이미지 url"),
					fieldWithPath("job").type(JsonFieldType.OBJECT).description("유저의 직업"),
					fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING)
						.description("직군 한글명"),
					fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING)
						.description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

					fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING)
						.description("직업 한글명"),

					fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
					fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

				)
			));
		//then
		verify(bookshelfService).findBookShelfById(bookshelfId);
	}
}
