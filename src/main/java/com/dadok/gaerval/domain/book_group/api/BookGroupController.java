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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupDetailResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.service.BookGroupService;
import com.dadok.gaerval.global.config.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/book-groups")
@RequiredArgsConstructor
public class BookGroupController {

	private final BookGroupService bookGroupService;

	@PreAuthorize(value = "hasAnyRole('ROLE_ANONYMOUS', 'ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping
	public ResponseEntity<BookGroupResponses> findBookGroups(
		@ModelAttribute @Valid BookGroupSearchRequest request
	) {
		return ResponseEntity.ok(bookGroupService.findAllBookGroups(request));
	}

	/**
	 * <Pre>
	 * 모임 생성
	 * </Pre>
	 *
	 * @param request       모임 생성 데이터
	 * @param userPrincipal 모임 생성 유저
	 * @return status : created - 생성된 모임 조회 페이지로 리다이렉트
	 */
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createBookGroup(
		@RequestBody @Valid BookGroupCreateRequest request,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		Long bookGroupId = bookGroupService.createBookGroup(userPrincipal.getUserId(), request);
		String redirectUri =
			ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + "/" + bookGroupId.toString();
		return ResponseEntity.created(URI.create(redirectUri)).build();
	}

	@PreAuthorize(value = "hasAnyRole('ROLE_ANONYMOUS', 'ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/{groupId}")
	public ResponseEntity<BookGroupDetailResponse> findBookGroup(
		@PathVariable Long groupId,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
		return ResponseEntity.ok(bookGroupService.findGroup(userPrincipal.getUserId(), groupId));
	}

	/**
	 * <Pre>
	 * 내가 참여한 모임 리스트 조회
	 * </Pre>
	 *
	 * @param request       page 관련 파라미터
	 * @param userPrincipal 해당 유저의 참여 모임 조회
	 * @return status : ok, BookGroupResponses 조회된 모임 list
	 */
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping(value = "/me", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<BookGroupResponses> findMyBookGroups(
		@ModelAttribute @Valid BookGroupSearchRequest request,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		return ResponseEntity.ok().body(bookGroupService.findAllBookGroupsByUser(request, userPrincipal.getUserId()));
	}

	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@DeleteMapping(value = "/{groupId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteBookGroup(
		@PathVariable("groupId") Long groupId,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		bookGroupService.deleteBookGroup(groupId, userPrincipal.getUserId());
		return ResponseEntity.ok().build();
	}
}
