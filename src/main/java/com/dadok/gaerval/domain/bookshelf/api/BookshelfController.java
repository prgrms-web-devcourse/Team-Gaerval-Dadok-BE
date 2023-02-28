package com.dadok.gaerval.domain.bookshelf.api;

import static org.springframework.http.MediaType.*;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.PopularBookshelvesOfJobResponses;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.config.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class BookshelfController {

	private final BookshelfService bookshelfService;

	/**
	 * <pre>
	 *     직군을 입력받아 해당 직군 관련 인기 책장을 조회한다.
	 * </pre>
	 *
	 * @param jobGroup
	 * @return status : ok , PopularBookshelvesOfJobResponses : 책장과 책장의 일부 책 list
	 */
	@GetMapping(value = "/suggestions/bookshelves",
		consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<PopularBookshelvesOfJobResponses> findBookshelvesByJobGroup(
		@RequestParam(name = "job_group") String jobGroup,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		User user = userPrincipal.getUserEntity();
		PopularBookshelvesOfJobResponses responses =
			bookshelfService.findPopularBookshelvesByJob(user, jobGroup);
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
		User user = userPrincipal.getUserEntity();
		bookshelfService.insertBookSelfItem(user, bookshelvesId, bookCreateRequest);
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
		User user = userPrincipal.getUserEntity();
		bookshelfService.removeBookSelfItem(user, bookshelvesId, bookId);
		return ResponseEntity.ok().build();
	}

}
