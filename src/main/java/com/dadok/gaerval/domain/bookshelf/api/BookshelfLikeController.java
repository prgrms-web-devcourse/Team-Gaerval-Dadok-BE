package com.dadok.gaerval.domain.bookshelf.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.bookshelf.service.BookshelfLikeService;
import com.dadok.gaerval.global.config.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/bookshelf/{bookshelfId}/like")
@RestController
@RequiredArgsConstructor
public class BookshelfLikeController {

	private final BookshelfLikeService bookshelfLikeService;

	/***
	 * <Pre>
	 *     책장 좋아요 추가
	 * </Pre>
	 * @param bookshelfId
	 * @param userPrincipal
	 * @return status : ok
	 */
	@PostMapping()
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<Void> createLike(@PathVariable Long bookshelfId,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
		bookshelfLikeService.createBookshelfLike(userPrincipal.getUserId(), bookshelfId);
		return ResponseEntity.ok().build();
	}

	/***
	 * <Pre>
	 *     책장 좋아요 해제
	 * </Pre>
	 * @param bookshelfId
	 * @param userPrincipal
	 * @return status : ok
	 */
	@DeleteMapping()
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<Void> deleteLike(@PathVariable Long bookshelfId,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
		bookshelfLikeService.deleteBookshelfLike(userPrincipal.getUserId(), bookshelfId);
		return ResponseEntity.ok().build();
	}
}
