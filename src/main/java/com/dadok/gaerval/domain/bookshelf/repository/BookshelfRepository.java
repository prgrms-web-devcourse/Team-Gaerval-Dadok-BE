package com.dadok.gaerval.domain.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {
}
