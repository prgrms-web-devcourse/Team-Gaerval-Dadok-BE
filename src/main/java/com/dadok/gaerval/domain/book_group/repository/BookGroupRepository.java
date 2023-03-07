package com.dadok.gaerval.domain.book_group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dadok.gaerval.domain.book_group.entity.BookGroup;

public interface BookGroupRepository extends JpaRepository<BookGroup, Long>, BookGroupSupport {
}
