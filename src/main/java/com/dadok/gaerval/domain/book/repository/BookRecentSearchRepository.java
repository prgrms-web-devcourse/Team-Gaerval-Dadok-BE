package com.dadok.gaerval.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dadok.gaerval.domain.book.entity.BookRecentSearch;

public interface BookRecentSearchRepository extends JpaRepository<BookRecentSearch, Long>, BookRecentSearchSupport {
}
