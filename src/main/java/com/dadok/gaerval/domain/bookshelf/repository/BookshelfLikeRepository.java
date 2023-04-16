package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dadok.gaerval.domain.bookshelf.entity.BookshelfLike;

public interface BookshelfLikeRepository extends JpaRepository<BookshelfLike, Long> {

	Optional<BookshelfLike> findByUserIdAndBookshelfId(Long userId, Long bookshelfId);
}
