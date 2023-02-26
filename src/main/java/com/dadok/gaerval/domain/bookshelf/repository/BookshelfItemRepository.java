package com.dadok.gaerval.domain.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;

public interface BookshelfItemRepository extends JpaRepository<BookshelfItem, Long> {

}
