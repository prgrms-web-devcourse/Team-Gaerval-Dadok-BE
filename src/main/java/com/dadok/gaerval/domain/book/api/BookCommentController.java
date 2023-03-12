package com.dadok.gaerval.domain.book.api;

import static org.springframework.http.MediaType.*;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dadok.gaerval.domain.book.dto.request.BookCommentCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentUpdateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentIdResponse;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponse;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponses;
import com.dadok.gaerval.domain.book.service.BookCommentService;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentDeleteRequest;
import com.dadok.gaerval.global.config.security.CurrentUserPrincipal;
import com.dadok.gaerval.global.config.security.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookCommentController {

	private final BookCommentService bookCommentService;

	/**
	 * <pre>
	 *     도서 ID를 통해 코멘트 목록을 가져온다.
	 * </pre>
	 *
	 * @param bookId 검색어
	 * @return status : ok, BookCommentResponses : 코멘트 목록
	 */
	@GetMapping(value = "/{bookId}/comments", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_ANONYMOUS')")
	public ResponseEntity<BookCommentResponses> findBookComments(
		@PathVariable(name = "bookId") Long bookId,
		@CurrentUserPrincipal UserPrincipal userPrincipal,
		@ModelAttribute @Valid BookCommentSearchRequest bookCommentSearchRequest
	) {
		log.info("[BookCommentController]-[BookCommentResponses]-bookId : {}", bookId);
		return ResponseEntity.ok()
			.body(bookCommentService.findBookCommentsBy(bookId, userPrincipal.getUserId(), bookCommentSearchRequest));
	}

	/**
	 * <pre>
	 *     저장 요청을 통해 도서 코멘트를 저장한다.
	 * </pre>
	 *
	 * @param bookCommentCreateRequest 코멘트 정보
	 * @return status : ok
	 */
	@PostMapping(value = "/{bookId}/comments", consumes = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<BookCommentIdResponse> saveBookComment(
		@PathVariable Long bookId,
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody BookCommentCreateRequest bookCommentCreateRequest) {
		log.info("[BookCommentController]-[BookCommentIdResponse]-bookCommentCreateRequest : {}",
			bookCommentCreateRequest);
		Long commentId = bookCommentService.createBookComment(bookId, userPrincipal.getUserId(),
			bookCommentCreateRequest);
		String redirectUri =
			ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + "/" + commentId.toString();
		return ResponseEntity.created(URI.create(redirectUri)).body(new BookCommentIdResponse(commentId));
	}

	/**
	 * <pre>
	 *     수정 요청을 통해 도서 코멘트를 수정한다.
	 * </pre>
	 *
	 * @param bookCommentUpdateRequest 코멘트 정보
	 * @return status : ok, BookCommentResponse : 코멘트 정보
	 */
	@PatchMapping(value = "/{bookId}/comments", consumes = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<BookCommentResponse> modifyBookComment(
		@PathVariable Long bookId,
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody BookCommentUpdateRequest bookCommentUpdateRequest) {
		log.info("[BookController]-[BookCommentResponse]-bookCommentUpdateRequest : {}", bookCommentUpdateRequest);
		BookCommentResponse bookCommentResponse = bookCommentService.updateBookComment(bookId,
			userPrincipal.getUserId(), bookCommentUpdateRequest);
		return ResponseEntity.ok().body(bookCommentResponse);
	}

	/**
	 * <pre>
	 *     수정 요청을 통해 도서 코멘트를 삭제한다.
	 * </pre>
	 *
	 * @param bookGroupCommentDeleteRequest 코멘트 아이디
	 * @return status : ok, BookCommentResponse : 코멘트 정보
	 */
	@DeleteMapping(value = "/{bookId}/comments", consumes = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<Void> deleteBookComment(
		@PathVariable Long bookId,
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody BookGroupCommentDeleteRequest bookGroupCommentDeleteRequest) {
		log.info("[BookController]-[BookCommentResponse]-bookGroupCommentDeleteRequest : {}",
			bookGroupCommentDeleteRequest);
		bookCommentService.deleteBookComment(bookId, userPrincipal.getUserId(), bookGroupCommentDeleteRequest);
		return ResponseEntity.ok().build();
	}
}
