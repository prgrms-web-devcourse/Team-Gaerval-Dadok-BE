package com.dadok.gaerval.domain.book_group.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.service.BookGroupService;

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

}
