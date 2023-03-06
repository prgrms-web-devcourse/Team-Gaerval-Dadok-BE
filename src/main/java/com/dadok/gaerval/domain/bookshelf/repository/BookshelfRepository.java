package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long>, BookshelfSupport {

	Optional<Bookshelf> findByUserId(@Param("userId") Long userId);

}
