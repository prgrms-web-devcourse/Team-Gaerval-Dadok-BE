package com.dadok.gaerval.domain.bookshelf.api;

import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;
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

import com.dadok.gaerval.controller.ControllerSliceTest;
import com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.dto.request.BookshelfItemCreateRequest;
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
class BookshelfControllerSliceTest extends ControllerSliceTest {

	@MockBean
	private BookshelfService bookshelfService;

	@DisplayName("findSuggestionBookshelvesByJobGroup - ????????? ???????????? ?????? ????????? ?????? - ??????")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findSuggestionBookshelvesByJobGroup() throws Exception {
		// Given
		var jobGroup = JobGroup.DEVELOPMENT;
		SuggestionBookshelvesByJobGroupResponses responses = new SuggestionBookshelvesByJobGroupResponses(
			jobGroup, jobGroup.getGroupName(), List.of(new BookShelfSummaryResponse(23L, "???????????? ??????",
				List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "????????????1",
						"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"),
					new BookShelfSummaryResponse.BookSummaryResponse(2L, "????????????2",
						"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
			),
			new BookShelfSummaryResponse(13L, "???????????? ??????",
				List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "????????????1",
						"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"),
					new BookShelfSummaryResponse.BookSummaryResponse(2L, "????????????2",
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
				.param("job_group", jobGroup.getName())
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.jobGroup").value(jobGroup.getName()))
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
					)
				),
				responseFields(
					fieldWithPath("jobGroup").type(JsonFieldType.STRING).description("??????"),
					fieldWithPath("JobGroupKoreanName").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
					fieldWithPath("bookshelfResponses[].bookshelfName").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("bookshelfResponses[].bookshelfId").type(JsonFieldType.NUMBER).description("?????? ID"),
					fieldWithPath("bookshelfResponses[].books[].bookId").type(JsonFieldType.NUMBER).description("??? ID"),
					fieldWithPath("bookshelfResponses[].books[].title").type(JsonFieldType.STRING).description("??? ??????"),
					fieldWithPath("bookshelfResponses[].books[].imageUrl").type(JsonFieldType.STRING)
						.description("??? ????????? url")
				)
			));
	}

	@DisplayName("findSuggestionBookshelves - ???????????? ????????? ????????? ???????????? ?????? ????????? ?????? - ??????")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findSuggestionBookshelves() throws Exception {
		// Given
		String jobGroup = JobGroup.DEVELOPMENT.getDescription();
		var responses = new SuggestionBookshelvesResponses(
			List.of(new BookShelfSummaryResponse(23L, "???????????? ??????",
					List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "????????????1",
							"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"),
						new BookShelfSummaryResponse.BookSummaryResponse(2L, "????????????2",
							"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
				),
				new BookShelfSummaryResponse(13L, "???????????? ??????",
					List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "????????????1",
							"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"),
						new BookShelfSummaryResponse.BookSummaryResponse(2L, "????????????2",
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
						.description("?????? ID - (???????????? ???????????? ?????? api??? ????????? ????????? ????????? ????????? ????????? ????????? ????????? ??? ????????????.)"),
					fieldWithPath("bookshelfResponses[].bookshelfName").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("bookshelfResponses[].books[].bookId").type(JsonFieldType.NUMBER).description("??? ID"),
					fieldWithPath("bookshelfResponses[].books[].title").type(JsonFieldType.STRING).description("??? ??????"),
					fieldWithPath("bookshelfResponses[].books[].imageUrl").type(JsonFieldType.STRING)
						.description("??? ????????? url")
				)
			));
	}

	@DisplayName("insertBookInBookshelf - ????????? ??? ?????? - ??????")
	@Test
	@WithMockCustomOAuth2LoginUser
	void insertBookInBookshelf() throws Exception {
		// Given
		BookshelfItemCreateRequest request = new BookshelfItemCreateRequest(55L);

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
					parameterWithName("bookshelvesId").description("?????? Id")
				),
				requestFields(
					fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("?????? id")
						.attributes(
							constrainsAttribute(BookshelfItemCreateRequest.class, "bookId")
						)
				)
			));
	}

	@SneakyThrows
	@DisplayName("removeBookFormBookshelf - ????????? ??? ?????? - ??????")
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
					parameterWithName("bookshelvesId").description("?????? Id"),
					parameterWithName("bookId").description("??? Id")
				)
			));
	}

	@DisplayName("findSummaryBookshelf - ????????? ?????? ?????? ?????? - ??????")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findMySummaryBookshelf() throws Exception {
		// Given
		var responses = new BookShelfSummaryResponse(23L, "???????????? ??????",
			List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "????????????",
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
					fieldWithPath("bookshelfName").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("bookshelfId").type(JsonFieldType.NUMBER).description("?????? ID"),
					fieldWithPath("books[].bookId").type(JsonFieldType.NUMBER).description("??? ID"),
					fieldWithPath("books[].title").type(JsonFieldType.STRING).description("??? ??????"),
					fieldWithPath("books[].imageUrl").type(JsonFieldType.STRING)
						.description("??? ????????? url")
				)
			));
	}

	@DisplayName("findSummaryBookshelf - ???????????? ?????? ?????? ?????? - ??????")
	@Test
	@WithMockCustomOAuth2LoginUser
	void findSummaryBookshelfByUserId() throws Exception {
		// Given
		var responses = new BookShelfSummaryResponse(23L, "???????????? ??????",
			List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "????????????1",
					"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"),
				new BookShelfSummaryResponse.BookSummaryResponse(2L, "????????????2",
					"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
		);

		given(bookshelfService.findSummaryBookshelf(5L))
			.willReturn(responses);

		// When // Then
		mockMvc.perform(get("/api/users/{userId}/bookshelves", 5L)
				.contentType(MediaType.APPLICATION_JSON)
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
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("userId").description("?????? Id")
				),
				responseFields(
					fieldWithPath("bookshelfName").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("bookshelfId").type(JsonFieldType.NUMBER).description("?????? ID"),
					fieldWithPath("books[].bookId").type(JsonFieldType.NUMBER).description("??? ID"),
					fieldWithPath("books[].title").type(JsonFieldType.STRING).description("??? ??????"),
					fieldWithPath("books[].imageUrl").type(JsonFieldType.STRING)
						.description("??? ????????? url")
				)
			));
	}

	@DisplayName("findBooksInBookShelf - ????????? ????????? ????????????. ")
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
		BookshelfItem bs1 = BookshelfItem.create(createBookShelf, book1, BookshelfItemType.WISH);
		ReflectionTestUtils.setField(bs1, "id", 1L);
		BookshelfItem bs2 = BookshelfItem.create(createBookShelf, book2, BookshelfItemType.WISH);
		ReflectionTestUtils.setField(bs2, "id", 2L);
		BookshelfItem bs3 = BookshelfItem.create(createBookShelf, book3, BookshelfItemType.WISH);
		ReflectionTestUtils.setField(bs3, "id", 3L);
		List<BookshelfItem> bookshelfItems = List.of(bs1, bs2, bs3);

		SliceImpl<BookshelfItem> bookshelfItemSlice = new SliceImpl<>(bookshelfItems, PageRequest.of(0, 50,
			Sort.by(Sort.Direction.DESC, "id")), false);

		List<BookInShelfResponses.BookInShelfResponse> bookInShelfResponseLists = bookshelfItemSlice.getContent()
			.stream().map(bookshelfItem -> {
				Book book = bookshelfItem.getBook();
				return new BookInShelfResponses.BookInShelfResponse(
					bookshelfItem.getId(),
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
		params.add("bookshelfItemCursorId", Long.toString(3L));
		params.add("sortDirection", SortDirection.DESC.name());

		//when
		mockMvc.perform(get("/api/bookshelves/{bookshelvesId}/books", bookShelfId)
				.contentType(MediaType.APPLICATION_JSON)
				.params(params)
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookshelvesId").description("?????? Id")
				),
				requestParameters(
					parameterWithName("type").description("?????? ????????? ??????. ").optional(),
					parameterWithName("pageSize").description("?????? ????????? ???. default : 10").optional()
						.attributes(
							constrainsAttribute(BooksInBookShelfFindRequest.class, "pageSize")
						),
					parameterWithName("bookshelfItemCursorId").description(
						"?????? bookshelfItem Id. ??????id??? ?????? DESC??? ?????? ?????? ?????????.").optional(),
					parameterWithName("sortDirection").description("?????? ??????. default : DESC").optional()
						.description("?????? ?????? : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.SORT_DIRECTION)
						)

				),
				responseFields(
					fieldWithPath("count").description("??? ??????").type(JsonFieldType.NUMBER),
					fieldWithPath("isEmpty").description("???????????? ????????? empty = true").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isFirst").description("??? ?????? ????????? ??????. ").type(JsonFieldType.BOOLEAN),
					fieldWithPath("isLast").description("????????? ????????? ??????.").type(JsonFieldType.BOOLEAN),
					fieldWithPath("hasNext").description("?????? ????????? ?????? ??????.").type(JsonFieldType.BOOLEAN),

					fieldWithPath("books").description("????????? ??????").type(JsonFieldType.ARRAY),
					fieldWithPath("books[].bookshelfItemId").type(JsonFieldType.NUMBER).description("?????? ??? ??? ????????? ID"),
					fieldWithPath("books[].bookId").type(JsonFieldType.NUMBER).description("??? ID"),
					fieldWithPath("books[].title").type(JsonFieldType.STRING).description("??? ??????"),
					fieldWithPath("books[].isbn").description("??? isbn. ????????????").type(JsonFieldType.STRING),
					fieldWithPath("books[].author").description("??? ??????").type(JsonFieldType.STRING),
					fieldWithPath("books[].contents").description("??? ??????").type(JsonFieldType.STRING),
					fieldWithPath("books[].imageUrl").type(JsonFieldType.STRING)
						.description("??? ????????? url"),
					fieldWithPath("books[].url").description("??? ????????? ???????????? url").type(JsonFieldType.STRING),
					fieldWithPath("books[].publisher").description("?????????").type(JsonFieldType.STRING)
				)
			))
		;

		//then
	}

	@DisplayName("findBookShelfWithUserJob - ????????? ????????? ????????? ?????? ???????????????.")
	@Test
	void findBookShelfWithUserJob() throws Exception {
		//given

		Long userId = 1L;

		BookShelfDetailResponse bookShelfDetailResponse = new BookShelfDetailResponse(1L, "????????????", true, userId,
			"username", "userNickname",
			"http://dadok.com/images", JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER, 5);

		given(bookshelfService.findBookShelfWithJob(userId))
			.willReturn(bookShelfDetailResponse);
		//when

		mockMvc.perform(get("/api/bookshelves")
				.contentType(MediaType.APPLICATION_JSON)
				.param("userId", userId.toString())
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				requestParameters(
					parameterWithName("userId").description("????????? ????????? userId")
						.attributes(
							key("constraints").value("Must not be null")
						)

				),
				responseFields(
					fieldWithPath("bookshelfId").type(JsonFieldType.NUMBER).description("?????? Id"),
					fieldWithPath("bookshelfName").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("?????? ?????? ??????"),

					fieldWithPath("userId").type(JsonFieldType.NUMBER).description("?????? Id"),
					fieldWithPath("username").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("userNickname").type(JsonFieldType.STRING).description("?????? ?????????"),
					fieldWithPath("userProfileImage").type(JsonFieldType.STRING).description("?????? ????????? ????????? url"),
					fieldWithPath("job").type(JsonFieldType.OBJECT).description("????????? ??????"),
					fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING)
						.description("?????? ?????????"),
					fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING)
						.description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

					fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING)
						.description("?????? ?????????"),

					fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("?????? ????????? :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
					fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("?????? ?????? ??????")

				)
			));
		//then
		verify(bookshelfService).findBookShelfWithJob(userId);
	}

	@DisplayName("findBookshelfById - ?????? id??? ????????? ????????? ????????? ?????? ???????????????.")
	@Test
	void findBookshelfById() throws Exception {
		//given

		Long bookshelfId = 1L;

		BookShelfDetailResponse bookShelfDetailResponse = new BookShelfDetailResponse(1L, "????????????", true, bookshelfId,
			"username", "userNickname",
			"http://dadok.com/images", JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER, 5);

		given(bookshelfService.findBookShelfById(bookshelfId))
			.willReturn(bookShelfDetailResponse);
		//when

		mockMvc.perform(get("/api/bookshelves/{bookshelfId}", bookshelfId)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookshelfId").description("????????? ????????? bookshelfId")
						.attributes(
							key("constraints").value("Must not be null")
						)

				),
				responseFields(
					fieldWithPath("bookshelfId").type(JsonFieldType.NUMBER).description("?????? Id"),
					fieldWithPath("bookshelfName").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("?????? ?????? ??????"),

					fieldWithPath("userId").type(JsonFieldType.NUMBER).description("?????? Id"),
					fieldWithPath("username").type(JsonFieldType.STRING).description("?????? ??????"),
					fieldWithPath("userNickname").type(JsonFieldType.STRING).description("?????? ?????????"),
					fieldWithPath("userProfileImage").type(JsonFieldType.STRING).description("?????? ????????? ????????? url"),
					fieldWithPath("job").type(JsonFieldType.OBJECT).description("????????? ??????"),
					fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING)
						.description("?????? ?????????"),
					fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING)
						.description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

					fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING)
						.description("?????? ?????????"),

					fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("?????? ????????? :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
					fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("?????? ?????? ??????")

				)
			));
		//then
		verify(bookshelfService).findBookShelfById(bookshelfId);
	}
}
