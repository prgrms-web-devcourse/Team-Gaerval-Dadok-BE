package com.dadok.gaerval.domain.bookshelf.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.bookshelf.service.BookshelfLikeService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/bookshelf")
@RestController
@RequiredArgsConstructor
public class BookshelfLikeController {

	private final BookshelfLikeService bookshelfLikeService;

	@PutMapping
	public ResponseEntity<Void> createLike() {
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteLike() {
		return ResponseEntity.ok().build();
	}
}
