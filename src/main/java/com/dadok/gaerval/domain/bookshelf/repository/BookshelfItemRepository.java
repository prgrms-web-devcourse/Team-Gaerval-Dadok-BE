package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;

public interface BookshelfItemRepository extends JpaRepository<BookshelfItem, Long> {

	Optional<BookshelfItem> findByBookshelfAndBook(Bookshelf bookshelf, Book Book);
}
