package com.dadok.gaerval.domain.bookshelf.service;

public interface BookshelfLikeService {

	void createBookshelfLike(Long userId, Long bookshelfId);

	void deleteBookshelfLike(Long userId, Long bookshelfId);
}
