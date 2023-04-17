package com.dadok.gaerval.domain.bookshelf.repository;

public interface BookshelfLikeSupport {

	boolean existsLike(Long bookshelfId, Long userId);
}
