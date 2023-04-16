package com.dadok.gaerval.domain.bookshelf.repository;

public interface BookshelfLikeSupport {
	boolean existsByBookshelfIdAndUserId(Long bookshelfId, Long userId);
}
