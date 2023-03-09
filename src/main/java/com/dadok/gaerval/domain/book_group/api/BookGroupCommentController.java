package com.dadok.gaerval.domain.book_group.api;

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

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentDeleteRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentUpdateRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponses;
import com.dadok.gaerval.domain.book_group.service.BookGroupCommentService;
import com.dadok.gaerval.global.config.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/book-groups")
@RequiredArgsConstructor
public class BookGroupCommentController {

	private final BookGroupCommentService bookGroupCommentService;

	/**
	 * <Pre>
	 * 모임 코멘트 생성
	 * </Pre>
	 *
	 * @param groupId       모임 아이디
	 * @param userPrincipal 모임 유저
	 * @return status : created - 생성된 모임 댓글로 리다이렉트
	 */
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@PostMapping(value = "/{groupId}/comment", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createBookGroupComment(
		@PathVariable Long groupId,
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody @Valid BookGroupCommentCreateRequest bookGroupCommentCreateRequest
	) {
		Long bookGroupCommentId = bookGroupCommentService.createBookGroupComment(groupId, userPrincipal.getUserId(),
			bookGroupCommentCreateRequest);
		String redirectUri =
			ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + "/" + bookGroupCommentId.toString();
		return ResponseEntity.created(URI.create(redirectUri)).build();
	}

	/**
	 * <Pre>
	 * 모임의 댓글 조회
	 * </Pre>
	 *
	 * @param groupId   모임 아이디
	 * @return status : ok, BookGroupCommentResponses 조회된 모임 댓글 list
	 */
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping(value = "/{groupId}/comments", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<BookGroupCommentResponses> findBookGroupsComment(
		@PathVariable Long groupId,
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@ModelAttribute @Valid BookGroupCommentSearchRequest bookGroupCommentSearchRequest
	) {
		return ResponseEntity.ok().body(bookGroupCommentService.findAllBookGroupCommentsByGroup(bookGroupCommentSearchRequest,userPrincipal.getUserId(), groupId));
	}


	/**
	 * <Pre>
	 * 모임 코멘트 수정
	 * </Pre>
	 *
	 * @param groupId       모임 아이디
	 * @param userPrincipal 모임 유저
	 * @return status : ok, BookGroupCommentResponse : 수정된 댓글 정보
	 */
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@PatchMapping(value = "/{groupId}/comment", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<BookGroupCommentResponse> updateBookGroupComment(
		@PathVariable Long groupId,
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody @Valid BookGroupCommentUpdateRequest bookGroupCommentUpdateRequest
	) {
		BookGroupCommentResponse bookGroupComment = bookGroupCommentService.updateBookGroupComment(groupId, userPrincipal.getUserId(),
			bookGroupCommentUpdateRequest);

		return ResponseEntity.ok().body(bookGroupComment);
	}


	/**
	 * <Pre>
	 * 모임 코멘트 삭제
	 * </Pre>
	 *
	 * @param groupId       모임 아이디
	 * @param userPrincipal 모임 유저
	 * @return status : ok
	 */
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@DeleteMapping(value = "/{groupId}/comment", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateBookGroupComment(
		@PathVariable Long groupId,
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestBody @Valid BookGroupCommentDeleteRequest bookGroupCommentDeleteRequest
	) {
		bookGroupCommentService.deleteBookGroupComment(groupId, userPrincipal.getUserId(),
			bookGroupCommentDeleteRequest);

		return ResponseEntity.ok().build();
	}
}
