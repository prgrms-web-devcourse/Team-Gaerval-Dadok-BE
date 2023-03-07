package com.dadok.gaerval.domain.bookshelf.api;

import static org.springframework.http.MediaType.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookInShelfResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfSummaryResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.SuggestionBookshelvesByJobGroupResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.SuggestionBookshelvesResponses;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.global.config.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class BookshelfController {

	private final BookshelfService bookshelfService;

	/**
	 * <Pre>
	 * 미로그인 사용자 접근용 인기 책장을 조회한다.
	 * </Pre>
	 *
	 * @return status : ok , SuggestionBookshelvesResponses : 책장 5개와 책장의 일부 책 list
	 */
	@GetMapping(value = "/suggestions/bookshelves/default",
		consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ANONYMOUS','ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<SuggestionBookshelvesResponses> findSuggestionBookshelves(
	) {
		SuggestionBookshelvesResponses responses =
			bookshelfService.findSuggestionBookshelves();
		return ResponseEntity.ok().body(responses);
	}

	/**
	 * <pre>
	 *     직군을 입력받아 해당 직군 관련 인기 책장을 조회한다.
	 * </pre>
	 *
	 * @param jobGroup
	 * @return status : ok , SuggestionBookshelvesByJobGroupResponses : 책장 5개와 책장의 일부 책 list
	 */
	@GetMapping(value = "/suggestions/bookshelves",
		consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<SuggestionBookshelvesByJobGroupResponses> findSuggestionBookshelvesByJobGroup(
		@RequestParam(name = "job_group") @NotBlank String jobGroup,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		Long userId = userPrincipal.getUserId();
		SuggestionBookshelvesByJobGroupResponses responses =
			bookshelfService.findSuggestionBookshelvesByJobGroup(userId, jobGroup);
		return ResponseEntity.ok().body(responses);
	}

	/**
	 * <pre>
	 *     자신의 책장 요약 데이터를 조회한다.
	 * </pre>
	 *
	 * @return status : ok , BookShelfSummaryResponse : 책장과 책장의 일부 책 list
	 */
	@GetMapping(value = "/bookshelves/me",
		consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<BookShelfSummaryResponse> findMySummaryBookshelf(
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		Long userId = userPrincipal.getUserId();
		BookShelfSummaryResponse responses =
			bookshelfService.findSummaryBookshelf(userId);
		return ResponseEntity.ok().body(responses);
	}

	/**
	 * <pre>
	 *     사용자의 책장 요약 데이터를 조회한다.
	 * </pre>
	 *
	 * @return status : ok , BookShelfSummaryResponse : 책장과 책장의 일부 책 list
	 */
	@GetMapping(value = "/users/{userId}/bookshelves",
		consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<BookShelfSummaryResponse> findSummaryBookshelfByUserId(@PathVariable("userId") Long userId) {
		BookShelfSummaryResponse responses =
			bookshelfService.findSummaryBookshelf(userId);
		return ResponseEntity.ok().body(responses);
	}

	/**
	 * <pre>
	 *     해당 책장에 입력받은 책을 추가한다.
	 * </pre>
	 *
	 * @param bookshelvesId     책장 id
	 * @param bookCreateRequest 책 정보
	 * @return status : ok
	 */
	@PostMapping(value = "/bookshelves/{bookshelvesId}/books",
		consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<Void> insertBookInBookshelf(@PathVariable("bookshelvesId") Long bookshelvesId,
		@RequestBody @Valid BookCreateRequest bookCreateRequest,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
		Long userId = userPrincipal.getUserId();
		bookshelfService.insertBookSelfItem(userId, bookshelvesId, bookCreateRequest);
		return ResponseEntity.ok().build();
	}

	/**
	 * <pre>
	 *     해당 책장에 입력받은 책을 제거한다.
	 * </pre>
	 *
	 * @param bookshelvesId 책장 id
	 * @param bookId        책 id
	 * @return status : ok
	 */
	@DeleteMapping(value = "/bookshelves/{bookshelvesId}/books/{bookId}",
		consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<Void> removeBookFormBookshelf(
		@PathVariable("bookshelvesId") Long bookshelvesId, @PathVariable("bookId") Long bookId,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
		Long userId = userPrincipal.getUserId();
		bookshelfService.removeBookSelfItem(userId, bookshelvesId, bookId);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/bookshelves/{bookshelvesId}/books",
		consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<BookInShelfResponses> findBooksInBookShelf(@PathVariable Long bookshelvesId,
		@ModelAttribute @Valid BooksInBookShelfFindRequest request
	) {
		BookInShelfResponses bookInShelfResponses = bookshelfService.findAllBooksInShelf(bookshelvesId, request);

		return ResponseEntity.ok(bookInShelfResponses);
	}

	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping(value = "/bookshelves", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<BookShelfDetailResponse> findBookShelfWithUserJob(@RequestParam @Valid @NotNull Long userId) {

		BookShelfDetailResponse bookShelf = bookshelfService.findBookShelfWithJob(userId);

		return ResponseEntity.ok(bookShelf);
	}
}
