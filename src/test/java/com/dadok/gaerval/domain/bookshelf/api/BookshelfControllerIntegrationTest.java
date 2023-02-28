package com.dadok.gaerval.domain.bookshelf.api;

import static com.dadok.gaerval.controller.ControllerTest.*;
import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfRepository;
import com.dadok.gaerval.domain.bookshelf.service.DefaultBookshelfService;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.repository.AuthorityRepository;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.domain.user.service.DefaultUserService;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@WithMockCustomOAuth2LoginUser
class BookshelfControllerIntegrationTest {

	private final MockMvc mockMvc;

	private final BookshelfRepository bookshelfRepository;

	private final BookshelfItemRepository bookshelfItemRepository;

	private final DefaultBookshelfService bookshelfService;

	private final DefaultUserService userService;

	private final UserRepository userRepository;

	private final AuthorityRepository authorityRepository;

	@MockBean
	private final BookService bookService;

	private User user;

	private Bookshelf bookshelf;

	private List<Book> books;

	@DisplayName("findPopularBookshelvesByJobGroup - 직군에 따른 인기 책장 조회 - 성공")
	@Test
	void findBookshelvesByJobGroup_success() throws Exception {
		// Given
		String jobGroup = JobGroup.DEVELOPMENT.getDescription();

		// When// Then

	}

	@DisplayName("findPopularBookshelvesByJobGroup - 직군의 책장이 없을 경우 빈 리스트 반환 - 성공")
	@Test
	void findBookshelvesByJobGroup_notExistBookshelf_success() {

	}

	@DisplayName("findPopularBookshelvesByJobGroup - 존재 하지 않은 직군의 책장 조회 - 실패")
	@ParameterizedTest
	@ValueSource(strings = {"개 발 ", "백수", "노 직군", "영지", "다독"})
	void findBookshelvesByJobGroup_notExistJobGroup_fail(String jobGroup) throws Exception {

		// Given
		// When// Then
		mockMvc.perform(get("/api/suggestions/bookshelves")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.param("job_group", jobGroup)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isBadRequest())
			.andDo(print());
	}

	@DisplayName("insertBookInBookshelf - db에 존재하지 않는 책 책장에 책 추가 - 성공")
	@Test
	void insertBookInBookshelf_bookSave_success() {

	}

	@DisplayName("insertBookInBookshelf - db에 존재하지 책 책장에 책 추가 - 성공")
	@Test
	void insertBookInBookshelf_bookExist_success() {

	}

	@DisplayName("insertBookInBookshelf - 잘못된 요청으로 책장에 책 추가 - 실패")
	@Test
	void insertBookInBookshelf_validationBook_fail() {

	}

	@DisplayName("insertBookInBookshelf - 자신의 책장이 아닌 책장에 책 추가 - 실패")
	@Test
	void insertBookInBookshelf_validationBookshelf_fail() throws Exception {

	}

	@DisplayName("removeBookFormBookshelf - 책 책장에 책 제거 - 성공")
	@Test
	void removeBookFormBookshelf_success() {
		// Given
		Book book = BookObjectProvider.createRequiredFieldBook();

		// When

		// Then
	}

	@DisplayName("removeBookFormBookshelf - 책장에 있지 않은 책 제거 - 실패")
	@Test
	void removeBookFormBookshelf_bookItemExist_fail() {

	}

	@DisplayName("removeBookFormBookshelf - 자신의 책장이 아닌 책장에 책 제거 - 실패")
	@Test
	void removeBookFormBookshelf_validationBookshelf_fail() throws Exception {
		// Given
		createUserAndBookshelf();

		// When// Then
		mockMvc.perform(delete("/api/bookshelves/{bookshelvesId}/books/{bookId}", bookshelf.getId(), 2L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isUnauthorized())
			.andDo(print());
	}

	@DisplayName("findSummaryBookshelf - 자신의 빈 책장 요약 조회 - 성공")
	@Test
	void findMySummaryBookshelf_userEmptyBookList_success() {

	}

	@DisplayName("findSummaryBookshelf - 잘못된 userId 책장 요약 조회 - 실패")
	@Test
	void findSummaryBookshelfByUserId_userIdNotExist_fail() {

	}

	@DisplayName("findSummaryBookshelf - 사용자의 책장이 없는 경우 조회 - 실패")
	@Test
	void findSummaryBookshelfByUserId_userShelfNotExist_fail() {

	}

	void createUserAndBookshelf() {
		user = UserObjectProvider.createKakaoUser();
		bookshelf = Bookshelf.create(user);
		bookshelfRepository.save(bookshelf);

		System.out.println(user.getId());
		System.out.println(bookshelf.getId());

	}
}